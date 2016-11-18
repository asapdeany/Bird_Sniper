package com.example.deansponholz.fish_game;

import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.graphics.Canvas;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

/**
 * Created by deansponholz on 11/13/16.
 */

public class AboutFragment extends Fragment {

    private int radioSelection;

    public SensorHandler sensorHandler = null;

    RadioButton accel_mag_radio = null;
    RadioButton gyro_radio = null;
    RadioButton fusion_radio = null;
    RadioGroup radioGroup = null;

    TextView zAxisAbout = null;
    TextView xAxisAbout = null;
    TextView yAxisAbout = null;

    Handler mHandler = new Handler();

    RelativeLayout fragment_about;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_about, container, false);


        sensorHandler = new SensorHandler(root.getContext());
        fragment_about = (RelativeLayout) root.findViewById(R.id.fragment_about);

        accel_mag_radio = (RadioButton) root.findViewById(R.id.accel_mag_radio);
        gyro_radio = (RadioButton) root.findViewById(R.id.gyro_radio);
        fusion_radio = (RadioButton) root.findViewById(R.id.fusion_radio);
        radioGroup = (RadioGroup) root.findViewById(R.id.aboutRadioGroup);

        zAxisAbout = (TextView) root.findViewById(R.id.zAxisAbout);
        xAxisAbout = (TextView) root.findViewById(R.id.xAxisAbout);
        yAxisAbout = (TextView) root.findViewById(R.id.yAxisAbout);
        radioSelection = -1;

        final AboutDrawView aboutDrawView = new AboutDrawView(this.getActivity());
        fragment_about.addView(aboutDrawView);

        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch(checkedId) {
                    case R.id.accel_mag_radio:
                        radioSelection = 0;
                        break;
                    case R.id.gyro_radio:
                        radioSelection = 1;
                        break;
                    case R.id.fusion_radio:
                        radioSelection = 2;
                        break;
                }

            }
        });

        return root;
    }

    public class AboutDrawView extends View{



        public AboutDrawView(Context context){

            super(context);
        }

        @Override
        public void onDraw(Canvas canvas){


            updateOrientationDisplay();
            invalidate();
        }
    }







    public void updateOrientationDisplay() {
        switch(radioSelection) {
            case 0:
                //zAxisAbout.setText("");
                //xAxisAbout.setText("");
                //yAxisAbout.setText("");
                zAxisAbout.setText(Double.toString(sensorHandler.accelmagZ));
                xAxisAbout.setText(Double.toString(sensorHandler.accelmagX));
                yAxisAbout.setText(Double.toString(sensorHandler.accelmagY));
                break;

            case 1:
                //zAxisAbout.setText("");
                //xAxisAbout.setText("");
                //yAxisAbout.setText("");
                zAxisAbout.setText(Double.toString(sensorHandler.gyroZ));
                xAxisAbout.setText(Double.toString(sensorHandler.gyroX));
                yAxisAbout.setText(Double.toString(sensorHandler.gyroY));
                break;
            case 2:
                //zAxisAbout.setText("");
                //xAxisAbout.setText("");
                //yAxisAbout.setText("");
                zAxisAbout.setText(Double.toString(sensorHandler.zPos));
                xAxisAbout.setText(Double.toString(sensorHandler.xPos));
                yAxisAbout.setText(Double.toString(sensorHandler.yPos));
                break;

        }
    }



}
