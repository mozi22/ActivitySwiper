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
    float initialViewLocation;
    private View grabbedView;
    private Activity activity;

    private SlideDirection direction;

    int screen_height = 0;
    int screen_width = 0;

    private int _xDelta;
    private int _yDelta;


    public onSwipeTouchListener (Activity ac,View parentView,View childView, SlideDirection direction){

        this.view = parentView;
        initialViewLocation = this.view.getY();
        gestureDetector = new GestureDetector(ac.getApplicationContext(), new GestureListener());
        this.grabbedView = childView;
        this.activity = ac;
        this.direction = direction;

        this.initialViewLocation = grabbedView.getY();

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

                        // than close the activity
                        MoveView(200, screen_height);
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
                }

                // if we've slided down the activity more than 60% of screen height.
                else{
                    // Slide the view back to the original(default) location.
                    MoveView(200, initialViewLocation);
                }
                break;
            case MotionEvent.ACTION_POINTER_DOWN:
                break;
            case MotionEvent.ACTION_POINTER_UP:
                break;
            case MotionEvent.ACTION_MOVE:

                // here we calculate the new location of the view on dragging. And pass it
                // to MoveView function if AllowMovement allows us to do it.

                int viewLocation = 0;
                if(this.direction == SlideDirection.SLIDE_BOTTOM){
                    viewLocation = Y - _yDelta;
                }
                else if(this.direction == SlideDirection.SLIDE_TOP){
                    viewLocation = Y + _yDelta;
                }
                else if(this.direction == SlideDirection.SLIDE_LEFT){
                    viewLocation = X - _xDelta;
                }
                else if(this.direction == SlideDirection.SLIDE_RIGHT){
                    viewLocation = X + _xDelta;
                }

                if(AllowMovement(viewLocation)){
                    MoveView(0, viewLocation);
                }
                break;
        }
        return gestureDetector.onTouchEvent(event);
    }

    private void CloseActivity(){

    }

    // this function will check that the view is not being moved in the opposite
    // direction from the one specified.
    private boolean AllowMovement(int location){
        if(this.direction == SlideDirection.SLIDE_BOTTOM){
            if(location>0){
                return false;
            }
        }
        else if(this.direction == SlideDirection.SLIDE_TOP){
            if(location > view.getHeight()){
                return false;
            }
        }
        else if(this.direction == SlideDirection.SLIDE_LEFT){
            if(location > view.getWidth()){
                return false;
            }
        }
        else if(this.direction == SlideDirection.SLIDE_RIGHT){
            if(location < view.getWidth()){
                return false;
            }
        }

        return true;
    }

    // Moves the view on the screen.
    private void MoveView(int duration,float locationY){

        view.animate()
                .x(dX)
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
