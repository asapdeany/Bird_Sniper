package com.example.deansponholz.fish_game;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.Random;

/**
 * Created by deansponholz on 11/3/16.
 */

public class HUDFragment extends Fragment {

    //theme song
    //https://www.youtube.com/watch?v=7-G0kK4FIdM

    //Dimension specifics, Fragment Layout, and Sensor Calibration
    RelativeLayout fragment_main;
    Display display;
    WindowManager wm;
    Point size;
    int width;
    int height;
    int fishSizeX, fishSizeY;
    int sharkSizeX, sharkSizeY;
    int lowSpawn, highSpawn;
    Boolean gameStarted = false;
    private SensorHandler sensorHandler = null;
    private CalibrationFragment calibrationFragment = null;

    //Views and Score
    TextView score_TextView = null;
    //TextView hookCount_TextView = null;
    Button menu_Button = null;
    Button startGame_Button = null;
    Button submitScore_Button = null;
    int gameScore, hookCount;

    //flip bitmap function
    public static final int FLIP_VERTICAL = 1;
    public static final int FLIP_HORIZONTAL = 2;

    //GameHandler
    Handler spawnHandler;
    Runnable spawnFish;
    Runnable spawnShark;

    //Fish Bitmaps and position
    Bitmap fish1, fish2, fish3, fish1flip, fish2flip, fish3flip;
    Bitmap leftFish, rightFish;
    float spawnedFishXPosition, spawnedFishYPosition;

    //Shark bitmap and position
    Bitmap shark1, shark2, shark1flip, shark2flip;
    Bitmap leftShark, rightShark;
    float spawnedSharkXPosition, spawnedSharkYPosition;

    //Ship Bitmaps, position, and spawnPosition
    ImageView shipImage = null;
    Bitmap pship;
    Bitmap pirateShip;
    float shipXPosition, shipYPosition;
    float shipSpawnY;

