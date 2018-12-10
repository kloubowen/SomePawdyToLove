package cs371m.bowen.somepawtytolove;

import android.support.v7.widget.RecyclerView;
import android.view.MotionEvent;
import android.view.View;

// Code adapted from https://stackoverflow.com/questions/17520750/list-view-item-swipe-left-and-swipe-right
// and adapted again from Homework 5 of CS 371M and is no longer used
public class PetSwipeDetector implements RecyclerView.OnItemTouchListener {
    @Override
    public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN: {
                mSwipeDetected = false;
                downX = event.getX();
                downY = event.getY();
            }
            case MotionEvent.ACTION_UP: {
                upY = event.getY();
                upX = event.getX();
                float deltaY = downY - upY;
                float deltaX = downX - upX;

                if ( Math.abs(deltaX) < Math.abs(deltaY) ) {
                    return false;
                }
                if (Math.abs(deltaX) > MIN_DISTANCE) {
                    // left or right
                    if (deltaX < 0 || deltaX > 0) {
                        mSwipeDetected = true;
                    }
                    View child = rv.findChildViewUnder(upX, upY);
                    if (child != null)
                        child.callOnClick();
                }
            }
        }
        return false;
    }

    @Override
    public void onTouchEvent(RecyclerView rv, MotionEvent e) {
        /* we never return true from intercept so this call should be impossible! */
        throw new Error("onTouchEvent Should not be called!");
    }

    @Override
    public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {
        /* unused */
    }

    private static final int MIN_DISTANCE = 100;
    private float downX, downY, upX, upY;
    private boolean mSwipeDetected = false;

    public boolean swipeDetected() {
        return mSwipeDetected;
    }
}