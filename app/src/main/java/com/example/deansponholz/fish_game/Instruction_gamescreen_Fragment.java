package com.example.deansponholz.fish_game;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by deansponholz on 11/25/16.
 */

public class Instruction_gamescreen_Fragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_instruction_gamescreen, container, false);
        return v;

    }

    public static Instruction_gamescreen_Fragment newInstance() {

        Instruction_gamescreen_Fragment f = new Instruction_gamescreen_Fragment();

        return f;
    }
}
