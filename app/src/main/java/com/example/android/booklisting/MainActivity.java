package com.example.android.booklisting;

import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import org.json.JSONException;
import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

/* User searches for a keyword, and a list of books for that keyword are displayed in a list */
public class MainActivity extends AppCompatActivity {

    private BookAdapter bookAdapter;

    /* The beginning part of URL for books data from Google Books. The keyword will be appended at the end */
    private static final String GOOGLE_BOOKS_URL_INITIAL = "https://www.googleapis.com/books/v1/volumes?q=";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        /* Create the search button and edit text for user to input keyword */
        Button searchButton = (Button) findViewById(R.id.search_button);
        final EditText keywordEditText = (EditText) findViewById(R.id.keyword_text);

        /* ClickListener for button saves keyword entered by user */
        searchButton.setOnClickListener( new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                /* Get the keyword entered by user and clear the search textfield */
                String keyword1 = keywordEditText.getText().toString();
                String keyword = keyword1.replaceAll("\\s+","");
                keywordEditText.setText("");

                /* Append first part of google books url to the keyword entered by user*/
                String requestUrl = GOOGLE_BOOKS_URL_INITIAL + keyword;

                /* Create an AsyncTask to perform HTTP request on background thread and update UI
                 * on main UI thread */
                BookListAsyncTask bookListTask = new BookListAsyncTask();
                bookListTask.execute(requestUrl);
            }
        });

        /* Find the ListView in the layout, create a new adapter and set the adapter to the ListView */
        ListView bookListView = (ListView) findViewById(R.id.list);
        bookAdapter = new BookAdapter(this, new ArrayList<Book>());
        bookListView.setAdapter(bookAdapter);

        /* Set a listener on the ListView to send an intent to a web browser to open the preview page of
         * the book */
        bookListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                /* Find the current book that was clicked on */
                Book currentBook = bookAdapter.getItem(position);

                /* Convert the String URL into a URI object (to pass into the Intent constructor) */
                Uri bookUri = Uri.parse(currentBook.getUrl());

                /* Create a new intent to view the book URI */
                Intent websiteIntent = new Intent(Intent.ACTION_VIEW, bookUri);

                /* Send the intent to launch a new activity */
                startActivity(websiteIntent);
            }
        });
    }

    /*
    * AsyncTask class that performs HTTP request on background thread and updates UI on main UI thread
    * */
    private class BookListAsyncTask extends AsyncTask<String, Void, List<Book>> {

        /* This method runs on background thread, calls Utils helper methods to
        * to get List of Book objects */
        @Override
        protected List<Book> doInBackground(String... url) {
            // Don't perform request if there are no URL or URL is null
            if (url.length < 1 || url[0] == null) {
                return null;
            }
            List<Book> books = null;
            try {
                books = Utils.fetchBookData(url[0]);
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return books;
        }

        /* This method runs on main UI thread to update adapter and repopulate list items */
        @Override
        protected void onPostExecute(List<Book> books) {
            // Clear the adapter of previous book data
            bookAdapter.clear();
            TextView noResultsView = (TextView) findViewById(R.id.no_results);

            // If there is a valid list of books, then add them to the adapter's
            // data set. This will trigger the ListView to update. If no results are found, then
            // inform user to enter a new keyword.
            if (books != null && !books.isEmpty()) {
                noResultsView.setText("");
                bookAdapter.addAll(books);
            } else {
                noResultsView.setText("No results found.\nEnter a new keyword");
            }
        }
    }
}
