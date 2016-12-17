package com.main.activityswiper;

import android.app.Activity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.widget.Button;
import android.widget.RelativeLayout;

/**
 * Created by Muazzam on 11/18/2016.
 */
public class NewActivity extends Activity{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.newactivity);


        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);

        int width = metrics.widthPixels;
        int height = metrics.heightPixels;

        getWindow().setLayout((int) (width * .9), (int) (height * .9));
        Button btn = (Button)findViewById(R.id.button2);

        RelativeLayout relativeLayout = (RelativeLayout)findViewById(R.id.hanger);

        SwiperDialogActivity swiperDialogActivity = new SwiperDialogActivity(this);
        swiperDialogActivity.SWIPERDIALOG_PARENTVIEW = relativeLayout;
        swiperDialogActivity.SWIPERDIALOG_SWIPEDIRECTION = SwipeDirection.SLIDE_BOTTOM;
        btn.setOnTouchListener(swiperDialogActivity);

    }


}
