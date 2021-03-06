package com.main.activityswiper;

import android.app.Activity;
import android.graphics.Point;
import android.util.Log;
import android.view.Display;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by Muazzam on 8/11/2016.
 *
 * Todo:
 *
 * Enable User to set the percentage of the screenSize dragged in order to close the activity
 * If you slide bottom by dragging through button - the activity doesn't close ( it's a bug )
 *
 */
public class SwiperDialogActivity implements View.OnTouchListener {


    private final GestureDetector gestureDetector;
    private int dX, dY;
    private float initialViewLocationX;
    private float initialViewLocationY;

    // an enum that users will be able to use to specify the direciton in which
    // they want the slide functionality
    protected SwipeDirection SWIPERDIALOG_SWIPEDIRECTION = com.main.activityswiper.SwipeDirection.SLIDE_BOTTOM;

    // manages how fast the activity should slide.
    private int SWIPERDIALOG_SWIPESPEED = 200;

    protected View SWIPERDIALOG_PARENTVIEW;


    // the width and height of the activity screen ( not the view ).
    private int screen_height = 0;
    private int screen_width = 0;

    private Activity activity;


    // different in the actual position of the view and the place where the user touched.
    // this helps us to control the view and not jump it around when the user touches
    // the screen.
    private int _xDelta;
    private int _yDelta;

    // the current location of the View, until where it is dragged.
    private int viewLocationY = 0;
    private int viewLocationX = 0;


    public SwiperDialogActivity(Activity activity){

        super();

        this.activity = activity;

        gestureDetector = new GestureDetector(activity, new GestureListener());

        final ViewGroup viewGroup = (ViewGroup) ((ViewGroup) activity
                .findViewById(android.R.id.content)).getChildAt(0);

        this.SWIPERDIALOG_PARENTVIEW = viewGroup;

        initialViewLocationY = this.SWIPERDIALOG_PARENTVIEW.getY();
        initialViewLocationX = this.SWIPERDIALOG_PARENTVIEW.getX();

        CalculateScreenDimensions();
    }


    @Override
    public boolean onTouch(View v, MotionEvent event) {

        final int X = (int) event.getRawX();
        final int Y = (int) event.getRawY();

        switch (event.getAction() & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_DOWN:
                _yDelta = Y;
                _xDelta = X;
            case MotionEvent.ACTION_UP:

                boolean ActivityClosed = false;
                if(com.main.activityswiper.SwipeDirection.SLIDE_BOTTOM == this.SWIPERDIALOG_SWIPEDIRECTION){

                    // if the user has slided down more than 40% ( 40% is the height from the top )
                    if(SWIPERDIALOG_PARENTVIEW.getY()  >= ((float)40/100)* screen_height){

                        ActivityClosed = true;
                        CloseActivity();
                    }
                }
                else if(com.main.activityswiper.SwipeDirection.SLIDE_TOP == this.SWIPERDIALOG_SWIPEDIRECTION){


                    // if the user has slided up more than 60% ( 60% is the height from the top )
                    if((SWIPERDIALOG_PARENTVIEW.getY()+SWIPERDIALOG_PARENTVIEW.getHeight()) <= (((float)60/100)* screen_height)){
                        ActivityClosed = true;
                        CloseActivity();
                    }
                }
                else if(com.main.activityswiper.SwipeDirection.SLIDE_RIGHT == this.SWIPERDIALOG_SWIPEDIRECTION){


                    // if the user slides more than 40% ( 40% is the width counted from the left of the screen )
                    if((SWIPERDIALOG_PARENTVIEW.getX()) >= ((float)40/100)* screen_width){
                        ActivityClosed = true;
                        CloseActivity();
                    }
                }
                else if(com.main.activityswiper.SwipeDirection.SLIDE_LEFT == this.SWIPERDIALOG_SWIPEDIRECTION){

                    /*
                     *  ParentView.getX() + ParentView.getWidth()
                     *
                     *  We used the above formula because we want to make sure the right corner of
                     *  the ParentView passes more than 60% towards the left in order to close the
                     *  activity.
                     */

                    if((SWIPERDIALOG_PARENTVIEW.getX() + SWIPERDIALOG_PARENTVIEW.getWidth()) <= ((float)60/100)* screen_width){
                        ActivityClosed = true;
                        CloseActivity();
                    }
                }

                // if the activity is not slided enough to close it. Than slide it back
                // to it's original(default) position
                if(!ActivityClosed){
                    MoveView(SWIPERDIALOG_SWIPESPEED, initialViewLocationX,initialViewLocationY);
                }
                break;
            case MotionEvent.ACTION_POINTER_DOWN:
                break;
            case MotionEvent.ACTION_POINTER_UP:
                break;
            case MotionEvent.ACTION_MOVE:

                // here we calculate the new locationX and locationY
                // of the view on dragging. And pass it to MoveView function
                // if AllowMovement allows us to do it.

                if(this.SWIPERDIALOG_SWIPEDIRECTION == com.main.activityswiper.SwipeDirection.SLIDE_BOTTOM || this.SWIPERDIALOG_SWIPEDIRECTION == com.main.activityswiper.SwipeDirection.SLIDE_TOP){
                    viewLocationY = Y - _yDelta;
                }
                else if(this.SWIPERDIALOG_SWIPEDIRECTION == com.main.activityswiper.SwipeDirection.SLIDE_LEFT || this.SWIPERDIALOG_SWIPEDIRECTION == com.main.activityswiper.SwipeDirection.SLIDE_RIGHT){
                    viewLocationX = X - _xDelta;
                }

                if(AllowMovement(viewLocationX,viewLocationY)){
                    MoveView(0, viewLocationX, viewLocationY);
                }
                break;
        }
        return gestureDetector.onTouchEvent(event);
    }
    // Moves the view on the screen.
    private void MoveView(int duration,float locationX,float locationY){

        SWIPERDIALOG_PARENTVIEW.animate()
                .x(locationX)
                .y(locationY)
                .setDuration(duration)
                .start();
    }

