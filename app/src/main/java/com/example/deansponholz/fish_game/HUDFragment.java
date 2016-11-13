package com.example.deansponholz.fish_game;

import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PathMeasure;
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
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Created by deansponholz on 11/3/16.
 */

public class HUDFragment extends Fragment {



    public ArrayList<Bitmap> arrayOfEnemies = null;


    int test = 0;


    TextView yaw_test = null;
    TextView pitch_test = null;
    TextView roll_test = null;
    Button menu_Button = null;




    float fishX, fishY;
    float pirateShipX, pirateShipY;
    float hookX, hookY;
    float shipSpawnY;


    private double yOffset;


    public Rect fishBoundsRect = new Rect(0,0,0,0);
    public Rect hookBoundsRect = new Rect(0, 0, 0, 0);

    Display display;
    WindowManager wm;
    Point size;
    int width;
    int height;

    int deviceCalibrate;

    // type definition
    public static final int FLIP_VERTICAL = 1;
    public static final int FLIP_HORIZONTAL = 2;
    public SensorHandler sensorHandler = null;
    private CalibrationFragment calibrationFragment = null;




    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_main, container, false);
        wm = (WindowManager) root.getContext().getSystemService(Context.WINDOW_SERVICE);

        sensorHandler = new SensorHandler(root.getContext());

        calibrationFragment = new CalibrationFragment();
        offSetCalculator();
        RelativeLayout fragment_main = (RelativeLayout) root.findViewById(R.id.fragment_main);
        final HUDDrawView hudDrawView = new HUDDrawView(this.getActivity());
        fragment_main.addView(hudDrawView);

        this.yaw_test = (TextView) root.findViewById(R.id.yaw_test);
        this.pitch_test = (TextView) root.findViewById(R.id.pitch_test);
        this.roll_test = (TextView) root.findViewById(R.id.roll_test);
        this.menu_Button = (Button) root.findViewById(R.id.menu_button);


        menu_Button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), MenuActivity.class);
                getActivity().startActivity(intent);
            }
        });


        return root;
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
            shipSpawnY = (float)(height * 0.2);
        }
        if (screenInches < 6.0){
            Log.d("SmallDevice", Double.toString(screenInches));
            Log.d("screenWidth", Integer.toString(width));
            Log.d("screenHeight", Integer.toString(height));
            shipSpawnY = (float)(height * 0.1);

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
        // if horizonal
        else if(type == FLIP_HORIZONTAL) {
            // x = x * -1
            matrix.preScale(-1.0f, 1.0f);
            // unknown type
        } else {
            return null;
        }
        //matrix.postRotate((float)(yPos * 4.5));

        // return transformed image
        return Bitmap.createBitmap(src, 0, 0, src.getWidth(), src.getHeight(), matrix, true);
    }


    public class HUDDrawView extends View{



        //BitMaps
        Bitmap fish = BitmapFactory.decodeResource(getResources(), R.drawable.yellowfish);
        Bitmap hook = BitmapFactory.decodeResource(getResources(), R.drawable.hook);

        Bitmap resizedFish = Bitmap.createScaledBitmap(fish, 90, 70, false);
        Bitmap flippedFish = Bitmap.createBitmap(flip(resizedFish, 2));

        Bitmap pship = BitmapFactory.decodeResource(getResources(), R.drawable.pirateship_normal);
        Bitmap pirateShip = Bitmap.createBitmap(pship);


        //onDraw
        Canvas canvas;
        Paint paint = new Paint();

        //Bitmap Positions
        int fish_offsetX, fish_offsetY;
        int ship_offsetX, ship_offSetY;


        //HookPath
        Path hookPath;
        PathMeasure hookPathMeasure;
        float[] hookPos;
        float[] hookTan;
        Matrix hookMatrix;


        //FishPath
        Path animPath;
        PathMeasure pathMeasure;
        float pathLength;
        float step;
        float distance;
        float[] pos;
        float[] tan;
        float speed;
        Matrix matrix;

        //ShipPath
        Path shipPath;
        PathMeasure shipPathMeasure;
        float shipPathLength;
        float shipStep;
        float shipDistance;
        float[] shipPos;
        float[] shipTan;
        float shipSpeed;
        Matrix shipMatrix;


        public HUDDrawView(Context context){super(context);
        initMyView();
        }

        public void initMyView(){

            //Drawing Tools
            canvas = new Canvas();
            paint = new Paint();
            paint.setColor(Color.WHITE);
            paint.setStrokeWidth(4);
            paint.setStyle(Paint.Style.STROKE);

            //initial Bitmap Positions
            fish_offsetX = resizedFish.getWidth()/2;
            fish_offsetY = resizedFish.getHeight()/2;
            ship_offsetX = pirateShip.getWidth()/2;
            ship_offSetY = pirateShip.getHeight()/2;


            //hookPath initialization
            hookPath = new Path();
            hookPath.moveTo(300, 400);
            hookPath.lineTo(200, 500);
            hookPath.close();
            hookMatrix = new Matrix();



            //hookPath.lineTo();
            //fishPath initialization
            animPath = randomPath();
            pathMeasure = new PathMeasure(animPath, false);
            speed = pathMeasure.getLength()/1000;
            pathLength = pathMeasure.getLength() / 2;
            step = 1;
            distance = 0;
            pos = new float[2];
            tan = new float[2];
            matrix = new Matrix();


            //shipPath initialization
            shipPath = new Path();
            shipPath.moveTo(width, shipSpawnY);
            shipPath.lineTo(0, shipSpawnY);
            shipPath.close();
            shipPathMeasure = new PathMeasure(shipPath, false);
            shipSpeed = shipPathMeasure.getLength()/5000;
            Log.d("ship speed", Float.toString(shipSpeed));
            shipPathLength = shipPathMeasure.getLength();
            shipPos = new float[2];
            shipTan = new float[2];
            shipDistance = 0;
            shipMatrix = new Matrix();


            //onDrawFishLine(canvas);



            final Handler spawnHandler = new Handler();
            Runnable SpawnEnemies = new Runnable(){
                public void run(){
                    //Your code here...
                    Log.d("2seconds", "spawnfish");
                    spawnHandler.postDelayed(this, 2000);
                    invalidate();
                }
            };

            spawnHandler.post(SpawnEnemies);




        }
        

        @Override
        public void onDraw(Canvas canvas){
            //http://android-er.blogspot.com/2014/05/animation-of-moving-bitmap-along-path.html



            yaw_test.setText(Double.toString(sensorHandler.zPos));
            pitch_test.setText(Double.toString(sensorHandler.xPos));
            roll_test.setText(Double.toString(sensorHandler.yPos));


            //hook and line
            onDrawFishLine(canvas);
            //ship and fish
            onDrawPath(canvas);



            if (isCollisionDetected(flippedFish, (int)fishX, (int)fishY, hook, (int)hookX, (int)hookY) == true){
                Random rnd = new Random();
                paint.setARGB(255, rnd.nextInt(256), rnd.nextInt(256), rnd.nextInt(256));
            }

            //canvas.drawPath(randomPath(), paint);
            //spawnFish(canvas);


            //hookX = (float) (-sensorHandler.xPos*15) + calibrationFragment.xOffset;
            //hookY = (float) (sensorHandler.yPos * 15) + calibrationFragment.yOffset;
            //canvas.drawBitmap(hook, hookX, hookY, paint);
            //hookBoundsRect = new Rect((int) hookX + 40,(int) hookY + 90, (int) hookX + hook.getWidth() -45, (int) hookY + hook.getHeight() - 20);
            //canvas.drawRect(hookBoundsRect, paint);
            //canvas.drawLine(hookX + 85, hookY + 40, pirateShipX + ship_offsetX, pirateShipY + ship_offSetY, paint);


            //canvas.drawPath(animPath, paint);
            //canvas.drawPath(shipPath, paint);
            //Log.d("test", Double.toString(sensorHandler.xPos));

            //hookMatrix.reset();
            //hookMatrix.postRotate((float)(Math.atan2(shipTan[1], shipTan[0])*180.0/Math.PI), hookX, hookY);
            //hookMatrix.postTranslate(pirateShipY, pirateShipY);
            //hookPath.transform(hookMatrix);
            //canvas.drawPath(hookPath, paint);




            /*

            //send fish on Path
            if(distance < pathLength){
                pathMeasure.getPosTan(distance, pos, tan);
                distance += speed;

                matrix.reset();
                float degrees = (float)(Math.atan2(tan[1], tan[0])*180.0/Math.PI);
                fishX = pos[0]-fish_offsetX;
                fishY = pos[1]-fish_offsetY;
                matrix.postRotate(degrees);
                matrix.postTranslate(fishX, fishY);

                canvas.drawBitmap(flippedFish, matrix, null);
                fishBoundsRect = new Rect((int)(fishX) + 10, (int)fishY + 10, (int) fishX +flippedFish.getWidth() - 10, (int)fishY + flippedFish.getHeight() - 10);
                canvas.drawRect(fishBoundsRect, paint);
                //Log.d("end", Float.toString(fishX));
                distance += step;
            }
            else{
                //make new random Path
                distance = 0;
                animPath = randomPath();
                pathMeasure = new PathMeasure(animPath, false);
                speed = pathMeasure.getLength()/1000;
                pathLength = pathMeasure.getLength() / 2;
                //Log.d("end", "killme");
            }

            if(shipDistance < shipPathLength){
                shipPathMeasure.getPosTan(shipDistance, shipPos, shipTan);
                shipDistance += shipSpeed;
                shipMatrix.reset();
                pirateShipX = shipPos[0]-ship_offsetX;
                pirateShipY = shipPos[1]-ship_offSetY;
                shipMatrix.postTranslate(pirateShipX, pirateShipY);
                canvas.drawBitmap(pirateShip, shipMatrix, null);

                shipDistance += shipStep;
            }
            else{
                shipDistance = 0;
                //Log.d("end", "killme");
            }

            //collision detection
            //http://stackoverflow.com/questions/5914911/pixel-perfect-collision-detection-android


            /*
            if (x > tempx) {
                canvas.drawBitmap(flipped, x + 530, y + yOffset, p);
                tempx = x;
            }

            else if (x < tempx){
                canvas.drawBitmap(resized, x + 530, y + yOffset, p);
                tempx = x;
            }
            */

            invalidate();
        }

        /*

        public void spawnFish(Canvas canvas){



            for (int i = 0; i < 2; i++) {
                Path animPath = randomPath();
                PathMeasure pathMeasureTest = new PathMeasure(animPath, false);
                Matrix matrixTest = new Matrix();
                int stepTest = 1;
                int distanceTest = 0;
                float[] posTest = new float[2];
                float[] tanTest = new float[2];

                matrixTest.reset();
                matrixTest.postTranslate(fishX, fishY);
                float speedTest = pathMeasureTest.getLength() / 1000;
                float pathLengthTest = pathMeasureTest.getLength() / 2;


                canvas.drawBitmap(flippedFish, matrixTest, paint);
                //canvas.drawBitmap(arrayOfEnemies.get(i), matrixTest, null);
            }




            invalidate();

        }
        */


        public void onDrawFishLine(Canvas canvas){

            hookX = (float) (-sensorHandler.xPos*15) + calibrationFragment.xOffset;
            hookY = (float) (sensorHandler.yPos * 15) + calibrationFragment.yOffset;
            canvas.drawBitmap(hook, hookX, hookY, paint);
            hookBoundsRect = new Rect((int) hookX + 40,(int) hookY + 90, (int) hookX + hook.getWidth() -45, (int) hookY + hook.getHeight() - 20);
            canvas.drawRect(hookBoundsRect, paint);


            canvas.drawLine(hookX + 85, hookY + 40, pirateShipX + ship_offsetX, pirateShipY + ship_offSetY, paint);

            invalidate();

        }



        public void onDrawPath(Canvas canvas){
            canvas.drawPath(animPath, paint);
            canvas.drawPath(shipPath, paint);


            if(distance < pathLength){
                pathMeasure.getPosTan(distance, pos, tan);
                distance += speed;

                matrix.reset();
                float degrees = (float)(Math.atan2(tan[1], tan[0])*180.0/Math.PI);
                fishX = pos[0]-fish_offsetX;
                fishY = pos[1]-fish_offsetY;
                matrix.postRotate(degrees);
                matrix.postTranslate(fishX, fishY);

                canvas.drawBitmap(flippedFish, matrix, null);
                fishBoundsRect = new Rect((int)(fishX) + 10, (int)fishY + 10, (int) fishX +flippedFish.getWidth() - 10, (int)fishY + flippedFish.getHeight() - 10);
                canvas.drawRect(fishBoundsRect, paint);
                //Log.d("end", Float.toString(fishX));
                distance += step;
            }
            else{
                //make new random Path
                distance = 0;
                animPath = randomPath();
                pathMeasure = new PathMeasure(animPath, false);
                speed = pathMeasure.getLength()/1000;
                pathLength = pathMeasure.getLength() / 2;
                //Log.d("end", "killme");
            }

            if(shipDistance < shipPathLength){
                shipPathMeasure.getPosTan(shipDistance, shipPos, shipTan);
                shipDistance += shipSpeed;
                shipMatrix.reset();
                pirateShipX = shipPos[0]-ship_offsetX;
                pirateShipY = shipPos[1]-ship_offSetY;
                shipMatrix.postTranslate(pirateShipX, pirateShipY);
                canvas.drawBitmap(pirateShip, shipMatrix, null);

                shipDistance += shipStep;
            }
            else{
                shipDistance = 0;
                //Log.d("end", "killme");
            }

            invalidate();

        }

    }

    public Path randomPath(){

        Random r = new Random();
        Path animPath = new Path();
        float top = (float) (height * 0.90);

        float bottom = (float) (height * 0.15);
        int topRound = Math.round(top);
        int bottomRound = Math.round(bottom);


        int randSpawn = r.nextInt(topRound-bottomRound) + bottomRound;
        int randEnd = r.nextInt(topRound-bottomRound) + bottomRound;
        //Log.d("start", Integer.toString(randSpawn));
        //Log.d("end", Integer.toString(randEnd));
        animPath.moveTo(0, randSpawn);
        animPath.lineTo(width, randEnd);

        animPath.close();

        return animPath;
    }

    /**
     * @param bitmap1 First bitmap
     * @param x1 x-position of bitmap1 on screen.
     * @param y1 y-position of bitmap1 on screen.
     * @param bitmap2 Second bitmap.
     * @param x2 x-position of bitmap2 on screen.
     * @param y2 y-position of bitmap2 on screen.
     */
    public  boolean isCollisionDetected(Bitmap bitmap1, int x1, int y1,
                                              Bitmap bitmap2, int x2, int y2) {

        Rect bounds1 = new Rect((int)(fishX) + 10, (int)fishY + 10, (int) fishX +bitmap1.getWidth() - 10, (int)fishY + bitmap1.getHeight() - 10);
        Rect bounds2 = new Rect((int) hookX + 40,(int) hookY + 90, (int) hookX + bitmap2.getWidth() -45, (int) hookY + bitmap2.getHeight() - 20);

        if (Rect.intersects(bounds1, bounds2)) {
            Rect collisionBounds = getCollisionBounds(bounds1, bounds2);
            for (int i = collisionBounds.left; i < collisionBounds.right; i++) {
                for (int j = collisionBounds.top; j < collisionBounds.bottom; j++) {
                    int bitmap1Pixel = bitmap1.getPixel(i-x1, j-y1);
                    int bitmap2Pixel = bitmap2.getPixel(i-x2, j-y2);
                    if (isFilled(bitmap1Pixel) && isFilled(bitmap2Pixel)) {

                        //bitmap1.recycle();
                        //bitmap1.eraseColor(0);
                        Log.d("test", "true");
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

}