    //Hook Bitmap, Position, Offset, and Collision Detection rectangle values
    Bitmap hookDrawable;
    Bitmap hookNormal, hookcaught1, hookcaught2, hookcaught3;
    Bitmap hook;
    float hookX, hookY;
    int hookSizeX, hookSizeY;
    int hookOffsetX, hookOffsetY;
    int hookLeft, hookRight, hookTop, hookBottom;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_main, container, false);

        //Device Metrics
        wm = (WindowManager) root.getContext().getSystemService(Context.WINDOW_SERVICE);
        deviceSizeCalculator();
        //Sensor and Calibration
        sensorHandler = new SensorHandler(root.getContext());
        calibrationFragment = new CalibrationFragment();

        //Fragment and buttons
        fragment_main = (RelativeLayout) root.findViewById(R.id.fragment_main);
        //this.hookCount_TextView = (TextView) root.findViewById(R.id.hookCountTextView);
        this.score_TextView = (TextView) root.findViewById(R.id.scoreTextView);
        this.menu_Button = (Button) root.findViewById(R.id.menu_button);
        this.startGame_Button = (Button) root.findViewById(R.id.startGameButton);
        this.submitScore_Button = (Button) root.findViewById(R.id.submitScoreButton);

        //Custom Draw View
        final HUDDrawView hudDrawView = new HUDDrawView(this.getActivity());
        fragment_main.addView(hudDrawView);

        //Button Listeners
        startGame_Button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gameStarted = true;
                startGame_Button.setClickable(false);
                startGame_Button.setVisibility(View.INVISIBLE);
                submitScore_Button.setVisibility(View.INVISIBLE);
                hookCount = 0;
                gameScore = 0;
                hook = hookNormal;
                hudDrawView.startGame();

            }
        });

        menu_Button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), MenuActivity.class);
                getActivity().startActivity(intent);
            }
        });


        return root;
    }

    public class HUDDrawView extends View{

        //Random
        Random r;

        //onDraw
        Canvas canvas;
        Paint paint = new Paint();
        Paint hookPaint = new Paint();

        //Bitmap Offsets
        int ship_offsetX, ship_offSetY;

        public HUDDrawView(Context context){super(context);
        initMyView();
        }

        public void initMyView(){

            r = new Random();



            //Drawing Tools
            canvas = new Canvas();
            paint = new Paint();
            hookPaint = new Paint();
            hookPaint.setColor(Color.TRANSPARENT);
            hookPaint.setStrokeWidth(1.5f);
            hookPaint.setStyle(Paint.Style.STROKE);

            //ShipOffset
            ship_offsetX = pirateShip.getWidth()/2;
            ship_offSetY = pirateShip.getHeight()/2;

        }

        public void startGame(){

            hookPaint.setColor(Color.WHITE);
            draw(canvas);
            spawnHandler = new Handler();
            spawnBoat();
            spawnFish = new Runnable(){
                public void run(){
                    //Log.d("Runnable", "spawnfish");
                    int x = r.nextBoolean() ? 1 : -1;
                    //Log.d("test", Integer.toString(x));
                    if (x == 1){
                        spawnFishRight();
                    }
                    else if (x == -1){
                        spawnFishLeft();
                    }
                    spawnHandler.postDelayed(this, 850);
                }
            };
            spawnShark = new Runnable() {
                @Override
                public void run() {
                    //Log.d("Runnable", "spawnShark");
                    int x = r.nextBoolean() ? 1 : -1;
                    if (x == 1){
                        spawnSharkRight();
                    }
                    else if (x == -1){
                        spawnSharkLeft();
                    }
                    spawnHandler.postDelayed(this, 3000);
                }
            };

            spawnHandler.post(spawnShark);
            spawnHandler.post(spawnFish);
        }

        public void spawnBoat(){

            shipImage = new ImageView(getContext());
            shipImage.setImageBitmap(pirateShip);
            shipImage.setX(width - 80);
            shipImage.setY(shipSpawnY);
            fragment_main.addView(shipImage);
            moveBoat(shipImage);
        }

        public void moveBoat(ImageView shipImageView){

            final ObjectAnimator translateXAnimation = ObjectAnimator.ofFloat(shipImage,"translationX",-75);
            ObjectAnimator translateYAnimation= ObjectAnimator.ofFloat(shipImage, "translationY", shipSpawnY);
            AnimatorSet set = new AnimatorSet();
            set.setDuration(60000);
            set.playTogether(translateXAnimation, translateYAnimation);
            set.start();

            set.addListener(new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(Animator animation) {

                }

                @Override
                public void onAnimationEnd(Animator animation) {
                    hookPaint.setColor(Color.TRANSPARENT);
                    spawnHandler.removeCallbacks(spawnFish);
                    spawnHandler.removeCallbacks(spawnShark);
                    fragment_main.removeView(shipImage);
                    startGame_Button.setVisibility(VISIBLE);
                    startGame_Button.setText("Play Again");
                    startGame_Button.setClickable(true);

                    submitScore_Button.setVisibility(VISIBLE);
                    submitScore_Button.setClickable(true);
                }

                @Override
                public void onAnimationCancel(Animator animation) {

                }

                @Override
                public void onAnimationRepeat(Animator animation) {

                }
            });


            translateXAnimation.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    shipXPosition = (Float)animation.getAnimatedValue();
                }
            });

            translateYAnimation.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    shipYPosition = (Float)animation.getAnimatedValue();
                    //String position = String.format("X:%d Y:%d", (int)shipXPosition, (int)shipYPosition);
                    //Log.d("shipPosition", position);
                    if (isCollisionShipHook(pirateShip, (int)shipXPosition, (int)shipYPosition, hook, (int)hookX, (int)hookY) == true){
                        gameScore = gameScore + hookCount;
                        hookCount = 0;
                        hook = hookNormal;
                    }
                }
            });
        }

        public void spawnSharkRight(){
            int startRand = r.nextInt(highSpawn - lowSpawn) + lowSpawn;
            //Log.d("sharkStartPositionRight", Integer.toString(startRand));
            int sharkRand = r.nextInt(3-1) + 1;
            //Log.d("randdomShark", Integer.toString(sharkRand));

            ImageView myImage = new ImageView(getContext());
            if (sharkRand == 1){
                leftShark = shark1;
                myImage.setImageBitmap(leftShark);
            }
            if (sharkRand == 2){
                leftShark = shark2;
                myImage.setImageBitmap(leftShark);
            }

            myImage.setX(width - 80);
            myImage.setY(startRand);
            fragment_main.addView(myImage);
            moveSharkLeft(myImage);

        }

        public void spawnSharkLeft(){
            int startRand = r.nextInt(highSpawn - lowSpawn) + lowSpawn;
            //Log.d("sharkStartPositionRight", Integer.toString(startRand));
            int sharkRand = r.nextInt(3-1) + 1;
            //Log.d("randdomShark", Integer.toString(sharkRand));

            ImageView myImage = new ImageView(getContext());
            if (sharkRand == 1){
                rightShark = shark1flip;
                myImage.setImageBitmap(rightShark);
            }
            if (sharkRand == 2){
                rightShark = shark2flip;
                myImage.setImageBitmap(rightShark);
            }

            myImage.setX(-100f);
            myImage.setY(startRand);
            fragment_main.addView(myImage);
            moveSharkRight(myImage);


        }
        public void moveSharkRight(final ImageView sharkView){
            int randEnd = r.nextInt(highSpawn - lowSpawn) + lowSpawn;

            ObjectAnimator translateXAnimation = ObjectAnimator.ofFloat(sharkView,"translationX",width);
            ObjectAnimator translateYAnimation= ObjectAnimator.ofFloat(sharkView, "translationY", randEnd);

            AnimatorSet set = new AnimatorSet();
            set.setDuration(10000);
            set.playTogether(translateXAnimation, translateYAnimation);
            set.start();

            set.addListener(new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(Animator animation) {
                }
                @Override
                public void onAnimationEnd(Animator animation) {
                    fragment_main.removeView(sharkView);
                }
                @Override
                public void onAnimationCancel(Animator animation) {
                }
                @Override
                public void onAnimationRepeat(Animator animation) {
                }
            });

            translateXAnimation.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    spawnedSharkXPosition = (Float)animation.getAnimatedValue();
                }
            });
            translateYAnimation.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    spawnedSharkYPosition = (Float)animation.getAnimatedValue();
                    //check for collision
                    if (isCollisionDetectedSharkHook(rightShark, (int) spawnedSharkXPosition, (int) spawnedSharkYPosition, hook, (int)hookX, (int)hookY) == true){
                        hookCount = 0;
                        hook = hookNormal;
                        hookPaint.setColor(Color.RED);
                        hookPaint.setStrokeWidth(5.25f);
                        final Handler handler = new Handler();
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                hookPaint.setColor(Color.WHITE);
                                hookPaint.setStrokeWidth(1.5f);
                            }
                        }, 100);
                    }
                }
            });
        }

        public void moveSharkLeft(final ImageView sharkView){
            int randEnd = r.nextInt(highSpawn - lowSpawn) + lowSpawn;

            ObjectAnimator translateXAnimation = ObjectAnimator.ofFloat(sharkView,"translationX",0);
            ObjectAnimator translateYAnimation= ObjectAnimator.ofFloat(sharkView, "translationY", randEnd);

            AnimatorSet set = new AnimatorSet();
            set.setDuration(10000);
            set.playTogether(translateXAnimation, translateYAnimation);
            set.start();

            set.addListener(new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(Animator animation) {
                }
                @Override
                public void onAnimationEnd(Animator animation) {
                    fragment_main.removeView(sharkView);
                }
                @Override
                public void onAnimationCancel(Animator animation) {
                }
                @Override
                public void onAnimationRepeat(Animator animation) {
                }
            });

            translateXAnimation.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    spawnedSharkXPosition = (Float)animation.getAnimatedValue();
                }
            });
            translateYAnimation.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    spawnedSharkYPosition = (Float)animation.getAnimatedValue();
                    //check for collision
                    if (isCollisionDetectedSharkHook(leftShark, (int) spawnedSharkXPosition, (int) spawnedSharkYPosition, hook, (int)hookX, (int)hookY) == true){
                        hookCount = 0;
                        hook = hookNormal;
                        hookPaint.setColor(Color.RED);
                        hookPaint.setStrokeWidth(5.25f);
                        final Handler handler = new Handler();
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                hookPaint.setColor(Color.WHITE);
                                hookPaint.setStrokeWidth(1.5f);
                            }
                        }, 100);
                    }
                }
            });
        }


        public void spawnFishRight(){

            int fishRand = r.nextInt(4-1) + 1;
            //Log.d("fishRand", Integer.toString(fishRand));
            int startRand = r.nextInt(highSpawn - lowSpawn) + lowSpawn;
            //Log.d("fishStartPositionRight", Integer.toString(startRand));

            ImageView myImage = new ImageView(getContext());
            if (fishRand == 1){
                //leftFish = Bitmap.createScaledBitmap(fish1, fishSizeX, fishSizeY, false);
                leftFish = fish1;
                myImage.setImageBitmap(leftFish);
            }
            if (fishRand == 2){
                //leftFish = Bitmap.createScaledBitmap(fish2, fishSizeX, fishSizeY, false);
                leftFish = fish2;
                myImage.setImageBitmap(leftFish);
            }
            if (fishRand == 3){
                //leftFish = Bitmap.createScaledBitmap(fish3, fishSizeX, fishSizeY, false);
                leftFish = fish3;
                myImage.setImageBitmap(leftFish);
            }

            myImage.setX(width-80);
            myImage.setY(startRand);
            fragment_main.addView(myImage);
            moveFishLeft(myImage);
        }

        public void spawnFishLeft(){

            int fishRand = r.nextInt(4-1) + 1;
            //Log.d("fishRand", Integer.toString(fishRand));
            int startRand = r.nextInt(highSpawn - lowSpawn) + lowSpawn;
            //Log.d("fishStartPositionLeft", Integer.toString(number));

            ImageView myImage = new ImageView(getContext());
            if (fishRand == 1){
                rightFish = fish1flip;
                myImage.setImageBitmap(rightFish);
            }
            if (fishRand == 2){
                rightFish = fish2flip;
                myImage.setImageBitmap(rightFish);
            }
            if (fishRand == 3){
                rightFish = fish3flip;
                myImage.setImageBitmap(rightFish);
            }

            myImage.setX(width * 0.0005f);
            myImage.setY(startRand);
            fragment_main.addView(myImage);
            moveFishRight(myImage);
        }

        public void moveFishRight(final ImageView fishView){

            int randEnd = r.nextInt(highSpawn - lowSpawn) + lowSpawn;

            ObjectAnimator translateXAnimation = ObjectAnimator.ofFloat(fishView,"translationX",width);
            ObjectAnimator translateYAnimation= ObjectAnimator.ofFloat(fishView, "translationY", randEnd);

            AnimatorSet set = new AnimatorSet();
            set.setDuration(6000);
            set.playTogether(translateXAnimation, translateYAnimation);
            set.start();

            set.addListener(new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(Animator animation) {

                }

                @Override
                public void onAnimationEnd(Animator animation) {
                    fragment_main.removeView(fishView);
                }

                @Override
                public void onAnimationCancel(Animator animation) {

                }

                @Override
                public void onAnimationRepeat(Animator animation) {

                }
            });

            translateXAnimation.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    spawnedFishXPosition = (Float)animation.getAnimatedValue();
                }
            });
            translateYAnimation.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    spawnedFishYPosition = (Float)animation.getAnimatedValue();
                    //check for collision
                    if (isCollisionDetectedFishHook(rightFish, (int) spawnedFishXPosition, (int) spawnedFishYPosition, hook, (int)hookX, (int)hookY) == true){
                        hookCount++;
                        fragment_main.removeView(fishView);
                        animation.removeAllUpdateListeners();
                    }
                    if (hookCount >= 1){
                        //hook = Bitmap.createScaledBitmap(hookcaught1, hookSizeX, hookSizeY, false);
                        hook = hookcaught1;
                    }
                    if (hookCount >= 5){
                        //hook = Bitmap.createScaledBitmap(hookcaught2, hookSizeX, hookSizeY, false);
                        hook = hookcaught2;
                    }
                    if (hookCount >= 10){
                        //hook = Bitmap.createScaledBitmap(hookcaught3, hookSizeX, hookSizeY, false);
                        hook = hookcaught3;
                    }
                }
            });
        }

        public void moveFishLeft(final ImageView fishView){

            int end = r.nextInt(highSpawn - lowSpawn) + lowSpawn;

            ObjectAnimator translateXAnimation = ObjectAnimator.ofFloat(fishView,"translationX",0);
            ObjectAnimator translateYAnimation= ObjectAnimator.ofFloat(fishView, "translationY", end);

            AnimatorSet set = new AnimatorSet();
            set.setDuration(6000);
            set.playTogether(translateXAnimation, translateYAnimation);
            set.start();

            set.addListener(new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(Animator animation) {
                }
                @Override
                public void onAnimationEnd(Animator animation) {
                    fragment_main.removeView(fishView);
                }
                @Override
                public void onAnimationCancel(Animator animation) {
                }
                @Override
                public void onAnimationRepeat(Animator animation) {
                }
            });

            translateXAnimation.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    spawnedFishXPosition = (Float)animation.getAnimatedValue();
                }
            });
            translateYAnimation.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    spawnedFishYPosition = (Float)animation.getAnimatedValue();
                    //check for collision
                    if (isCollisionDetectedFishHook(leftFish, (int) spawnedFishXPosition, (int) spawnedFishYPosition, hook, (int)hookX, (int)hookY) == true){
                        hookCount++;
                        fragment_main.removeView(fishView);
                        animation.removeAllUpdateListeners();
                    }
                    if (hookCount >= 1){
                        //hook = Bitmap.createScaledBitmap(hookcaught1, hookSizeX, hookSizeY, false);
                        hook = hookcaught1;
                    }
                    if (hookCount >= 5){
                        //hook = Bitmap.createScaledBitmap(hookcaught2, hookSizeX, hookSizeY, false);
                        hook = hookcaught2;
                    }
                    if (hookCount >= 10){
                       //hook = Bitmap.createScaledBitmap(hookcaught3, hookSizeX, hookSizeY, false);
                        hook = hookcaught3;
                    }
                }
            });
        }

        public void onDraw(Canvas canvas){
            //update Score
            //hookCount_TextView.setText("Hook Count: " + Integer.toString(hookCount));
            score_TextView.setText(Integer.toString(gameScore));
            //draw hook and line
            drawFishLine(canvas);

            //Rect bounds1 = new Rect((int)(shipXPosition) + 10, (int)shipYPosition + 10, (int) shipXPosition + pirateShip.getWidth()-10, (int)shipYPosition + pirateShip.getHeight()-10);
            //canvas.drawRect(bounds1, paint);
            //Rect bounds2 = new Rect((int) hookX + hookLeft,(int) hookY + hookTop, (int) hookX + hook.getWidth() - hookRight, (int) hookY + hook.getHeight() - hookBottom);
            //canvas.drawRect(bounds2, paint);
            invalidate();
        }

        public void drawFishLine(Canvas canvas){
            //use sensors and calibration data to set hook position
            hookX = (float) (-sensorHandler.xPos*15) + calibrationFragment.xOffset;
            hookY = (float) (sensorHandler.yPos * 15) + calibrationFragment.yOffset;
            canvas.drawBitmap(hook, hookX, hookY, paint);
            canvas.drawLine(hookX + hookOffsetX, hookY + hookOffsetY, shipXPosition + ship_offsetX, shipYPosition + ship_offSetY, hookPaint);
        }
    }

    public void deviceSizeCalculator(){

        //BitMaps
        hookDrawable = BitmapFactory.decodeResource(getResources(), R.drawable.hook);
        hookcaught1 = BitmapFactory.decodeResource(getResources(), R.drawable.hookcaught_first);
        hookcaught2 = BitmapFactory.decodeResource(getResources(), R.drawable.hookcaught_second);
        hookcaught3 = BitmapFactory.decodeResource(getResources(), R.drawable.hookcaught_third);
        pship = BitmapFactory.decodeResource(getResources(), R.drawable.pirateship_normal);
        //fishBitmaps
        fish1 = BitmapFactory.decodeResource(getResources(), R.drawable.fish1);
        fish2 = BitmapFactory.decodeResource(getResources(), R.drawable.fish2);
        fish3 = BitmapFactory.decodeResource(getResources(), R.drawable.fish3);
        shark1 = BitmapFactory.decodeResource(getResources(), R.drawable.shark1);
        shark2 = BitmapFactory.decodeResource(getResources(), R.drawable.shark2);

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
            shipSpawnY = (float)(height * 0.055);
            fishSizeX = 80;
            fishSizeY = 60;
            fish1 = Bitmap.createScaledBitmap(fish1, fishSizeX, fishSizeY, false);
            fish2 = Bitmap.createScaledBitmap(fish2, fishSizeX, fishSizeY, false);
            fish3 = Bitmap.createScaledBitmap(fish3, fishSizeX, fishSizeY, false);
            fish1flip = flip(fish1, 2);
            fish2flip = flip(fish2, 2);
            fish3flip = flip(fish3, 2);
            sharkSizeX = 235;
            sharkSizeY = 143;
            shark1 = Bitmap.createScaledBitmap(shark1, sharkSizeX, sharkSizeY, false);
            shark2 = Bitmap.createScaledBitmap(shark2, sharkSizeX, sharkSizeY, false);
            shark1flip = Bitmap.createBitmap(flip(shark1, 2));
            shark2flip = Bitmap.createBitmap(flip(shark2, 2));
            pirateShip = Bitmap.createBitmap(pship);
            hookSizeX = 150;
            hookSizeY = 150;
            hook = Bitmap.createScaledBitmap(hookDrawable, hookSizeX, hookSizeY, false);
            hookNormal = Bitmap.createScaledBitmap(hookDrawable, hookSizeX, hookSizeY, false);
            hookcaught1 = Bitmap.createScaledBitmap(hookcaught1, hookSizeX, hookSizeY, false);
            hookcaught2 = Bitmap.createScaledBitmap(hookcaught2, hookSizeX, hookSizeY, false);
            hookcaught3 = Bitmap.createScaledBitmap(hookcaught3, hookSizeX, hookSizeY, false);
            hookOffsetX = 85;
            hookOffsetY = 40;
            hookLeft = 45;
            hookTop = 80;
            hookRight = 50;
            hookBottom = 25;
            lowSpawn = 335;
            highSpawn = 1000;

        }
        if (screenInches < 6.0){
            Log.d("SmallDevice", Double.toString(screenInches));
            Log.d("screenWidth", Integer.toString(width));
            Log.d("screenHeight", Integer.toString(height));
            shipSpawnY = (float)(height * 0.055);
            fishSizeX = 70;
            fishSizeY = 50;
            fish1 = Bitmap.createScaledBitmap(fish1, fishSizeX, fishSizeY, false);
            fish2 = Bitmap.createScaledBitmap(fish2, fishSizeX, fishSizeY, false);
            fish3 = Bitmap.createScaledBitmap(fish3, fishSizeX, fishSizeY, false);
            fish1flip = flip(fish1, 2);
            fish2flip = flip(fish2, 2);
            fish3flip = flip(fish3, 2);
            sharkSizeX = 195;
            sharkSizeY = 97;
            shark1 = Bitmap.createScaledBitmap(shark1, sharkSizeX, sharkSizeY, false);
            shark2 = Bitmap.createScaledBitmap(shark2, sharkSizeX, sharkSizeY, false);
            shark1flip = Bitmap.createBitmap(flip(shark1, 2));
            shark2flip = Bitmap.createBitmap(flip(shark2, 2));
            hookSizeX = 100;
            hookSizeY = 100;
            pirateShip = Bitmap.createScaledBitmap(pship, 135, 135, false);
            hook = Bitmap.createScaledBitmap(hookDrawable, hookSizeX, hookSizeY, false);
            hookNormal = Bitmap.createScaledBitmap(hookDrawable, hookSizeX, hookSizeY, false);
            hookcaught1 = Bitmap.createScaledBitmap(hookcaught1, hookSizeX, hookSizeY, false);
            hookcaught2 = Bitmap.createScaledBitmap(hookcaught2, hookSizeX, hookSizeY, false);
            hookcaught3 = Bitmap.createScaledBitmap(hookcaught3, hookSizeX, hookSizeY, false);
            hookOffsetX = 55;
            hookOffsetY = 25;
            hookLeft = 25;
            hookTop = 60;
            hookRight = 35;
            hookBottom = 10;
            lowSpawn = 170;
            highSpawn = 620;
        }

    }

    public Bitmap flip(Bitmap src, int type) {
        // create new matrix for transformation
        Matrix matrix = new Matrix();
        // if vertical
        if(type == FLIP_VERTICAL) {
            // y = y * -1
            matrix.preScale(1.0f, -1.0f);
        }
        // if horizontal
        else if(type == FLIP_HORIZONTAL) {
            // x = x * -1
            matrix.preScale(-1.0f, 1.0f);
            // unknown type
        } else {
            return null;
        }
        // return transformed image
        return Bitmap.createBitmap(src, 0, 0, src.getWidth(), src.getHeight(), matrix, true);
    }

    /** Collision Detection for FISH & HOOK
     * @param bitmap1 First bitmap
     * @param x1 x-position of bitmap1 on screen.
     * @param y1 y-position of bitmap1 on screen.
     * @param bitmap2 Second bitmap.
     * @param x2 x-position of bitmap2 on screen.
     * @param y2 y-position of bitmap2 on screen.
     */
    public boolean isCollisionDetectedFishHook(Bitmap bitmap1, int x1, int y1,
                                               Bitmap bitmap2, int x2, int y2) {

        //Rect bounds1 = new Rect((int)(fishX) + 10, (int)fishY + 10, (int) fishX +bitmap1.getWidth() - 10, (int)fishY + bitmap1.getHeight() - 10);
        Rect bounds1 = new Rect((int)(spawnedFishXPosition) + 10, (int) spawnedFishYPosition + 10, (int) spawnedFishXPosition + bitmap1.getWidth()-10, (int) spawnedFishYPosition + bitmap1.getHeight()-10);
        Rect bounds2 = new Rect((int) hookX + hookLeft,(int) hookY + hookTop, (int) hookX + bitmap2.getWidth() - hookRight, (int) hookY + bitmap2.getHeight() - hookBottom);


        if (Rect.intersects(bounds1, bounds2)) {
            Rect collisionBounds = getCollisionBounds(bounds1, bounds2);
            for (int i = collisionBounds.left; i < collisionBounds.right; i++) {
                for (int j = collisionBounds.top; j < collisionBounds.bottom; j++) {
                    int bitmap1Pixel = bitmap1.getPixel(i-x1, j-y1);
                    int bitmap2Pixel = bitmap2.getPixel(i-x2, j-y2);
                    if (isFilled(bitmap1Pixel) && isFilled(bitmap2Pixel)) {
                        //Log.d("test", "true");
                        return true;
                    }
                }
            }
        }
        return false;
    }
    /** Collision Detection for FISH & HOOK
     * @param bitmap1 First bitmap
     * @param x1 x-position of bitmap1 on screen.
     * @param y1 y-position of bitmap1 on screen.
     * @param bitmap2 Second bitmap.
     * @param x2 x-position of bitmap2 on screen.
     * @param y2 y-position of bitmap2 on screen.
     */
    public boolean isCollisionDetectedSharkHook(Bitmap bitmap1, int x1, int y1,
                                               Bitmap bitmap2, int x2, int y2) {

        //Rect bounds1 = new Rect((int)(fishX) + 10, (int)fishY + 10, (int) fishX +bitmap1.getWidth() - 10, (int)fishY + bitmap1.getHeight() - 10);
        Rect bounds1 = new Rect((int)(spawnedSharkXPosition) + 10, (int) spawnedSharkYPosition + 10, (int) spawnedSharkXPosition + bitmap1.getWidth()-10, (int) spawnedSharkYPosition + bitmap1.getHeight()-10);
        Rect bounds2 = new Rect((int) hookX + hookLeft,(int) hookY + hookTop, (int) hookX + bitmap2.getWidth() - hookRight, (int) hookY + bitmap2.getHeight() - hookBottom);


        if (Rect.intersects(bounds1, bounds2)) {
            Rect collisionBounds = getCollisionBounds(bounds1, bounds2);
            for (int i = collisionBounds.left; i < collisionBounds.right; i++) {
                for (int j = collisionBounds.top; j < collisionBounds.bottom; j++) {
                    int bitmap1Pixel = bitmap1.getPixel(i-x1, j-y1);
                    int bitmap2Pixel = bitmap2.getPixel(i-x2, j-y2);
                    if (isFilled(bitmap1Pixel) && isFilled(bitmap2Pixel)) {
                        //Log.d("test", "true");
                        return true;
                    }
                }
            }
        }
        return false;
    }

    /** Collision Detection for SHIP & HOOK
     * @param bitmap1 First bitmap
     * @param x1 x-position of bitmap1 on screen.
     * @param y1 y-position of bitmap1 on screen.
     * @param bitmap2 Second bitmap.
     * @param x2 x-position of bitmap2 on screen.
     * @param y2 y-position of bitmap2 on screen.
     */
    public boolean isCollisionShipHook(Bitmap bitmap1, int x1, int y1,
                                       Bitmap bitmap2, int x2, int y2) {


        Rect bounds1 = new Rect((int)(shipXPosition) + 10, (int)shipYPosition + 10, (int) shipXPosition + bitmap1.getWidth()-10, (int)shipYPosition + bitmap1.getHeight()-10);
        Rect bounds2 = new Rect((int) hookX + hookLeft,(int) hookY + hookTop, (int) hookX + bitmap2.getWidth() - hookRight, (int) hookY + bitmap2.getHeight() - hookBottom);

        if (Rect.intersects(bounds1, bounds2)) {
            Rect collisionBounds = getCollisionBounds(bounds1, bounds2);
            for (int i = collisionBounds.left; i < collisionBounds.right; i++) {
                for (int j = collisionBounds.top; j < collisionBounds.bottom; j++) {
                    int bitmap1Pixel = bitmap1.getPixel(i-x1, j-y1);
                    int bitmap2Pixel = bitmap2.getPixel(i-x2, j-y2);
                    if (isFilled(bitmap1Pixel) && isFilled(bitmap2Pixel)) {
                        //Log.d("test", "true");
                        return true;
                    }
                }
            }
        }
        return false;
    }

    private static Rect getCollisionBounds(Rect rect1, Rect rect2) {
        int left = (int) Math.max(rect1.left, rect2.left);
        int top = (int) Math.max(rect1.top, rect2.top);
        int right = (int) Math.min(rect1.right, rect2.right);
        int bottom = (int) Math.min(rect1.bottom, rect2.bottom);
        return new Rect(left, top, right, bottom);
    }

    private static boolean isFilled(int pixel) {
        return pixel != Color.TRANSPARENT;
    }


    @Override
    public void onPause() {
        if (gameStarted == true){
            spawnHandler.removeCallbacks(spawnFish);
        }
        super.onResume();
    }

}
