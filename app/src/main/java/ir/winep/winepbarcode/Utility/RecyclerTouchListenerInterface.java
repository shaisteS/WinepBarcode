package ir.winep.winepbarcode.Utility;

import android.view.View;

/**
 * Created by ShaisteS on 5/25/2016.
 */
public interface RecyclerTouchListenerInterface {
    void onClick(View view, int position);
    void onLongClick(View view, int position);
}