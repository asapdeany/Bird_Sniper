package com.example.deansponholz.fish_game;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

/**
 * Created by deansponholz on 11/3/16.
 */

public class MenuFragment extends Fragment {

    Button play_button = null;
    Button about_button = null;
    Button instruction_button = null;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_menu, container, false);
        this.play_button = (Button) root.findViewById(R.id.play_button);
        this.about_button = (Button) root.findViewById(R.id.about_button);
        this.instruction_button = (Button) root.findViewById(R.id.instructions_button);


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


        return root;
    }
}
