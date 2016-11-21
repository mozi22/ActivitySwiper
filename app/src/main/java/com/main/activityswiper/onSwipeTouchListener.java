package com.main.activityswiper;

import android.app.Activity;
import android.content.Context;
import android.graphics.Point;
import android.view.Display;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

/**
 * Created by Muazzam on 8/11/2016.
 */
public class onSwipeTouchListener implements View.OnTouchListener {

    private final GestureDetector gestureDetector;
    private View view;
    int dX, dY;
    float initialViewLocationX;
    float initialViewLocationY;
    private View grabbedView;
    private Activity activity;

    private SlideDirection direction;

    int screen_height = 0;
    int screen_width = 0;

    private int _xDelta;
    private int _yDelta;


    public onSwipeTouchListener (Activity ac,View parentView,View childView, SlideDirection direction){

        this.view = parentView;

        initialViewLocationY = childView.getY();
        initialViewLocationX = childView.getX();

        gestureDetector = new GestureDetector(ac.getApplicationContext(), new GestureListener());

        this.grabbedView = childView;
        this.activity = ac;
        this.direction = direction;


        Display display = ac.getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        screen_height = size.y;
        screen_width = size.x;

    }


    @Override
    public boolean onTouch(View v, MotionEvent event) {

        final int X = (int) event.getRawX();
        final int Y = (int) event.getRawY();

        switch (event.getAction() & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_DOWN:
                FrameLayout.LayoutParams lParams = (FrameLayout.LayoutParams) v.getLayoutParams();

                if(this.direction == SlideDirection.SLIDE_BOTTOM || this.direction == SlideDirection.SLIDE_TOP){
                    _yDelta = Y - lParams.topMargin;
                }
                else if(this.direction == SlideDirection.SLIDE_LEFT || this.direction == SlideDirection.SLIDE_RIGHT){
                    _xDelta = X - lParams.leftMargin;
                }
            case MotionEvent.ACTION_UP:

                if(SlideDirection.SLIDE_BOTTOM == this.direction){
                    if(event.getRawY() >= (60*screen_height)/100){
                        CloseActivity();
                    }
                }
                else if(SlideDirection.SLIDE_TOP == this.direction){
                    if(event.getRawY() <= (60*screen_height)/100){
                        CloseActivity();
                    }
                }
                else if(SlideDirection.SLIDE_RIGHT == this.direction){
                    if(event.getRawX() >= (60*screen_width)/100){
                        CloseActivity();
                    }
                }
                else if(SlideDirection.SLIDE_LEFT == this.direction){
                    if(event.getRawX() <= (60*screen_width)/100){
                        CloseActivity();
                    }
                }
                // if we've slided down the activity more than 60% of screen height.
                else{
                    // Slide the view back to the original(default) location.
                    MoveView(200, initialViewLocationX,initialViewLocationY);
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

                int viewLocationY = 0;
                int viewLocationX = 0;
                if(this.direction == SlideDirection.SLIDE_BOTTOM){
                    viewLocationY = Y - _yDelta;
                }
                else if(this.direction == SlideDirection.SLIDE_TOP){
                    viewLocationY = Y + _yDelta;
                }
                else if(this.direction == SlideDirection.SLIDE_LEFT){
                    viewLocationX = X - _xDelta;
                }
                else if(this.direction == SlideDirection.SLIDE_RIGHT){
                    viewLocationX = X + _xDelta;
                }

                if(AllowMovement(viewLocationX,viewLocationY)){
                    MoveView(0, viewLocationX, viewLocationY);
                }
                break;
        }
        return gestureDetector.onTouchEvent(event);
    }

    private void CloseActivity(){
        // than close the activity

        if(SlideDirection.SLIDE_BOTTOM == this.direction){
            MoveView(200, dX, screen_height);
        }
        else if(SlideDirection.SLIDE_TOP == this.direction){
            MoveView(200, dX, 0);
        }
        else if(SlideDirection.SLIDE_LEFT == this.direction){
            MoveView(200, 0, dY);
        }
        else if(SlideDirection.SLIDE_RIGHT == this.direction){
            MoveView(200,screen_width, dY);
        }

        Thread thread = new Thread(){
            @Override
            public void run() {
                try {
                    Thread.sleep(300); // As I am using LENGTH_LONG in Toast
                    activity.finish();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
        thread.start();

    }

    // this function will check that the view is not being moved in the opposite
    // direction from the one specified using the SlideDirection parameter
    private boolean AllowMovement(int locationX,int locationY){
        if(this.direction == SlideDirection.SLIDE_BOTTOM){
            if(locationY < 0){
                return false;
            }
        }
        else if(this.direction == SlideDirection.SLIDE_TOP){
            if(locationY > view.getHeight()){
                return false;
            }
        }
        else if(this.direction == SlideDirection.SLIDE_LEFT){
            if(locationX > view.getWidth()){
                return false;
            }
        }
        else if(this.direction == SlideDirection.SLIDE_RIGHT){
            if(locationX < view.getWidth()){
                return false;
            }
        }

        return true;
    }

    // Moves the view on the screen.
    private void MoveView(int duration,float locationX,float locationY){

        view.animate()
                .x(locationX)
                .y(locationY)
                .setDuration(duration)
                .start();
    }


    // The class which listens to the gestures we're making in the screen.
    // No need to play around with it unless you really need something special.
    private final class GestureListener extends GestureDetector.SimpleOnGestureListener {

        private static final int SWIPE_THRESHOLD = 100;
        private static final int SWIPE_VELOCITY_THRESHOLD = 100;

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
