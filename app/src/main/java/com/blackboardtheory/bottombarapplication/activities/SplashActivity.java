package com.blackboardtheory.bottombarapplication.activities;

import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.util.Log;
import android.widget.ImageView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.blackboardtheory.bottombarapplication.HelperFunctions;
import com.blackboardtheory.bottombarapplication.MyApplication;
import com.blackboardtheory.bottombarapplication.R;
import com.blackboardtheory.bottombarapplication.models.MediaCollection;
import com.blackboardtheory.bottombarapplication.models.Movie;

public class SplashActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        ImageView mAnimation = (ImageView) findViewById(R.id.animation);
        ((AnimationDrawable) mAnimation.getBackground()).start();
    }

    @Override
    protected void onStart() {
        super.onStart();
        new FetchCollections().execute();
    }

    private class FetchCollections extends AsyncTask<Void, Integer, List<MediaCollection>> {
        @Override
        protected List<MediaCollection> doInBackground(Void... params) {
            try {
                JSONArray genres = new JSONArray(HelperFunctions.getJSON(MyApplication.jsonSource));
                List<MediaCollection> mediaCollections = new ArrayList<>();
                for(int i = 0; i < genres.length(); i++) {
                    JSONObject genre = genres.getJSONObject(i);
                    JSONArray collection = genre.getJSONArray("mediaRefList");
                    if(collection.length() > 0) {
                        MediaCollection myGenre = new MediaCollection(genre.getString("title"),genre.getString("id"));
                        List<Movie> movieList = new ArrayList<>();
                        for(int j = 0; j < collection.length(); j++) {
                            movieList.add(Movie.makeMovie(collection.getJSONObject(j), SplashActivity.this.getApplicationContext()));
                        }
                        myGenre.setMovieList(movieList);
                        mediaCollections.add(myGenre);
                    }
                }

                return mediaCollections;

            } catch(IOException | JSONException e) {
                Log.d("Delta","Something went wrong");
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(List<MediaCollection> mediaCollections) {
            MyApplication.getInstance().setMedia(mediaCollections);
            Log.d("Delta","there are " + Integer.toString(mediaCollections.size()) + " collections");
            Intent mainIntent = new Intent(SplashActivity.this, MainActivity.class);
            startActivity(mainIntent);
        }


    }

}
