package com.example.android.booklisting;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.text.TextUtils;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Constructor;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

/*
* Helper methods related to requesting and receiving book data from Google Books.
* The methods and variables are static which can be directly accessed from the class name Utils
*/

public final class Utils {

    /** Tag for the log messages */
    public static final String LOG_TAG = Utils.class.getSimpleName();

    /* Constructor */
    private Utils() {
    }

    /* Query Google Books dataset and return a list of Book objects */
    public static List<Book> fetchBookData(String stringUrl) throws IOException, JSONException {
        // Create URL object
        URL url = createNewUrl(stringUrl);

        // Perform HTTP request to the URL and receive  JSON response back
        String jsonResponse = null;
        try {
            jsonResponse = makeHttpConnection(url);
        } catch (IOException e){
            Log.e(LOG_TAG, "Error closing input stream", e);
        }

        // Extract relevant fields from the JSON response and create a Book object
        List<Book> books = extractFeatureFromJson(jsonResponse);

        // Return list of Book objects
        return books;
    }

    /* Method converts String to URL object */
    private static URL createNewUrl(String stringUrl)  {
        URL url = null;
        try {
            url = new URL(stringUrl);
        } catch (MalformedURLException e) {
            Log.e(LOG_TAG, "Error with creating URL ", e);
        }
        return url;
    }

    /* Method makes an HTTP request to the given URL and returns the JSON response as a string */
    private static String makeHttpConnection(URL url) throws IOException {
        String jsonResponse = "";

        // If the URL is null, then return early
        if (url == null) {
            return jsonResponse;
        }

        HttpURLConnection urlConnection = null;
        InputStream inputStream = null;

        try {
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setReadTimeout(10000 /* milliseconds */);
            urlConnection.setConnectTimeout(15000 /* milliseconds */);
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            // If the request was successful, ie the response code is 200, then read the input
            // stream and parse the response
            if (urlConnection.getResponseCode() == 200) {
                inputStream = urlConnection.getInputStream();
                jsonResponse = readFromStream(inputStream);
            } else {
                Log.e(LOG_TAG, "Error response code: " + urlConnection.getResponseCode());
            }
        } catch (IOException e) {
            Log.e(LOG_TAG, "Problem retrieving the book list JSON results.", e);
        } finally{
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (inputStream != null) {
                inputStream.close();
            }
        }
        return jsonResponse;
    }

    /* Method converts the InputStream into a String which has the whole JSON
    response from the server */
    private static String readFromStream(InputStream inputStream) throws IOException {
        StringBuilder output = new StringBuilder();
        if (inputStream != null) {
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream, Charset.forName("UTF-8"));
            BufferedReader reader = new BufferedReader(inputStreamReader);
            String line = reader.readLine();

            while (line != null) {
                output.append(line);
                line = reader.readLine();
            }
        }
        return output.toString();
    }

    /* Method parses the JSON response and returns a List of Book objects */
    private static List<Book> extractFeatureFromJson(String jsonResponse) {
        // If the JSON string is empty or null, return early
        if (TextUtils.isEmpty(jsonResponse)) {
            return null;
        }
        // Create an empty ArrayList to add Book objects to it
        List<Book> books = new ArrayList<>();

        // Parse the JSON response
        try {
            // Create JSON object from the JSON response string
            JSONObject root = new JSONObject(jsonResponse);

            // Extract the JSONArray with the key "items" which has the
            // list of books
            JSONArray itemsArray = root.getJSONArray("items");

            // If there are no book items in the JSON response, return early
            if(itemsArray == null){
                return null;
            }

            // Loop through items array, extract its info and create a Book object
            for (int i = 0; i<itemsArray.length(); i++) {
                JSONObject bookObject = itemsArray.getJSONObject(i);
                JSONObject volumeinfo = bookObject.getJSONObject("volumeInfo");
                String title = volumeinfo.getString("title");
                String description = volumeinfo.getString("description");
                String previewLink = volumeinfo.getString("previewLink");

                // Create ArrayList for authors of book since each book has
                // variable number of authors
                JSONArray authorsArray;
                ArrayList<String> authors=null;
                if(volumeinfo.isNull("authors")){
                    // do nothing
                } else {
                    authorsArray = volumeinfo.getJSONArray("authors");
                    authors = new ArrayList<>();
                    for (int j = 0; j < authorsArray.length(); j++) {
                        authors.add(authorsArray.getString(j));
                    }
                }

                // Create JSONObject for imageLinks for thumbnail of book
                JSONObject imageLinks = volumeinfo.getJSONObject("imageLinks");
                String thumbnailUrl = imageLinks.getString("smallThumbnail");

                // Create bitmap from URL of thumbnail of book
                Bitmap thumbnail = fetchThumbnail(thumbnailUrl);

                // Create new Book object from the info extracted from JSON response
                Book book = new Book(title, authors, description, thumbnail, previewLink);

                // Add book to ArrayList of books
                books.add(book);
            }
        } catch (JSONException e) {
                Log.e("QueryUtils", "Problem parsing the book JSON results", e);
        }
        return books;
    }

    /* Create Bitmap from String URL of thumbnail extracted from JSON response */
    private static Bitmap fetchThumbnail(String thumbnailUrl) {
        Bitmap thumbnail = null;
        try {
            InputStream in = new java.net.URL(thumbnailUrl).openStream();
            thumbnail = BitmapFactory.decodeStream(in);
        } catch (Exception e){
            Log.e(LOG_TAG, "Error getting image ", e);
        }
        return thumbnail;
    }
}
