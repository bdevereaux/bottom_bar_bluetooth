package com.blackboardtheory.bottombarapplication;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.annotation.Nullable;
import android.util.Log;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;

/**
 * Created by bdevereaux3 on 10/26/16.
 */

public class HelperFunctions {

    public static String getJSON(String urlString) throws IOException {
        URL url;
        StringBuilder sb = new StringBuilder();
        try {
            url = new URL(urlString);
        } catch(MalformedURLException e) {
            try {
                url = new URL("http://webdev.thefifthace.net/collections.json");//http://airbornemedia.gogoinflight.com/asp/api/media/en-US/collections
            } catch(MalformedURLException e1) {
                return "";
            }
        }

        InputStream is = url.openStream();
        try {
            BufferedReader rd = new BufferedReader(new InputStreamReader(is, Charset.forName("UTF-8")));
            String sCurrentLine;
            while ((sCurrentLine = rd.readLine()) != null) {
                sb.append(sCurrentLine);
            }
        } finally {
            is.close();
        }

        return sb.toString();
    }

    @Nullable
    public static Bitmap fetchBitmap(String urlSrc) {
        try {
            URL url = new URL(urlSrc);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.connect();
            InputStream input = connection.getInputStream();
            Bitmap myBitmap = BitmapFactory.decodeStream(input);
            return myBitmap;
        } catch (IOException e) {
            // Log exception
            return null;
        }
    }

    @Nullable
    public static Bitmap loadBitmap(String mediaID, Context context) {
        mediaID = mediaID.toLowerCase().replaceAll("\\s","_");
        String filename = mediaID + "_boxart.jpeg";
        if(fileExists(filename, context)) {
            // if the file exists, we don't add it again
            File file = context.getFileStreamPath(filename);
            try {
                return BitmapFactory.decodeStream(new FileInputStream(file));
            } catch (FileNotFoundException e) {
                e.printStackTrace();
                return null;
            }

        }
        else {
            return null;
        }
    }

    /**
     * Checks to see if the boxart for a given piece of media exists and creates it if it doesn't
     * @param mediaID The unique media ID of the piece of media whose box art we are saving
     * @param boxArt A bitmap representation of the media box art
     */
    public static void writeBoxArt(String mediaID, Bitmap boxArt, Context context) {
        FileOutputStream ops = null;
        mediaID = mediaID.toLowerCase().replaceAll("\\s","_");
        String filename = mediaID + "_boxart.jpeg";

        if(!fileExists(filename, context)) {
            try {
                Log.d("Delta", "Saving box art to disk");
                ops = context.openFileOutput(filename, Context.MODE_PRIVATE);
                boxArt.compress(Bitmap.CompressFormat.JPEG, 100, ops);
                ops.close();
            } catch (IOException e) {
                Log.e("Error", e.getMessage());
                e.printStackTrace();
            }
        }
    }

    public static boolean fileExists(String filename, Context context) {
        File file = context.getFileStreamPath(filename);
        return file.exists();
    }
}
