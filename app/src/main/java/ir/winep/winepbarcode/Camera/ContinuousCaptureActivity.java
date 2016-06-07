package ir.winep.winepbarcode.Camera;

/**
 * Created by ShaisteS on 5/23/2016.
 */

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.google.zxing.ResultPoint;
import com.journeyapps.barcodescanner.BarcodeCallback;
import com.journeyapps.barcodescanner.BarcodeResult;
import com.journeyapps.barcodescanner.CompoundBarcodeView;


import java.text.DateFormat;
import java.util.Date;
import java.util.List;

import ir.winep.winepbarcode.DataModel.BarcodeInformation;
import ir.winep.winepbarcode.DataModel.DataBaseHandler;
import ir.winep.winepbarcode.Presenter.DialogGetTitleBarcode;
import ir.winep.winepbarcode.Presenter.MainActivity;
import ir.winep.winepbarcode.R;
import ir.winep.winepbarcode.Utility.Configuration;
import ir.winep.winepbarcode.Utility.UserToolBarManage;


public class ContinuousCaptureActivity extends Activity implements CompoundBarcodeView.TorchListener {

    private static final String TAG = ContinuousCaptureActivity.class.getSimpleName();
    // permission request codes need to be < 256
    private static final int RC_HANDLE_CAMERA_PERM =1;
    private static final int RC_HANDLE_CALL_PHONE=2;
    private static final int RC_HANDLE_READW_PHONE_STATE=3;
    private CompoundBarcodeView barcodeView;
    private ImageButton switchFlashlightButton;

