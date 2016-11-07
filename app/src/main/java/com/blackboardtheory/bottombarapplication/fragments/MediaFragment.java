package com.blackboardtheory.bottombarapplication.fragments;


import android.app.Dialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.transition.ChangeBounds;
import android.transition.ChangeImageTransform;
import android.transition.ChangeTransform;
import android.transition.TransitionSet;
import android.util.Log;
import android.util.LruCache;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.Volley;

import java.util.List;

import com.blackboardtheory.bottombarapplication.models.MediaCollection;
import com.blackboardtheory.bottombarapplication.models.Movie;
import com.blackboardtheory.bottombarapplication.MyApplication;
import com.blackboardtheory.bottombarapplication.R;


public class MediaFragment extends Fragment {

    private RequestQueue mRequestQueue;
    private ImageLoader mImageLoader;


    public MediaFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mRequestQueue = Volley.newRequestQueue(getContext());
        mImageLoader = new ImageLoader(mRequestQueue, new ImageLoader.ImageCache() {

            private final LruCache<String, Bitmap> mCache = new LruCache<String, Bitmap>(10);

            public void putBitmap(String url, Bitmap bitmap) {
                mCache.put(url, bitmap);
            }

            public Bitmap getBitmap(String url) {
                return mCache.get(url);
            }
        });
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        getActivity().getActionBar().setTitle("Entertainment");
        View view = inflater.inflate(R.layout.view_media, container, false);


        LinearLayout parent = (LinearLayout)view.findViewById(R.id.media_content);

        // This is where we will fetch all of our media and display it in scrollers
        List<MediaCollection> genres = MyApplication.getInstance().getMedia();
        for(int i = 0; i < genres.size(); i++) {
            View movieScroller = inflater.inflate(R.layout.movie_scroller, parent, false);
            TextView genreTitle = (TextView)movieScroller.findViewById(R.id.section_title_text_view);
            genreTitle.setText(genres.get(i).getGenre());
            genreTitle.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                }
            });

            LinearLayout scrollLayout = (LinearLayout)movieScroller.findViewById(R.id.movie_layout);
            final List<Movie> movies = genres.get(i).getMovieList();
            for(int j = 0; j < movies.size(); j++) {
                final int k = j;
                View moviePoster = movies.get(j).createView(inflater, mImageLoader);
                moviePoster.setTag(movies.get(j));
                moviePoster.setOnClickListener(new View.OnClickListener(){
                    public void onClick(View view) {
                        MediaFragment.this.onClick(view);
                        //need to assign the active movie here and open the new fragment
                    }
                });
                /*moviePoster.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        final Dialog dialog = new Dialog(MediaFragment.this.getActivity());
                        dialog.setContentView(R.layout.restrict_layout);
                        dialog.setTitle("Developer Options");

                        Button noButton = (Button)dialog.findViewById(R.id.dialogButtonNo);
                        Button yesButton = (Button)dialog.findViewById(R.id.dialogButtonYes);

                        noButton.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {

                                dialog.dismiss();
                            }
                        });

                        yesButton.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                // Instantiate the RequestQueue.
                                RequestQueue queue = Volley.newRequestQueue(MediaFragment.this.getActivity());
                                String url = "http://"+MyApplication.getServerAddress()+"/movieRestriction?id="+(movies.get(k).getId())+"&userId=DAL0003";

                                StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                                        new Response.Listener<String>() {
                                            @Override
                                            public void onResponse(String response) {
                                            }
                                        }, new Response.ErrorListener() {
                                            @Override
                                            public void onErrorResponse(VolleyError error) {

                                            }
                                        }
                                );

                                queue.add(stringRequest);
                                dialog.dismiss();
                            }
                        });

                        dialog.show();
                        return false;
                    }
                });*/
                scrollLayout.addView(moviePoster);
            }
            parent.addView(movieScroller);
        }

        return view;
    }

    private void onClick(View view) {
        Log.d("MyApp",((Movie)view.getTag()).getTitle());//This works

        MovieDetailFragment detailFragment = new MovieDetailFragment();

        /*detailFragment.setSharedElementEnterTransition(new DetailsTransition());
        detailFragment.setEnterTransition(new Fade());
        setExitTransition(new Fade());
        detailFragment.setSharedElementReturnTransition(new DetailsTransition());*/

        Movie tag = ((Movie)view.getTag());
        Bundle movieBundle = new Bundle();
        movieBundle.putString("title",tag.getTitle());
        movieBundle.putString("boxArt",tag.getBoxArt());
        movieBundle.putString("synopsis",tag.getSynopsis());
        movieBundle.putString("id",tag.getId());

        detailFragment.setArguments(movieBundle);


        //need the main activity to switch fragments
        FragmentManager fm = this.getActivity().getFragmentManager();
        FragmentTransaction transaction = fm.beginTransaction();

        view.setTransitionName(((Movie)view.getTag()).getTitle());

        transaction
                /*.addSharedElement(view, "poster")*/
                .replace(R.id.view_container, detailFragment)
                .addToBackStack(null)
                .commit();
    }

    public class DetailsTransition extends TransitionSet {
        public DetailsTransition() {
            setOrdering(ORDERING_TOGETHER);
            addTransition(new ChangeBounds()).
                    addTransition(new ChangeTransform()).
                    addTransition(new ChangeImageTransform());
        }
    }
}
