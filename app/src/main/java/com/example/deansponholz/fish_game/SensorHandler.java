package com.example.deansponholz.fish_game;

import android.content.Context;
import android.hardware.SensorManager;
import android.widget.Button;
import android.widget.TextView;

/**
 * Created by deansponholz on 11/12/16.
 */

public class SensorHandler {

    SensorData s = null;
    //HUDFragment hudFragment = null;
    public double zPos, xPos, yPos;




    public SensorHandler(Context context){
        s = new SensorData((SensorManager) context.getSystemService(Context.SENSOR_SERVICE), this);
        //hudFragment = new HUDFragment();
    }

    public void setValues(float[] values){
        zPos = values[0] * 180/Math.PI;
        xPos = values[1] * 180/Math.PI;
        yPos = -values[2] * 180/Math.PI;

    }
}
