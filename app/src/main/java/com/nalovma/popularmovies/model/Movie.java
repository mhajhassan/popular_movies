package com.nalovma.popularmovies.model;

public class Movie {
    private int id;
    private String title;
    private int voteCount;
    private double voteAverage;
    private String posterPath;
    private String backdropPath;
    private String plot;

    public Movie(int id, String title, int voteCount, double voteAverage, String posterPath, String backdropPath, String plot) {
        this.id = id;
        this.title = title;
        this.voteCount = voteCount;
        this.voteAverage = voteAverage;
        this.posterPath = posterPath;
        this.backdropPath = backdropPath;
        this.plot = plot;
    }

    public int getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public int getVoteCount() {
        return voteCount;
    }

    public double getVoteAverage() {
        return voteAverage;
    }

    public String getPosterPath() {
        return posterPath;
    }

    public String getBackdropPath() {
        return backdropPath;
    }

    public String getPlot() {
        return plot;
    }
}
