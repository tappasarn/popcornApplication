package com.popcorn.data;

public class Suggestion {

    private String title, genre, plot;
    private int rating;

    public Suggestion(String title, int rating, String genre, String plot) {
        this.title = title;
        this.rating = rating;
        this.genre = genre;
        this.plot = plot;
    }

    public String getGenre() {
        return genre;
    }

    public String getPlot() {
        return plot;
    }

    public String getTitle() {
        return title;
    }

    public int getRating() {
        return rating;
    }
}
