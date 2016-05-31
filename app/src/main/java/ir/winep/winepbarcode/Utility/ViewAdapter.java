package ir.winep.winepbarcode.Utility;

import android.content.Context;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AbsListView;

/**
 * Created by ShaisteS on 5/25/2016.
 */
public interface ViewAdapter {
    Context getContext();
    int getWidth();
    int getChildCount();
    void getLocationOnScreen(int[] locations);
    View getChildAt(int index);
    int getChildPosition(View position);
    void requestDisallowInterceptTouchEvent(boolean disallowIntercept);
    void onTouchEvent(MotionEvent e);
    Object makeScrollListener(AbsListView.OnScrollListener listener);
}
