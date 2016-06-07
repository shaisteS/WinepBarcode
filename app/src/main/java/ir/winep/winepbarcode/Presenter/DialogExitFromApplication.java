package ir.winep.winepbarcode.Presenter;

import android.app.DialogFragment;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import ir.winep.winepbarcode.R;

/**
 * Created by ShaisteS on 6/7/2016.
 */
public class DialogExitFromApplication extends DialogFragment {

    private Button btnExit;
    private Button btnCancel;
    private Button btnRateUs;
    private Intent rateIntent;
    private static DialogExitFromApplication dialogExitFromApplication;
    public static DialogExitFromApplication getInstance() {
        if (dialogExitFromApplication == null) {
            dialogExitFromApplication = new DialogExitFromApplication();
        }
        return dialogExitFromApplication;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View dialogView = inflater.inflate(R.layout.dialog_exit_from_app, container, false);
        getDialog().setTitle(getResources().getString(R.string.dialog_exit_from_app_title));
        btnCancel=(Button)dialogView.findViewById(R.id.btn_cancel);
        btnRateUs=(Button)dialogView.findViewById(R.id.btn_rateUs);
        btnExit=(Button)dialogView.findViewById(R.id.btn_exit);
        rateIntent = new Intent(Intent.ACTION_VIEW);

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        btnRateUs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
                rateIntent.setData(Uri.parse("market://details?id="+getActivity().getPackageName()));
                getActivity().startActivity(rateIntent);
            }
        });

        btnExit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
                Intent intent = new Intent(Intent.ACTION_MAIN);
                intent.addCategory(Intent.CATEGORY_HOME);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                getActivity().startActivity(intent);
                getActivity().finish();
                System.exit(0);
            }
        });

        return dialogView;
    }

}
