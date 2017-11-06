package com.example.android.booklisting;

import android.graphics.Bitmap;
import java.util.ArrayList;

/**
 * Custom Book object that stores the tile, author, description, thumbnail image and the url to
 * the book preview of the book.
 */
public class Book {

    private String title;
    private ArrayList<String> author;
    private String description;
    private Bitmap thumbnail;
    private String url;

    /* Constructor for a book object */
    public Book(String bTitle, ArrayList<String> bAuthor, String bDescription, Bitmap bThumbnail, String previewLink) {
        title = bTitle;
        author = bAuthor;
        description = bDescription;
        thumbnail = bThumbnail;
        url = previewLink;
    }

    /* Getters for book attributes */
    public String getTitle(){ return title; }
    public ArrayList<String> getAuthor() { return author; }
    public String getDescription() { return description; }
    public Bitmap getThumbnail() { return thumbnail; }
    public String getUrl() { return url; }

}
