package com.blackboardtheory.bottombarapplication.models;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;


import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import com.blackboardtheory.bottombarapplication.R;

/**
 * Created by bdevereaux3 on 10/3/16.
 */

public class Movie {



    private String title, id, synopsis, boxArt, rating;
    private Context context;

    public Movie(String title, String id, String synopsis, String boxArt, String rating, Context context) {
        this.title = title;
        this.id = id;
        this.synopsis = synopsis;
        this.boxArt = boxArt;
        this.rating = rating;
        this.context = context;
    }


    public View createView(LayoutInflater inflater, ImageLoader imageLoader) {
        View view = inflater.inflate(R.layout.movie_poster, null);
        NetworkImageView poster = (NetworkImageView)view.findViewById(R.id.movie_image);
        poster.setImageUrl(this.boxArt, imageLoader);
        return view;
    }


    public String getTitle() {
        return this.title;
    }

    public String getId() {
        return this.id;
    }

    public String getSynopsis() {
        return this.synopsis;
    }

    public String getBoxArt() {
        return this.boxArt;
    }

    public String getRating() {
        return this.rating;
    }

    public Context getContext() {
        return this.context;
    }

    public static Movie makeMovie(JSONObject movieJSON, Context context) throws JSONException{
        String id = movieJSON.getString("id");
        String title = movieJSON.getString("title");
        String synopsis = movieJSON.getString("synopsis");
        String boxArt = movieJSON.getString("boxArtImage");
        String rating = null;
        if(movieJSON.has("mpaaRating") && !movieJSON.isNull("mpaaRating")) {
            rating = movieJSON.getJSONObject("mpaaRating").getString("rating");
        }
        else {
            rating = "unknown";
        }

        return new Movie(title, id, synopsis, boxArt, rating, context);
    }


}
