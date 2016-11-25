package com.example.deansponholz.fish_game;

import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.widget.RadioGroup;

/**
 * Created by deansponholz on 11/23/16.
 */

public class InstructionActivity extends FragmentActivity implements ViewPager.OnPageChangeListener, RadioGroup.OnCheckedChangeListener{

    private RadioGroup radioGroup;
    private static final int NUMBER_OF_PAGES = 4;
    ViewPager pager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_instruction);

        pager = (ViewPager) findViewById(R.id.viewPager);
        pager.setAdapter(new MyPagerAdapter(getSupportFragmentManager()));
        pager.addOnPageChangeListener(this);
        radioGroup = (RadioGroup)findViewById(R.id.radiogroup);
        radioGroup.setOnCheckedChangeListener(this);
    }


    /*************************************************************
     * Listeners for ViewPager
     *************************************************************/
    /**
     * When the current page is scrolled
     * @param position
     * @param v
     * @param i
     */
    @Override
    public void onPageScrolled(int position, float v, int i) {

    }

    /**
     * When a new page becomes selected
     * @param position
     */
    @Override
    public void onPageSelected(int position) {
        switch(position) {
            case 0:
                radioGroup.check(R.id.radioButton1);
                break;
            case 1:
                radioGroup.check(R.id.radioButton2);
                break;
            case 2:
                radioGroup.check(R.id.radioButton3);
                break;
            case 3:
                radioGroup.check(R.id.radioButton4);
                break;
            default:
                radioGroup.check(R.id.radioButton1);
        }
    }

    /**
     * When the pager is automatically setting to the current page
     * @param position
     */
    @Override
    public void onPageScrollStateChanged(int position) {

    }

    /**
     * On checked listener to Radio Buttons.
     * @param group
     * @param checkedId
     */
    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        switch(checkedId) {
            case R.id.radioButton1:
                pager.setCurrentItem(0);
                break;
            case R.id.radioButton2:
                pager.setCurrentItem(1);
                break;
            case R.id.radioButton3:
                pager.setCurrentItem(2);
                break;
            case R.id.radioButton4:
                pager.setCurrentItem(3);
                break;
        }
    }


    private class MyPagerAdapter extends FragmentPagerAdapter {

        public MyPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int pos) {
            switch(pos) {

                case 0: return Instruction_intro_Fragment.newInstance();
                case 1: return Instruction_one_Fragment.newInstance();
                case 2: return Instruction_two_Fragment.newInstance();
                case 3: return Instruction_three_Fragment.newInstance();
                default: return Instruction_one_Fragment.newInstance();
            }
        }

        @Override
        public int getCount() {
            return 4;
        }
    }
}
