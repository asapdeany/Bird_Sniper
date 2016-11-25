package com.example.deansponholz.fish_game;

import android.graphics.drawable.Drawable;
import android.graphics.drawable.TransitionDrawable;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Created by deansponholz on 11/23/16.
 */

public class Instruction_one_Fragment extends Fragment{

    Handler handler = null;
    final int[] layers = new int[7];
    ImageView iv;
    int i;



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_instruction_one, container, false);

        i = 0;
        handler = new Handler();
        iv = (ImageView) v.findViewById(R.id.tiltImage);
        handler.postDelayed(updateImageView, 0);



        return v;

    }


    Runnable updateImageView = new Runnable() {
        public void run()
        {

            layers[0] = (R.drawable.pencil_one);
            layers[1] = (R.drawable.pencil_two);
            layers[2] = (R.drawable.pencil_three);
            layers[3] = (R.drawable.pencil_four);
            layers[4] = (R.drawable.pencil_three);
            layers[5] = (R.drawable.pencil_two);
            layers[6] = (R.drawable.pencil_one);

            if (i <= layers.length - 1){
                iv.setImageResource(layers[i]);
                i++;
            }
            if (i > layers.length -1){
                i = 0;
            }
            Log.d("hey", "test");
            handler.postDelayed(this, 750);
        }
    };

    public static Instruction_one_Fragment newInstance() {

        Instruction_one_Fragment f = new Instruction_one_Fragment();

        return f;
    }

    @Override
    public void onPause() {
        handler.removeCallbacks(updateImageView);
        super.onPause();
    }
}
