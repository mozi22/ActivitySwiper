# ActivitySwiper

ActivitySwiper is a simple class implemented to build activities that can be dragged and closed when pinched on a particular element inside that activity.

### HOW TO USE

Simply copy the class `SwiperDialogActivity` to your program and use it in the following way.

    // A button inside the activity that will act as a draggable element
    Button btn = (Button)findViewById(R.id.button);
    
    // The parent container view of the activity
    RelativeLayout parent = (RelativeLayout)findViewById(R.id.parent);

    // New instance of the SwiperDialogActivity
    SwiperDialogActivity swiperDialogActivity = new SwiperDialogActivity(this);
    
    // Assigning the parent view which should be dragged
    swiperDialogActivity.SWIPERDIALOG_PARENTVIEW = parent;

    // Which direction we want to swipe and close the activity
    swiperDialogActivity.SWIPERDIALOG_SWIPEDIRECTION = SwipeDirection.SLIDE_BOTTOM;
    
    // Assign the onTouch event to the button such that the whole activity can be dragged using that button.
    btn.setOnTouchListener(swiperDialogActivity);

**Note:** In this example, if you would have used the same parent view instead of the button for dragging the activity. Than the whole activity will be draggable from anywhere you pinch on the activity.

### Activity Dialog

You can make your activity look like a dialog using the following code.

    DisplayMetrics metrics = new DisplayMetrics();
    getWindowManager().getDefaultDisplay().getMetrics(metrics);

    int width = metrics.widthPixels;
    int height = metrics.heightPixels;

    getWindow().setLayout((int) (width * .9), (int) (height * .9));
        
Than simply use the `SwiperDialogActivity` to drag and close the activity.
 
 
 ## License
```
Copyright 2016 Muazzam Ali

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

   http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
```

 
