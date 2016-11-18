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

    public double accelmagZ, accelmagX, accelmagY;
    public double gyroZ, gyroX, gyroY;
    public double zPos, xPos, yPos;


    public SensorHandler(Context context){
        s = new SensorData((SensorManager) context.getSystemService(Context.SENSOR_SERVICE), this);
        //hudFragment = new HUDFragment();
    }

    public void setAccelMagValues(float[] values){
        accelmagZ = values[0] * 180/Math.PI;
        accelmagX = values[1] * 180/Math.PI;
        accelmagY = -values[2] * 180/Math.PI;
    }
    public void setGyroValues(float[] values){
        gyroZ = values[0] * 180/Math.PI;
        gyroX = values[1] * 180/Math.PI;
        gyroY = -values[2] * 180/Math.PI;
    }
    public void setFusedValues(float[] values){
        zPos = values[0] * 180/Math.PI;
        xPos = values[1] * 180/Math.PI;
        yPos = -values[2] * 180/Math.PI;

    }

}
