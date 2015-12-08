package com.popcorn.data;

public class Suggestion {

    private String title;
    private int rating;

    public Suggestion(String title, int rating) {
        this.title = title;
        this.rating = rating;
    }

    public String getTitle() {
        return title;
    }

    public int getRating() {
        return rating;
    }
}