    private void CloseActivity(){
        // than close the activity

        if(com.main.activityswiper.SwipeDirection.SLIDE_BOTTOM == this.SWIPERDIALOG_SWIPEDIRECTION){
            MoveView(this.SWIPERDIALOG_SWIPESPEED,dX, screen_height);
        }
        else if(com.main.activityswiper.SwipeDirection.SLIDE_TOP == this.SWIPERDIALOG_SWIPEDIRECTION){
            MoveView(this.SWIPERDIALOG_SWIPESPEED,dX, -screen_height);
        }
        else if(com.main.activityswiper.SwipeDirection.SLIDE_LEFT == this.SWIPERDIALOG_SWIPEDIRECTION){

            /*
             *   0 represents the top right X location of the ParentView.
             *   So if the user slides left more than 60% of the total view area than
             *   animate the view to -(width of the parent view)
             *   this will give us a -ve value equal to the width of the view.
             *   Which means the right side of the view will go to 0 hence animating the
             *   view towards the left.
             */

            MoveView(this.SWIPERDIALOG_SWIPESPEED, -screen_width, dY);
        }
        else if(com.main.activityswiper.SwipeDirection.SLIDE_RIGHT == this.SWIPERDIALOG_SWIPEDIRECTION){
            MoveView(this.SWIPERDIALOG_SWIPESPEED,screen_width, dY);
        }

        // this is used such that the animation for sliding completes before the activity
        // disappears.
        Thread thread = new Thread(){
            @Override
            public void run() {
                try {
                    Thread.sleep(200); // As I am using LENGTH_LONG in Toast
                    activity.finish();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
        thread.start();

    }


    // this function will check that the view is not being moved in the opposite
    // direction from the one specified using the SwipeDirection parameter
    private boolean AllowMovement(int locationX,int locationY){
        if(this.SWIPERDIALOG_SWIPEDIRECTION == com.main.activityswiper.SwipeDirection.SLIDE_BOTTOM){
            if(locationY < 0){
                return false;
            }
        }
        else if(this.SWIPERDIALOG_SWIPEDIRECTION == com.main.activityswiper.SwipeDirection.SLIDE_TOP){
            if(locationY > 0){
                return false;
            }
        }
        else if(this.SWIPERDIALOG_SWIPEDIRECTION == com.main.activityswiper.SwipeDirection.SLIDE_LEFT){
            if(locationX > 0){
                return false;
            }
        }
        else if(this.SWIPERDIALOG_SWIPEDIRECTION == com.main.activityswiper.SwipeDirection.SLIDE_RIGHT){
            if(locationX < 0){
                return false;
            }
        }
        return true;
    }



    private void CalculateScreenDimensions(){

        Display display = activity.getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);

        screen_height = size.y;
        screen_width = size.x;
    }


    // The class which listens to the gestures we're making in the screen.
    // No need to play around with it unless you really need something special.
    private final class GestureListener extends GestureDetector.SimpleOnGestureListener {

        private static final int SWIPE_THRESHOLD = 1;
        private static final int SWIPE_VELOCITY_THRESHOLD = 10;

        @Override
        public boolean onDown(MotionEvent e) {
            return true;
        }

        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            boolean result = false;
            try {
                float diffY = e2.getY() - e1.getY();
                float diffX = e2.getX() - e1.getX();
                if (Math.abs(diffX) > Math.abs(diffY)) {
                    if (Math.abs(diffX) > SWIPE_THRESHOLD && Math.abs(velocityX) > SWIPE_VELOCITY_THRESHOLD) {
                        if (diffX > 0) {
                            onSwipeRight();
                        } else {
                            onSwipeLeft();
                        }
                    }
                    result = true;
                }
                else if (Math.abs(diffY) > SWIPE_THRESHOLD && Math.abs(velocityY) > SWIPE_VELOCITY_THRESHOLD) {
                    if (diffY > 0) {
                        onSwipeBottom();
                    } else {
                        onSwipeTop();
                    }
                }
                result = true;

            } catch (Exception exception) {
                exception.printStackTrace();
            }
            return result;
        }
    }

    public void onSwipeRight() {
    }

    public void onSwipeLeft() {
    }

    public void onSwipeTop() {
    }

    public void onSwipeBottom() {
    }
}
