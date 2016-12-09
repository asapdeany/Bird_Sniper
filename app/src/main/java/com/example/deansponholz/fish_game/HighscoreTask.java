package com.example.deansponholz.fish_game;

import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

/**
 * Created by deansponholz on 12/8/16.
 */

public class HighscoreTask extends AsyncTask {

    public AsyncResponse delegate = null;

    public interface AsyncResponse {
        void processFinish(String output);
    }

    public HighscoreTask(AsyncResponse delegate){
        this.delegate = delegate;
    }


    StringBuilder sb = null;
    String result = null;

    @Override
    protected Object doInBackground(Object[] params) {

        try{

            String link="http://cs-linuxlab-30.stlawu.local";
            URL url = new URL(link);
            URLConnection urlConnection = url.openConnection();
            BufferedReader reader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
            sb = new StringBuilder();
            String line = null;

            while ((line = reader.readLine()) != null){
                sb.append(line);

                //break;
            }
            return sb.toString();
        }catch (Exception e){
            Log.d("FAILED", "failed");
            return new String(e.getMessage());
        }
    }


    @Override
    protected void onPostExecute(Object o) {
        result = sb.toString();
        delegate.processFinish(result);

    }
}
