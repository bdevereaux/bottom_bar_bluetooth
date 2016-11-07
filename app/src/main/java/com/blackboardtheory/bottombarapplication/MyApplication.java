package com.blackboardtheory.bottombarapplication;

import android.app.Application;
import android.os.Handler;

import java.util.List;

import com.blackboardtheory.bottombarapplication.models.MediaCollection;

/**
 * Created by bdevereaux3 on 10/21/16.
 */

public class MyApplication extends Application {

    private Handler.Callback realCallback;
    private Handler handler;
    private static MyApplication instance;
    private static String userID;
    private List<MediaCollection> media;
    private boolean connected;
    private String activeMovieID;

    public static boolean isPlaying = false;
    public static boolean restrictionShown = false;

    public static String externalSource = "http://webdev.thefifthace.net/collections.json";

    public static String jsonSource = externalSource;

    public static String myAddress = "172.19.131.177";
    public static String myPort = "9000";

    private MyApplication() {
        realCallback = null;
        handler = new Handler() {
            public void handleMessage(android.os.Message msg) {
                if(realCallback != null) {
                    realCallback.handleMessage(msg);
                }
            }
        };
    }

    public static MyApplication getInstance() {
        if(instance == null) {
            initInstance();
        }
        return instance;
    }

    private static void initInstance() {
        instance = new MyApplication();
        instance.setConnected(false);
        instance.initMovieID("testing");
    }

    public Handler getHandler() {
        return handler;
    }

    public void setCallback(Handler.Callback callback) {
        realCallback = callback;
    }

    public void setUserID(String id) {
        this.userID = id;
    }

    public String getUserID() {
        return this.userID;
    }

    public void setMedia(List<MediaCollection> media) {
        this.media = media;
    }

    public List<MediaCollection> getMedia() {
        return this.media;
    }

    public void setConnected(boolean connected) {
        this.connected = connected;
    }

    public boolean isConnected() {
        return connected;
    }

    public static String getServerAddress() {
        return myAddress+":"+myPort;
    }

    public String getActiveMovieID() {
        return this.activeMovieID;
    }

    public void setActiveMovieID(String id) {
        if(!this.activeMovieID.equals(id)) {//new movie
            isPlaying = true;
        }
        this.activeMovieID = id;
    }

    public void pause() {
        isPlaying = false;
    }

    public void initMovieID(String id) {
        this.activeMovieID = id;
    }

}
