package com.popcorn.data;

public class Movie {

    private String title;
    private long id;
    private int year;

    public Movie(long id, String title, int year) {
        this.id = id;
        this.title = title;
        this.year = year;
    }

    @Override
    public String toString() {
        return String.format("%s (%d)", title, year);
    }
}
