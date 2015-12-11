package com.popcorn.data;

/**
 * This is a class the defines data structure for Movie object.
 * The class is used in Review Fragment inside AutoCompleteTextView's ArrayAdapter
 */
public class Movie {

    private String title;
    private long id;
    private int year;

    public Movie(long id, String title, int year) {
        this.id = id;
        this.title = title;
        this.year = year;
    }

    public String getTitle() {
        return title;
    }

    public long getId() {
        return id;
    }

    public int getYear() {
        return year;
    }

    @Override
    public String toString() {
        return String.format("%s (%d)", title, year);
    }

}
