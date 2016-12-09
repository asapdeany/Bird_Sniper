package com.example.deansponholz.fish_game;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.TextView;

import java.io.InputStream;

/**
 * Created by deansponholz on 12/8/16.
 */

public class HighscoreActivity extends AppCompatActivity implements HighscoreTask.AsyncResponse {

    TextView highscore_textview;
    String help = "not";
    HighscoreTask highscoreTask;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_highscore);

        this.highscore_textview = (TextView) findViewById(R.id.highscore_textview);


        //highscoreTask.execute();
        new HighscoreTask(this).execute();




    }
    //this override the implemented method from AsyncResponse
    @Override
    public void processFinish(String output){
        //Here you will receive the result fired from async class
        //of onPostExecute(result) method.
        highscore_textview.setText(output);
    }

}
