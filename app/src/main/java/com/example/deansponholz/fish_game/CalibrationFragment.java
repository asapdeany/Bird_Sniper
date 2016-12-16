package com.example.deansponholz.fish_game;

import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.RelativeLayout;


/**
 * Created by deansponholz on 11/12/16.
 */

public class CalibrationFragment extends Fragment {

    Button up_button, down_button, center_button;

    public static float yOffset, xOffset;

    float fishX, fishY;
    float shipSpawnY;
    int circleRadius;

    int lineX, lineY;
    int fishSizeX, fishSizeY;



    public Rect sprite1Bounds = new Rect(0,0,0,0);

    Display display;
    WindowManager wm;
    Point size;
    int width;
    int height;

    int deviceCalibrateUp, deviceCalibrateDown;

    // type definition
    public static final int FLIP_VERTICAL = 1;
    public static final int FLIP_HORIZONTAL = 2;
    public SensorHandler sensorHandler = null;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_calibrate, container, false);
        wm = (WindowManager) root.getContext().getSystemService(Context.WINDOW_SERVICE);
        offSetCalculator();
        sensorHandler = new SensorHandler(root.getContext());

        RelativeLayout fragment_calibrate = (RelativeLayout) root.findViewById(R.id.fragment_calibrate);
        CalibrateDrawView calibrateDrawView = new CalibrateDrawView(this.getActivity());
        fragment_calibrate.addView(calibrateDrawView);

        this.up_button = (Button) root.findViewById(R.id.up_button);
        this.center_button = (Button) root.findViewById(R.id.center_calibrate_button);
        this.down_button = (Button) root.findViewById(R.id.down_button);

        up_button.setOnTouchListener(new RepeatListener(400, 100, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                yOffset = yOffset - deviceCalibrateUp;
                //Log.d("yOffsetbefore", Double.toString(yOffset));
            }
        }));

        center_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Log.d("yOffsetbefore", Double.toString(yOffset));
                Intent intent = new Intent(getActivity(), HUDActivity.class);
                getActivity().startActivity(intent);
            }
        });

        down_button.setOnTouchListener(new RepeatListener(400, 100, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                yOffset = yOffset + deviceCalibrateDown;
                //yOffset = height / 2;
                //Log.d("yOffsetbefore", Double.toString(yOffset));
            }
        }));


        return root;
    }

    public class CalibrateDrawView extends View {

        //BitMaps
        Bitmap fish = BitmapFactory.decodeResource(getResources(), R.drawable.shark);
        Bitmap resizedFish = Bitmap.createScaledBitmap(fish, fishSizeX, fishSizeY, false);


        //onDraw
        Canvas canvas;
        Paint paint = new Paint();

        //Bitmap Positions
        int fish_offsetX, fish_offsetY;



        public CalibrateDrawView(Context context) {
            super(context);
            initMyView();
        }

        public void initMyView() {


            //Drawing Tools
            canvas = new Canvas();
            paint = new Paint();
            paint.setColor(Color.BLACK);
            paint.setStrokeWidth(2);
            paint.setStyle(Paint.Style.STROKE);

            //initial Bitmap Positions
            fish_offsetX = resizedFish.getWidth() / 2;
            fish_offsetY = resizedFish.getHeight() / 2;


        }
        @Override
        public void onDraw(Canvas canvas){
            canvas.drawCircle(width/2, height/2, circleRadius, paint);

            fishX = (float) (-sensorHandler.xPos*15) + xOffset;
            fishY = (float) (sensorHandler.yPos * 15) + yOffset;



            canvas.drawLine(fishX + lineX, fishY + lineY, width / 2, height / 2, paint);
            canvas.drawBitmap(resizedFish, fishX, fishY, paint);


            invalidate();
        }
    }


    public void offSetCalculator(){


        //Screen Inches
        DisplayMetrics dm = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(dm);
        int widthtest=dm.widthPixels;
        int heighttest=dm.heightPixels;
        int dens=dm.densityDpi;
        double wi=(double)widthtest/(double)dens;
        double hi=(double)heighttest/(double)dens;
        double xtest = Math.pow(wi,2);
        double ytest = Math.pow(hi,2);
        double screenInches = Math.sqrt(xtest+ytest);


        //screen Pixels
        display = wm.getDefaultDisplay();
        size = new Point();
        display.getSize(size);
        width =  size.x;
        height = size.y;


        if (screenInches > 6.0){
            //Log.d("BigDevice", Double.toString(screenInches));
            //Log.d("screenWidth", Integer.toString(width));
            //Log.d("screenHeight", Integer.toString(height));
            circleRadius = 90;
            shipSpawnY = (float)(height * 0.2);
            deviceCalibrateUp = 70;
            deviceCalibrateDown = 70;
            fishSizeX = 140;
            fishSizeY = 120;
            lineX = 50;
            lineY = 30;
        }
        
        if (screenInches < 6.0){
            //Log.d("SmallDevice", Double.toString(screenInches));
            //Log.d("screenWidth", Integer.toString(width));
            //Log.d("screenHeight", Integer.toString(height));
            circleRadius = 50;
            shipSpawnY = (float)(height * 0.1);
            deviceCalibrateUp = 50;
            deviceCalibrateDown = 50;
            fishSizeX = 80;
            fishSizeY = 60;
            lineX = 50;
            lineY = 40;
        }


        yOffset = (height / 2) - 60;
        xOffset = (width / 2) - 55;


    }



    public class RepeatListener implements View.OnTouchListener {

        private Handler handler = new Handler();

        private int initialInterval;
        private final int normalInterval;
        private final View.OnClickListener clickListener;

        private Runnable handlerRunnable = new Runnable() {
            @Override
            public void run() {
                handler.postDelayed(this, normalInterval);
                clickListener.onClick(downView);
            }
        };

        private View downView;

        /**
         * @param initialInterval The interval after first click event
         * @param normalInterval The interval after second and subsequent click
         *       events
         * @param clickListener The OnClickListener, that will be called
         *       periodically
         */
        public RepeatListener(int initialInterval, int normalInterval,
                              View.OnClickListener clickListener) {
            if (clickListener == null)
                throw new IllegalArgumentException("null runnable");
            if (initialInterval < 0 || normalInterval < 0)
                throw new IllegalArgumentException("negative interval");

            this.initialInterval = initialInterval;
            this.normalInterval = normalInterval;
            this.clickListener = clickListener;
        }

        public boolean onTouch(View view, MotionEvent motionEvent) {
            switch (motionEvent.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    handler.removeCallbacks(handlerRunnable);
                    handler.postDelayed(handlerRunnable, initialInterval);
                    downView = view;
                    downView.setPressed(true);
                    clickListener.onClick(view);
                    return true;
                case MotionEvent.ACTION_UP:
                case MotionEvent.ACTION_CANCEL:
                    handler.removeCallbacks(handlerRunnable);
                    downView.setPressed(false);
                    downView = null;
                    return true;
            }

            return false;
        }

    }

    @Override
    public void onPause() {
        super.onPause();
    }
}
