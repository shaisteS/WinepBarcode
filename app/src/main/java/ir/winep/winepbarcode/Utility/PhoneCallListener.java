package ir.winep.winepbarcode.Utility;

import android.content.Intent;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;

import ir.winep.winepbarcode.Camera.ContinuousCaptureActivity;

/**
 * Created by ShaisteS on 1394/10/11.
 * if you have question call this phone
 */
public class PhoneCallListener extends PhoneStateListener {

    private boolean isPhoneCalling = false;

    @Override
    public void onCallStateChanged(int state, String incomingNumber) {

        String LOG_TAG = "LOGGING 123";
        if (TelephonyManager.CALL_STATE_RINGING == state) {
            // phone ringing
            Log.i(LOG_TAG, "RINGING, number: " + incomingNumber);
        }

        if (TelephonyManager.CALL_STATE_OFFHOOK == state) {
            // active
            Log.i(LOG_TAG, "OFF_HOOK");

            isPhoneCalling = true;
        }

        if (TelephonyManager.CALL_STATE_IDLE == state) {
            // run when class initial and phone call ended,
            // need detect flag from CALL_STATE_OFF_HOOK
            Log.i(LOG_TAG, "IDLE");

            if (isPhoneCalling) {

                Log.i(LOG_TAG, "restart app");

                // restart app
                Intent i = Configuration.getInstance().applicationContext.getPackageManager()
                        .getLaunchIntentForPackage(
                                Configuration.getInstance().applicationContext.getPackageName());
                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                i.setClass(Configuration.getInstance().applicationContext,ContinuousCaptureActivity.class);
                Configuration.getInstance().applicationContext.startActivity(i);
                isPhoneCalling = false;
            }

        }
    }
}
