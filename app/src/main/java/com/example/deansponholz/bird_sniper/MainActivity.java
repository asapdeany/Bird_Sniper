package com.example.deansponholz.bird_sniper;

import android.app.Fragment;
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
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    TextView yaw_test = null;
    TextView pitch_test = null;
    TextView roll_test = null;
    Button calibrate_button = null;
    Button decalibrate_buton = null;

    SensorData s = null;


    RelativeLayout rl = null;
    public double xPos, yPos;

    List<Float> temparray = new ArrayList<Float>();

    int i = 0;

    boolean test = true;

    float yOffset = 300;
    int size;
    float tempx = 0;
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
        calibrate_button = (Button) findViewById(R.id.calibration_button);
        decalibrate_buton = (Button) findViewById(R.id.decalibrate_button);


        calibrate_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                yOffset = y - 292;
            }
        });

        decalibrate_buton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                yOffset = 300;
            }
        });


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

        Paint p = new Paint();
        Bitmap b = BitmapFactory.decodeResource(getResources(), R.drawable.yellowfish);

        Bitmap resized = Bitmap.createScaledBitmap(b, 90, 70, false);
        Bitmap flipped = Bitmap.createBitmap(flip(resized, 2));

        public MyDrawView(Context context){super(context);}

        @Override
        public void onDraw(Canvas canvas){

            x = (float) (-xPos*15);
            y = (float) (yPos * 15);

            Log.d("y", Float.toString(y));

            if (x > tempx) {
                canvas.drawBitmap(flipped, x + 530, y + yOffset, p);
                tempx = x;
            }

            else if (x < tempx){
                canvas.drawBitmap(resized, x + 530, y + yOffset, p);
                tempx = x;
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
