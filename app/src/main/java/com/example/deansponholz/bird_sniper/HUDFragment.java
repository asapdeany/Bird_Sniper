package com.example.deansponholz.bird_sniper;

import android.app.Fragment;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PathMeasure;
import android.graphics.PorterDuff;
import android.graphics.Rect;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.Random;

import static android.R.attr.animationDuration;
import static android.content.Context.SENSOR_SERVICE;

/**
 * Created by deansponholz on 11/3/16.
 */

public class HUDFragment extends Fragment {

    TextView yaw_test = null;
    TextView pitch_test = null;
    TextView roll_test = null;
    Button calibrate_button = null;
    Button decalibrate_buton = null;

    SensorData s = null;


    public double xPos, yPos;

    float yOffset = 300;

    float fishX;
    float fishY;

    float tempx = 0;
    float x = 260;
    float y = 500;

    public Rect sprite1Bounds = new Rect(0,0,0,0);

    int width;
    int height;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_main, container, false);

        RelativeLayout test = (RelativeLayout) root.findViewById(R.id.test);
        ScopeView myDrawing = new ScopeView(this.getActivity());
        fishDrawView fishDrawView = new fishDrawView(this.getActivity());
        test.addView(myDrawing);
        test.addView(fishDrawView);



        s = new SensorData((SensorManager) getActivity().getSystemService(Context.SENSOR_SERVICE), this);

        this.yaw_test = (TextView) root.findViewById(R.id.yaw_test);
        pitch_test = (TextView) root.findViewById(R.id.pitch_test);
        roll_test = (TextView) root.findViewById(R.id.roll_test);
        calibrate_button = (Button) root.findViewById(R.id.calibration_button);
        decalibrate_buton = (Button) root.findViewById(R.id.decalibrate_button);


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


        return root;
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
    public class fishDrawView extends View{

        Paint paint = new Paint();
        Bitmap b = BitmapFactory.decodeResource(getResources(), R.drawable.yellowfish);
        Bitmap pship = BitmapFactory.decodeResource(getResources(), R.drawable.pirateship_normal);

        Bitmap resized = Bitmap.createScaledBitmap(b, 90, 70, false);
        Bitmap flipped = Bitmap.createBitmap(flip(resized, 2));
        Bitmap pirateShip = Bitmap.createBitmap(pship);

        Random r = new Random();

        int bm_offsetX, bm_offsetY;
        int ship_offsetX, ship_offSetY;

        Path animPath;
        Path shipPath;
        PathMeasure pathMeasure;
        PathMeasure shipPathMeasure;
        float pathLength, shipPathLength;

        float step, shipStep;   //distance each step
        float distance, shipdistance;  //distance moved

        float[] pos;
        float[] tan;
        float speed, shipSpeed;
        float[] shippos;
        float[] shiptan;
        Matrix matrix;
        Matrix shipMatrix;
        Canvas canvas;

        public fishDrawView(Context context){super(context);
        initMyView();
        }

        public void initMyView(){
            canvas = new Canvas();
            paint = new Paint();
            paint.setColor(Color.WHITE);
            paint.setStrokeWidth(2);
            paint.setStyle(Paint.Style.STROKE);


            bm_offsetX = resized.getWidth()/2;
            bm_offsetY = resized.getHeight()/2;

            ship_offsetX = pirateShip.getWidth()/2;
            ship_offSetY = pirateShip.getHeight()/2;



            animPath = randomPath();
            pathMeasure = new PathMeasure(randomPath(), false);
            speed = pathMeasure.getLength()/1000;
            pathLength = pathMeasure.getLength() / 2;


            step = 1;
            distance = 0;
            pos = new float[2];
            tan = new float[2];

            matrix = new Matrix();

            shipPath = new Path();
            shipPath.moveTo(1200, 80);
            shipPath.lineTo(0, 80);
            shipPath.close();

            shipPathMeasure = new PathMeasure(shipPath, false);
            shipSpeed = shipPathMeasure.getLength()/5000;
            shipPathLength = shipPathMeasure.getLength();
            shippos = new float[2];
            shiptan = new float[2];
            shipdistance = 0;
            shipMatrix = new Matrix();
        }


        @Override
        public void onDraw(Canvas canvas){
            //http://android-er.blogspot.com/2014/05/animation-of-moving-bitmap-along-path.html


            //canvas.drawPath(animPath, paint);
            canvas.drawPath(shipPath, paint);
            if(distance < pathLength){
                pathMeasure.getPosTan(distance, pos, tan);
                distance += speed;

                matrix.reset();
                float degrees = (float)(Math.atan2(tan[1], tan[0])*180.0/Math.PI);
                fishX = pos[0]-bm_offsetX;
                fishY = pos[1]-bm_offsetY;
                matrix.postTranslate(fishX, fishY);
                canvas.drawBitmap(flipped, matrix, null);
                sprite1Bounds = new Rect((int)(fishX) + 10, (int)fishY + 10, (int) fishX +flipped.getWidth() - 10, (int)fishY + flipped.getHeight() - 10);

                canvas.drawRect(sprite1Bounds, paint);
                //Log.d("end", Float.toString(fishX));

                distance += step;
            }else{

                distance = 0;
                animPath = randomPath();
                pathMeasure = new PathMeasure(randomPath(), false);
                speed = pathMeasure.getLength()/1000;
                pathLength = pathMeasure.getLength() / 2;
                //Log.d("end", "killme");
            }

            if(shipdistance < shipPathLength){
                shipPathMeasure.getPosTan(shipdistance, shippos, shiptan);
                shipdistance += shipSpeed;

                shipMatrix.reset();

                shipMatrix.postTranslate(shippos[0]-ship_offsetX, shippos[1]-ship_offSetY);
                canvas.drawBitmap(pirateShip, shipMatrix, null);
                shipdistance += shipStep;
            }else{
                shipdistance = 0;
                //Log.d("end", "killme");
            }

            //collision detection
            //http://stackoverflow.com/questions/5914911/pixel-perfect-collision-detection-android




            invalidate();
        }

    }

    public Path randomPath(){

        Random r = new Random();
        Path animPath = new Path();
        int Low = 90;
        int High = 700;
        int randSpawn = r.nextInt(High-Low) + Low;
        int randEnd = r.nextInt(High-Low) + Low;
        animPath.moveTo(0, randSpawn);
        animPath.lineTo(1250, randEnd);
        animPath.close();

        return animPath;
    }


    public class ScopeView extends View {

        Paint p = new Paint();
        //Bitmap b = BitmapFactory.decodeResource(getResources(), R.drawable.yellowfish);

        //Bitmap resized = Bitmap.createScaledBitmap(b, 90, 70, false);
        //Bitmap flipped = Bitmap.createBitmap(flip(resized, 2));

        public ScopeView(Context context){super(context);}

        @Override
        public void onDraw(Canvas canvas){
            p.setColor(Color.YELLOW);
            p.setStyle(Paint.Style.STROKE);
            p.setStrokeWidth(2.5f);

            x = (float) (-xPos*15);
            y = (float) (yPos * 15);




            canvas.drawCircle(x + 530, y+ yOffset, 50, p);



            /*
            if (x > tempx) {
                canvas.drawBitmap(flipped, x + 530, y + yOffset, p);
                tempx = x;
            }

            else if (x < tempx){
                canvas.drawBitmap(resized, x + 530, y + yOffset, p);
                tempx = x;
            }
            */




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
    /*

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
    */


}
