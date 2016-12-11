package com.example.deansponholz.fish_game;

import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
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


    StringBuilder sb;
    String result;
    URL url;
    HttpURLConnection urlConnection;

    @Override
    protected Object doInBackground(Object[] params) {

        try{
            //String highscoreTxt = "http://cs-linuxlab-30.stlawu.local/scoreline.txt";
            //String highscorePhp = "http://cs-linuxlab-30.stlawu.local/test.php";
            String highscoreTxt  = "http://cs-linuxlab-30.stlawu.local/db_php.php";
            url = new URL(highscoreTxt);
        } catch (MalformedURLException e){
            e.printStackTrace();
            return e.toString();
        }

        try {

            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.setDoOutput(true);
        }catch (IOException e1) {
            e1.printStackTrace();
            return e1.toString();
        }
        try{
            int response_code = urlConnection.getResponseCode();

            if (response_code == HttpURLConnection.HTTP_OK) {

                // Read data sent from server
                InputStream input = urlConnection.getInputStream();
                BufferedReader reader = new BufferedReader(new InputStreamReader(input));
                String line;
                sb = new StringBuilder();

                while ((line = reader.readLine()) != null) {
                    if (line.startsWith("<li>")){
                        //Log.d("test", line);
                        sb.append(line.substring(4, line.length()-5) + "\n");
                    }

                }

                // Pass data to onPostExecute method
                return (sb.toString());

            } else {

                return ("unsuccessful");
            }

        } catch (IOException e) {
            e.printStackTrace();
            return e.toString();
        } finally {
            urlConnection.disconnect();
        }

    }

    @Override
    protected void onPostExecute(Object o) {
        result = sb.toString();
        delegate.processFinish(result);

    }
}
