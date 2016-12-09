package com.example.deansponholz.fish_game;

import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.RelativeLayout;

/**
 * Created by deansponholz on 11/3/16.
 */

public class MenuFragment extends Fragment {

    Button play_button = null;
    Button about_button = null;
    Button instruction_button = null;
    Button highscore_button = null;

    public SensorHandler sensorHandler = null;
    Display display;
    WindowManager wm;
    Point size;
    int width;
    int height;
    public static float yOffset, xOffset;
    private static final int LINE_SPACING = 80;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_menu, container, false);

        wm = (WindowManager) root.getContext().getSystemService(Context.WINDOW_SERVICE);
        offSetCalculator();
        sensorHandler = new SensorHandler(root.getContext());

        RelativeLayout fragment_menu = (RelativeLayout) root.findViewById(R.id.menu_layout);
        MenuDrawView menuDrawView = new MenuDrawView(this.getActivity());
        fragment_menu.addView(menuDrawView);
        this.play_button = (Button) root.findViewById(R.id.play_button);
        this.about_button = (Button) root.findViewById(R.id.about_button);
        this.instruction_button = (Button) root.findViewById(R.id.instructions_button);
        this.highscore_button = (Button) root.findViewById(R.id.highscore_button);


        play_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), CalibrationActivity.class);
                getActivity().startActivity(intent);
            }
        });

        about_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), AboutActivity.class);
                getActivity().startActivity(intent);
            }
        });

        instruction_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), InstructionActivity.class);
                getActivity().startActivity(intent);
            }
        });

        highscore_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), HighscoreActivity.class);
                getActivity().startActivity(intent);
            }
        });


        return root;
    }

    public class MenuDrawView extends View {




        //onDraw
        Canvas canvas;
        Paint paint = new Paint();
        float fishX, fishY;

        public MenuDrawView(Context context) {
            super(context);
            initMyView();
        }

        public void initMyView() {


            //Drawing Tools
            canvas = new Canvas();
            paint = new Paint();
            paint.setColor(Color.BLACK);
            paint.setStrokeWidth(1.0f);
            paint.setStyle(Paint.Style.STROKE);


        }
        @Override
        public void onDraw(Canvas canvas){

            fishX = (float) (-sensorHandler.xPos * 5) + xOffset;
            fishY = (float) (sensorHandler.yPos * 5) + yOffset;




            //Loop through to create 10 vertical lines
            for (int i = 1; i < 15; i++) {
                canvas.drawLine(fishX + (i * LINE_SPACING), -height, fishX + (i * LINE_SPACING), +height, paint);

            }
            for (int i = 1; i < 15; i++) {
                canvas.drawLine(fishX + (i * -LINE_SPACING), -height, fishX + (i * -LINE_SPACING), +height, paint);

            }

            //Loop through to create 10 horizontal lines
            for (int i = 1; i < 15; i++) {
                canvas.drawLine(0, fishY + (i * LINE_SPACING), width, fishY + (i * LINE_SPACING), paint);

            }
            for (int i = 1; i < 15; i++) {
                canvas.drawLine(0, fishY - (i * LINE_SPACING), width, fishY - (i * LINE_SPACING), paint);
            }
            //middle vertical line from landscape point of view
            canvas.drawLine(fishX, -height, fishX, height, paint);
            //middle horizontal line from landscape point of view
            canvas.drawLine(0, fishY, width, fishY, paint);
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



        }
        if (screenInches < 6.0){
            Log.d("SmallDevice", Double.toString(screenInches));
            Log.d("screenWidth", Integer.toString(width));
            Log.d("screenHeight", Integer.toString(height));

        }


        yOffset = (height / 2) - 60;
        xOffset = (width / 2) - 55;

    }
}
