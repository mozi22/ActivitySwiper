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
    float dX, dY;
    float initialViewLocation;
    View grabbedView;
    Activity activity;

    int screen_height = 0;

    private int _xDelta;
    private int _yDelta;


    public onSwipeTouchListener (Activity ac,View parentView,View childView){

        this.view = parentView;
        initialViewLocation = this.view.getY();
        gestureDetector = new GestureDetector(ac.getApplicationContext(), new GestureListener());
        this.grabbedView = childView;
        this.activity = ac;

        this.initialViewLocation = grabbedView.getY();

        Display display = ac.getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        screen_height = size.y;

    }


    @Override
    public boolean onTouch(View v, MotionEvent event) {

        final int X = (int) event.getRawX();
        final int Y = (int) event.getRawY();

        switch (event.getAction() & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_DOWN:
                RelativeLayout.LayoutParams lParams = (RelativeLayout.LayoutParams) v.getLayoutParams();
                _xDelta = X - lParams.leftMargin;
                _yDelta = Y - lParams.topMargin;
            case MotionEvent.ACTION_UP:

                // if we've slided down the activity more than 60% of screen height.
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
                else{
                    MoveView(200, initialViewLocation);
                }
                break;
            case MotionEvent.ACTION_POINTER_DOWN:
                dX = view.getX();
                dY = view.getY() - event.getRawY();
                break;
            case MotionEvent.ACTION_POINTER_UP:
                break;
            case MotionEvent.ACTION_MOVE:

//                MoveView(0, event.getRawY() - ( grabbedView.getHeight() - event.getRawY()));
                RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) v.getLayoutParams();
                layoutParams.leftMargin = X - _xDelta;
                layoutParams.topMargin = Y - _yDelta;
                layoutParams.rightMargin = -250;
                layoutParams.bottomMargin = -250;
                view.setLayoutParams(layoutParams);
                break;
        }
        return gestureDetector.onTouchEvent(event);
    }

    public void MoveView(int duration,float locationY){

        view.animate()
                .x(dX)
                .y(locationY)
                .setDuration(duration)
                .start();
    }

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
