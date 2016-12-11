package com.example.deansponholz.fish_game;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.RelativeLayout;

/**
 * Created by deansponholz on 11/26/16.
 */

public class Instruction_practice_Fragment extends Fragment {

    public static float yOffset, xOffset;
    private static final int LINE_SPACING = 80;
    float fishX, fishY;
    int fishSizeX, fishSizeY;
    Display display;
    WindowManager wm;
    Point size;
    int width;
    int height;
    public SensorHandler sensorHandler = null;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_instruction_practice, container, false);


        wm = (WindowManager) root.getContext().getSystemService(Context.WINDOW_SERVICE);
        offSetCalculator();
        sensorHandler = new SensorHandler(root.getContext());

        RelativeLayout fragment_practice = (RelativeLayout) root.findViewById(R.id.fragment_practice);
        PracticeDrawView practiceDrawView = new PracticeDrawView(this.getActivity());
        fragment_practice.addView(practiceDrawView);
        return root;
    }

    public static Instruction_practice_Fragment newInstance() {

        Instruction_practice_Fragment f = new Instruction_practice_Fragment();
        return f;
    }
    public class PracticeDrawView extends View {

        //BitMaps
        Bitmap fish = BitmapFactory.decodeResource(getResources(), R.drawable.shark);
        Bitmap resizedFish = Bitmap.createScaledBitmap(fish, fishSizeX, fishSizeY, false);


        //onDraw
        Canvas canvas;
        Paint paint = new Paint();


        public PracticeDrawView(Context context) {
            super(context);
            initMyView();
        }

        public void initMyView() {


            //Drawing Tools
            canvas = new Canvas();
            paint = new Paint();
            paint.setColor(Color.WHITE);
            paint.setStrokeWidth(1.0f);
            paint.setStyle(Paint.Style.STROKE);


        }
        @Override
        public void onDraw(Canvas canvas){

            fishX = (float) (-sensorHandler.xPos*15) + xOffset;
            fishY = (float) (sensorHandler.yPos * 15) + yOffset;




            //Loop through to create 10 vertical lines
            for (int i = 1; i < 15; i++) {
                canvas.drawLine(width/2 + (i * LINE_SPACING), -height, width/2 + (i * LINE_SPACING), +height, paint);

            }
            for (int i = 1; i < 15; i++) {
                canvas.drawLine(width/2 + (i * -LINE_SPACING), -height, width/2 + (i * -LINE_SPACING), +height, paint);

            }

            //Loop through to create 10 horizontal lines
            for (int i = 1; i < 15; i++) {
                canvas.drawLine(0, width/2 + (i * LINE_SPACING), width, width/2 + (i * LINE_SPACING), paint);

            }
            for (int i = 1; i < 15; i++) {
                canvas.drawLine(0, width/2 - (i * LINE_SPACING), width, width/2 - (i * LINE_SPACING), paint);
            }
            //middle vertical line from landscape point of view
            canvas.drawLine(width/2, -height, width/2, height, paint);
            //middle horizontal line from landscape point of view
            canvas.drawLine(0, width/2, width, width/2, paint);
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
            Log.d("BigDevice", Double.toString(screenInches));
            Log.d("screenWidth", Integer.toString(width));
            Log.d("screenHeight", Integer.toString(height));
            fishSizeX = 150;
            fishSizeY = 130;


        }
        if (screenInches < 6.0){
            Log.d("SmallDevice", Double.toString(screenInches));
            Log.d("screenWidth", Integer.toString(width));
            Log.d("screenHeight", Integer.toString(height));
            fishSizeX = 120;
            fishSizeY = 100;
        }


        yOffset = (height / 2) - 60;
        xOffset = (width / 2) - 55;

    }

}
