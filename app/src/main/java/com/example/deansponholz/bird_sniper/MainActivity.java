package com.example.deansponholz.bird_sniper;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.lang.reflect.Array;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    TextView yaw_test = null;
    TextView pitch_test = null;
    TextView roll_test = null;

    Sensor accelerometer = null;
    Sensor gyroscope = null;
    Sensor magnetic = null;

    SensorManager mSensorManager = null;
    SensorData s = null;
    long prev_time = 0;

    RelativeLayout rl = null;
    public double xPos, yPos;

    ArrayList<Float> temparray = new ArrayList<>(2);
    int i = 0;



    int size;
    float tempx;
    float x = 260;
    float y = 500;
    int width;
    int height;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        s = new SensorData((SensorManager)getSystemService(SENSOR_SERVICE), this);
        RelativeLayout rl = (RelativeLayout) findViewById(R.id.activity_main);
        MyDrawView myDrawing = new MyDrawView(this);
        rl.addView(myDrawing);
        yaw_test = (TextView) findViewById(R.id.yaw_test);
        pitch_test = (TextView) findViewById(R.id.pitch_test);
        roll_test = (TextView) findViewById(R.id.roll_test);
    }



    protected void setTextViewValue(float[] values){

        xPos = values[1] * 180/Math.PI;
        yPos = -values[2] * 180/Math.PI;
        //getValues();
        yaw_test.setText(Double.toString(values[0] * 180/Math.PI));
        pitch_test.setText(Double.toString(values[1] * 180/Math.PI));
        roll_test.setText(Double.toString(values[2] * 180/Math.PI));
        //Log.d("bro", ( Double.toString(yaw)));
        //yaw_test.setX((float)xPos);

    }

    public class MyDrawView extends View {
        public MyDrawView(Context context){super(context);}

        @Override
        public void onDraw(Canvas canvas){

            Paint p = new Paint();
            Bitmap b = BitmapFactory.decodeResource(getResources(), R.drawable.yellowfish);
            Matrix matrix = new Matrix();
            matrix.preScale(1.0f, -1.0f);
            matrix.postTranslate(canvas.getWidth(), 0);




            Bitmap resized = Bitmap.createScaledBitmap(b, 120, 100, false);

            //p.setColor(Color.MAGENTA);
            //size = 50;
            //canvas.drawBitmap(resized, x + 530, y + 300, p);


            //canvas.scale(-1f, 1f);
            //canvas.drawCircle(x + 600, y + 320, size, p);

            x = (float) (-xPos*15);
            //Log.d("x", Float.toString(x));
            y = (float) (yPos * 15);


            temparray.add(x);



/*
            if (x > 0){

                tempx = x;

                //Log.d("x", Float.toString(tempx));
                canvas.drawBitmap(flip(resized ,2), x + 530, y + 300, p);
                tempx = x;
            }
            else if (x < 0){
                canvas.drawBitmap(resized, x + 530, y + 300, p);
            }
            */

            i++;

            if (i % 2 == 0){
                float testfloat = temparray.get(0);
                float testfloat2 = temparray.get(1);

                if (testfloat > testfloat2){
                    Log.d("x", Float.toString(testfloat));
                    Log.d("x2", Float.toString(testfloat2));
                    canvas.drawBitmap(flip(resized ,2), x + 530, y + 300, p);
                    temparray.clear();
                }
                else {
                    //canvas.drawBitmap(flip(resized ,2), x + 530, y + 300, p);
                    temparray.clear();
                }
            }




            /*
            if(topCollision(y))
            {
                y=size - 640;
            }

            if(leftCollision(x))
            {
                x=size -360;
            }


            if(rightCollision(x))
            {
                x=((width-310) - size);
            }

            if(bottomCollision(y))
            {
                y = height-size -550;
            }
            */


            invalidate();


        }

    }

    // type definition
    public static final int FLIP_VERTICAL = 1;
    public static final int FLIP_HORIZONTAL = 2;

    public Bitmap flip(Bitmap src, int type) {
        // create new matrix for transformation
        Matrix matrix = new Matrix();
        // if vertical
        if(type == FLIP_VERTICAL) {
            // y = y * -1
            matrix.preScale(1.0f, -1.0f);
        }
        // if horizonal
        else if(type == FLIP_HORIZONTAL) {
            // x = x * -1
            matrix.preScale(-1.0f, 1.0f);
            // unknown type
        } else {
            return null;
        }
        //matrix.postRotate((float)(yPos * 4.5));

        // return transformed image
        return Bitmap.createBitmap(src, 0, 0, src.getWidth(), src.getHeight(), matrix, true);
    }

    //Checks top of screen collision or if ball goes past
    private boolean topCollision(float y)
    {
        if(y-(size - 640) <=0)
        {
            return true;
        }
        else
        {
            return false;
        }
    }
    //Checks left of screen collision or if ball goes past
    private boolean leftCollision(float x)
    {
        if((x+360) - size<=0)
        {
            return true;
        }
        else
        {
            return false;
        }
    }
    //Checks right of screen collision or if ball goes past
    private boolean rightCollision(float x)
    {
        if((x)+size>=width -310)
        {
            return true;
        }
        else
        {
            return false;
        }
    }
    //Checks bottom of screen collision or if ball goes past
    private boolean bottomCollision(float y)
    {
        if(y+size>=height-550)
        {
            return true;
        }
        else
        {
            return false;
        }
    }

    protected double getValues(){
        return 5;
        //return yaw;
    }
    protected void setValues(double y){
        //yaw = y;
        //Log.d("bro", Double.toString(yaw));
    }
}
