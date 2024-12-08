package com.guyi.class25a_sem_1.rest;

public class Movie {

    public enum GENRE {
        ACTION,
        HORROR,
        DRAMA,
        ANIMA,
        KIDS,
        COMEDY
    }


    private String title;
    private String image;
    private GENRE genre;
    private boolean inNetflix = false;
    private int duration; // minutes
    private double rating;

    public Movie() { }

    public String getTitle() {
        return title;
    }

    public Movie setTitle(String title) {
        this.title = title;
        return this;
    }

    public String getImage() {
        return image;
    }

    public Movie setImage(String image) {
        this.image = image;
        return this;
    }

    public GENRE getGenre() {
        return genre;
    }

    public Movie setGenre(GENRE genre) {
        this.genre = genre;
        return this;
    }

    public boolean isInNetflix() {
        return inNetflix;
    }

    public Movie setInNetflix(boolean inNetflix) {
        this.inNetflix = inNetflix;
        return this;
    }

    public int getDuration() {
        return duration;
    }

    public Movie setDuration(int duration) {
        this.duration = duration;
        return this;
    }

    public double getRating() {
        return rating;
    }

    public Movie setRating(double rating) {
        this.rating = rating;
        return this;
    }
}