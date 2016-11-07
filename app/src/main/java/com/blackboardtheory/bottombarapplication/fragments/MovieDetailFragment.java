package com.blackboardtheory.bottombarapplication.fragments;


import android.graphics.Bitmap;
import android.os.Bundle;
import android.app.Fragment;
import android.os.Message;
import android.util.LruCache;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import com.blackboardtheory.bottombarapplication.MyApplication;
import com.blackboardtheory.bottombarapplication.R;

/**
 * Fragment for displaying our movie details and bluetooth controller
 */
public class MovieDetailFragment extends Fragment implements View.OnClickListener, View.OnLongClickListener {
    private MyApplication app;

    private RequestQueue mRequestQueue;
    private ImageLoader mImageLoader;

    private String movieID, movieTitle;

    private ImageView button;

    public MovieDetailFragment() {
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
        app = MyApplication.getInstance();
        Bundle bundle = this.getArguments();
        getActivity().getActionBar().setTitle(bundle.getString("title"));
        View view = inflater.inflate(R.layout.view_movie_detail, container, false);

        TextView title = (TextView)view.findViewById(R.id.movie_title);
        title.setText(bundle.getString("title"));
        this.movieTitle = bundle.getString("title");

        TextView synopsis = (TextView)view.findViewById(R.id.synopsis);
        synopsis.setText(bundle.getString("synopsis"));

        this.movieID = bundle.getString("id");

        NetworkImageView poster = (NetworkImageView)view.findViewById(R.id.movie_image);
        TextView myListButton = (TextView)view.findViewById(R.id.my_list_button);
        RelativeLayout controller = (RelativeLayout)view.findViewById(R.id.controller);

        /*Control our "logged in only visibility"*/
        myListButton.setVisibility("Guest".equals(app.getUserID()) ? View.INVISIBLE : View.VISIBLE);
        controller.setVisibility(app.isConnected() ? View.VISIBLE : View.INVISIBLE);

        poster.setImageUrl(bundle.getString("boxArt"), mImageLoader);

        button = (ImageView)view.findViewById(R.id.play_pause_button);
        ImageView rewind = (ImageView)view.findViewById(R.id.rewind_button);
        ImageView fast_forward = (ImageView)view.findViewById(R.id.fast_forward_button);
        ImageView volume_up = (ImageView)view.findViewById(R.id.volume_up_button);
        ImageView volume_down = (ImageView)view.findViewById(R.id.volume_down_button);
        TextView myList = (TextView)view.findViewById(R.id.my_list_button);

        if(this.movieID.equals(MyApplication.getInstance().getActiveMovieID())) {
            if(MyApplication.isPlaying) {
                button.setImageResource(R.drawable.pause_button);
            }
        }

        button.setOnClickListener(this);
        rewind.setOnClickListener(this);
        fast_forward.setOnClickListener(this);
        volume_up.setOnClickListener(this);
        volume_down.setOnClickListener(this);
        myList.setOnClickListener(this);
        myList.setOnLongClickListener(this);

        return view;
    }


    @Override
    public void onClick(View v) {
        switch(v.getId()) {
            case R.id.play_pause_button:
                if(MyApplication.isPlaying && this.movieID.equals(MyApplication.getInstance().getActiveMovieID())) {// This is the movie currently playing
                    sendBluetoothCommand("pause", null);
                    MyApplication.getInstance().pause();
                    button.setImageResource(R.drawable.play_button);
                }
                else {
                    sendBluetoothCommand("play", this.movieID);
                    MyApplication.getInstance().setActiveMovieID(this.movieID);
                    MyApplication.isPlaying = true;
                    button.setImageResource(R.drawable.pause_button);

                }
                break;
            case R.id.fast_forward_button:
                sendBluetoothCommand("fast_forward", null);
                break;
            case R.id.rewind_button:
                sendBluetoothCommand("rewind", null);
                break;
            case R.id.volume_down_button:
                sendBluetoothCommand("volume_down", null);
                break;
            case R.id.volume_up_button:
                sendBluetoothCommand("volume_up", null);
                break;
            case R.id.my_list_button:
                addToList(MyApplication.getInstance().getUserID());
                Toast.makeText(getActivity(), movieTitle + " added to my watch list!", Toast.LENGTH_LONG).show();
                break;
        }
    }

    private void sendBluetoothCommand(String command, String id) {
        Bundle message = new Bundle();
        message.putString("message",command);
        if(id != null) {
            message.putString("movieID", id);
        }
        Message handlerMessage = new Message();
        handlerMessage.setData(message);
        MyApplication.getInstance().getHandler().sendMessage(handlerMessage);
    }

    private void addToList(String userID) {
        RequestQueue queue = Volley.newRequestQueue(this.getActivity());
        String url = "http://"+MyApplication.getServerAddress()+"/movie?id="+(this.movieID)+"&userId="+userID;

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
    }

    @Override
    public boolean onLongClick(View v) {
        switch(v.getId()) {
            case R.id.my_list_button:
                if("DAL0002".equals(MyApplication.getInstance().getUserID())) {
                    addToList("DAL0003");
                    Toast.makeText(getActivity(), movieTitle + " added to child's watch list!", Toast.LENGTH_LONG).show();
                }
                break;
        }
        return true;
    }
}
