package com.example.android.booklisting;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import java.util.ArrayList;

/*
* Custom adapter that takes in Book objects and populates it on ListView
* */
public class BookAdapter extends ArrayAdapter<Book> {

    // Constructor
    public BookAdapter(Context context,  ArrayList<Book> books) {
        super(context, 0, books);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        // get the object located at this position of the list
        Book currentBook = getItem(position);

        // Check if existing view is being reused, otherwise inflate the view
        View listItemView = convertView;
        if(listItemView == null) {
            listItemView = LayoutInflater.from(getContext()).inflate(R.layout.list_item, parent, false);
            }

        // Find the relevant textview from the list_item.xml for title of book and
        // set the text to the book's title
        TextView titleView = (TextView) listItemView.findViewById(R.id.title);
        titleView.setText(currentBook.getTitle());

        // Find the textview of the author for the book, check if there are authors, then create one
        // string which includes all authors and update textview to that value. If there are no authors,
        // authors is an empty string
        TextView authorView = (TextView) listItemView.findViewById(R.id.author);
        String authors="";
        if (currentBook.getAuthor()!= null){
            authors = "by \n";
            for (int i=0; i<currentBook.getAuthor().size(); i++) {
                authors += currentBook.getAuthor().get(i) + "\n";
            }
        }
        authorView.setText(authors);

        // Find ImageView in list_item.xml and set it to the Bitmap image of the book
        ImageView imageView = (ImageView) listItemView.findViewById(R.id.image);
        imageView.setImageBitmap(currentBook.getThumbnail());

        return listItemView;
    }
}
