package com.blackboardtheory.bottombarapplication.models;

import java.util.List;

/**
 * Created by bdevereaux3 on 10/26/16.
 */

public class MediaCollection {



    private String genre;
    private String id;
    private List<Movie> movieList;

    public MediaCollection(String genre, String id) {
        this.genre = genre;
        this.id = id;
    }

    public String getGenre() {
        return genre;
    }

    public void setGenre(String genre) {
        this.genre = genre;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public List<Movie> getMovieList() {
        return movieList;
    }

    public void setMovieList(List<Movie> movieList) {
        this.movieList = movieList;
    }


}
