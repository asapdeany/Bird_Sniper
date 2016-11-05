package com.example.deansponholz.bird_sniper;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.hardware.SensorManager;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import static android.content.ContentValues.TAG;
import static android.content.Context.SENSOR_SERVICE;

/**
 * Created by deansponholz on 11/3/16.
 */

public class Rectangle extends View {
    Paint paint = new Paint();
    MainActivity ma = new MainActivity();

    public Rectangle(Context context) {
        super(context);
    }

    public Rectangle(Context context, AttributeSet attrs) {
        super(context, attrs);
        //init(context);
    }

    public Rectangle(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        //init(context);
    }

    @Override
    public void onDraw(Canvas canvas) {
        //paint.setColor(Color.GREEN);
        //paint.setStyle(Paint.Style.STROKE);
        //Rect rect = new Rect(50, 50 ,50, 50);
        //Rect rect = new Rect(0 + 5, 0, getWidth()-5, getHeight()-5);
        //canvas.drawRect(rect, paint);

        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(4);
        paint.setColor(Color.MAGENTA);
        //draw a circle at the point designated based on accelerometer data and previous points with a specified size and a color P
        //canvas.drawCircle(getWidth()/2, getHeight()/2, 50, paint);
        //canvas.drawCircle((float)ma.yaw, getHeight() / 2, 50, paint);
        //Log.d(TAG, Float.toString(sd.x));
        //Log.d(TAG, Integer.toString(canvas.getWidth()));

    }
}