    private Context context;
    private UserToolBarManage userToolBarManage;
    private LinearLayout linearLayoutUserToolbar;
    private RelativeLayout relativeLayoutMain;
    private ImageButton btnVisitWeb;
    private ImageButton btnSearch;
    private ImageButton saveBarcode;
    private ImageButton btnShare;
    private ImageButton btnCallPhone;
    private BarcodeInformation barcodeInformation;
    private boolean flashStatus=false;//false=off
    private MediaPlayer mediaPlayer;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.continuous_scan);
        Configuration.getInstance().applicationContext = getBaseContext();
        context=this;
        mediaPlayer = MediaPlayer.create(ContinuousCaptureActivity.this, R.raw.road_runner);


        int rc = ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA);
        if (rc == PackageManager.PERMISSION_GRANTED) {
        }
        else {
            requestCameraPermission();
        }

        int phonePermission=ActivityCompat.checkSelfPermission(this,Manifest.permission.CALL_PHONE);
        if (phonePermission==PackageManager.PERMISSION_GRANTED){

        }
        else {
            requestPhonePermission();
        }
        int phoneReadState=ActivityCompat.checkSelfPermission(this,Manifest.permission.READ_PHONE_STATE);
        if(phoneReadState==PackageManager.PERMISSION_GRANTED){

        }
        else {
            requestPhoneStateread();
        }

        barcodeView = (CompoundBarcodeView) findViewById(R.id.barcode_scanner);
        barcodeView.setTorchListener(this);
        barcodeView.setSoundEffectsEnabled(true);
        barcodeView.decodeContinuous(callback);

        linearLayoutUserToolbar =(LinearLayout)findViewById(R.id.userToolBar);
        relativeLayoutMain=(RelativeLayout)findViewById(R.id.main_layout_continuous_scan);
        saveBarcode=(ImageButton)findViewById(R.id.btnSave);
        btnSearch=(ImageButton)findViewById(R.id.btnSearch);
        btnVisitWeb=(ImageButton)findViewById(R.id.btnVisitWeb);
        btnShare=(ImageButton)findViewById(R.id.btnShare);
        btnCallPhone=(ImageButton)findViewById(R.id.btnCall);
        userToolBarManage=UserToolBarManage.getInstance();

        btnVisitWeb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String url=barcodeInformation.getBarcodeContentURL();
                userToolBarManage.openBrowser(context,url);
            }
        });

        btnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String url=barcodeInformation.getBarcodeContent();
                url=userToolBarManage.createURlForSearchInGoogle(url);
                userToolBarManage.openBrowser(context,url);
            }
        });

        saveBarcode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(barcodeInformation!=null) {
                    Bundle bundle=new Bundle();
                    bundle.putSerializable("barcodeInformation", barcodeInformation);
                    DialogGetTitleBarcode dialogGetTitleBarcode=DialogGetTitleBarcode.getInstance();
                    dialogGetTitleBarcode.setArguments(bundle);
                    dialogGetTitleBarcode.show(getFragmentManager(), "getTitleBarcode");
                }
            }
        });

        btnShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                userToolBarManage.shareContent(context,barcodeInformation.getBarcodeContent());
            }
        });

        btnCallPhone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                userToolBarManage.callPhone(context,barcodeInformation.getBarcodeContentPhone());
                userToolBarManage.phoneManager(context);
            }
        });

        switchFlashlightButton = (ImageButton)findViewById(R.id.switch_flashlight);
        switchFlashlightButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (flashStatus)
                    barcodeView.setTorchOff();
                else
                    barcodeView.setTorchOn();
            }
        });
        if (!hasFlash()) {
            switchFlashlightButton.setVisibility(View.GONE);
        }
    }


    private BarcodeCallback callback = new BarcodeCallback() {
        @Override
        public void barcodeResult(BarcodeResult result) {
            if (result.getText() != null) {
                //startService(serviceMusicPlay);
                mediaPlayer.start();
                barcodeView.setTorchOff();
                pause(barcodeView);
                barcodeInformation=UserToolBarManage.getInstance().createBarcodeInformation(result.getText(),
                        DateFormat.getDateTimeInstance().format(new Date()));
                customizeUserToolbar(barcodeInformation);
            }
        }

        @Override
        public void possibleResultPoints(List<ResultPoint> resultPoints) {
        }
    };

    private void customizeUserToolbar(BarcodeInformation barcodeInformation){
        linearLayoutUserToolbar.setVisibility(View.VISIBLE);
        float weightSumLinearLayout=linearLayoutUserToolbar.getWeightSum();
        if (barcodeInformation.getBarcodeContentPhone()==null) {
            btnCallPhone.setVisibility(View.GONE);
            weightSumLinearLayout=weightSumLinearLayout-1;
            linearLayoutUserToolbar.setWeightSum(weightSumLinearLayout);
        }
        if (barcodeInformation.getBarcodeContentURL()==null) {
            btnVisitWeb.setVisibility(View.GONE);
            weightSumLinearLayout=weightSumLinearLayout-1;
            linearLayoutUserToolbar.setWeightSum(weightSumLinearLayout);
        }
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case 0:
                //get subCategory Selected from FilterSubcategory Dialog
                Bundle bundle = data.getExtras();
                BarcodeInformation barcode = (BarcodeInformation) bundle.getSerializable("barcodeInformationWithTitle");
                DataBaseHandler.getInstance(getBaseContext()).insertBarcodeScan(barcode);
                linearLayoutUserToolbar.setVisibility(View.GONE);
                Snackbar.make(relativeLayoutMain, "Barcode Information Saved . . . ",
                        Snackbar.LENGTH_LONG)
                        .show();
                onResume();
                break;
        }

    }

    private void requestCameraPermission() {
        Log.w(TAG, "Camera permission is not granted. Requesting permission");

        final String[] permissions = new String[]{Manifest.permission.CAMERA};

        if (!ActivityCompat.shouldShowRequestPermissionRationale(this,
                Manifest.permission.CAMERA)) {
            ActivityCompat.requestPermissions(this, permissions, RC_HANDLE_CAMERA_PERM);
            return;
        }

        final Activity thisActivity = this;

        View.OnClickListener listener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ActivityCompat.requestPermissions(thisActivity, permissions,
                        RC_HANDLE_CAMERA_PERM);
            }
        };

        Snackbar.make(relativeLayoutMain, R.string.permission_camera_rationale,
                Snackbar.LENGTH_INDEFINITE)
                .setAction(R.string.ok, listener)
                .show();
    }

    private void requestPhonePermission(){
        Log.w(TAG, "Call Phone permission is not granted. Requesting permission");
        final String[] permissions = new String[]{Manifest.permission.CAMERA};

        if (!ActivityCompat.shouldShowRequestPermissionRationale(this,
                Manifest.permission.CALL_PHONE)) {
            ActivityCompat.requestPermissions(this, permissions,RC_HANDLE_CALL_PHONE );
            return;
        }

        final Activity thisActivity = this;

        View.OnClickListener listener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ActivityCompat.requestPermissions(thisActivity, permissions,RC_HANDLE_CALL_PHONE);
            }
        };

        Snackbar.make(relativeLayoutMain, R.string.permission_call_phone,
                Snackbar.LENGTH_INDEFINITE)
                .setAction(R.string.ok, listener)
                .show();


    }
    private void requestPhoneStateread(){
        Log.w(TAG, "Call Phone Reade State permission is not granted. Requesting permission");
        final String[] permissions = new String[]{Manifest.permission.READ_PHONE_STATE};

        if (!ActivityCompat.shouldShowRequestPermissionRationale(this,
                Manifest.permission.CALL_PHONE)) {
            ActivityCompat.requestPermissions(this, permissions,RC_HANDLE_READW_PHONE_STATE );
            return;
        }

        final Activity thisActivity = this;

        View.OnClickListener listener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ActivityCompat.requestPermissions(thisActivity, permissions,RC_HANDLE_READW_PHONE_STATE);
            }
        };

        Snackbar.make(relativeLayoutMain, R.string.permission_reade_phone_state,
                Snackbar.LENGTH_INDEFINITE)
                .setAction(R.string.ok, listener)
                .show();

    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case RC_HANDLE_CAMERA_PERM: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.

                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;
            }

            case RC_HANDLE_CALL_PHONE: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.

                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;
            }
            case RC_HANDLE_READW_PHONE_STATE: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.

                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;
            }


            // other 'case' lines to check for other
            // permissions this app might request
        }
    }

    @Override
    public void onBackPressed() {
        if (flashStatus)
            barcodeView.setTorchOff();
        Intent intent=new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    protected void onResume() {
        super.onResume();

        barcodeView.resume();
    }

    @Override
    protected void onPause() {
        super.onPause();

        barcodeView.pause();
    }

    public void pause(View view) {
        barcodeView.pause();
    }

    public void resume(View view) {
        barcodeView.resume();
    }

    public void triggerScan(View view) {
        barcodeView.decodeSingle(callback);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        return barcodeView.onKeyDown(keyCode, event) || super.onKeyDown(keyCode, event);
    }

    private boolean hasFlash() {
        return getApplicationContext().getPackageManager()
                .hasSystemFeature(PackageManager.FEATURE_CAMERA_FLASH);
    }

    @Override
    public void onTorchOn() {
        flashStatus=!flashStatus;
        switchFlashlightButton.setImageResource(R.mipmap.flash_off);

    }

    @Override
    public void onTorchOff() {
        flashStatus=!flashStatus;
        switchFlashlightButton.setImageResource(R.mipmap.camera_flash);

    }

    private void stopPlayingSound() {
        mediaPlayer.stop();
        mediaPlayer.release();
    }
}