package com.example.deansponholz.fish_game;

import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

/**
 * Created by deansponholz on 11/25/16.
 */

public class Instruction_three_Fragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_instruction_three, container, false);
        return v;

    }

    public static Instruction_three_Fragment newInstance() {

        Instruction_three_Fragment f = new Instruction_three_Fragment();

        return f;
    }
}
