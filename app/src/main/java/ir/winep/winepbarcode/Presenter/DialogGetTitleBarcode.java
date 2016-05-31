package ir.winep.winepbarcode.Presenter;

import android.app.DialogFragment;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import ir.winep.winepbarcode.Camera.ContinuousCaptureActivity;
import ir.winep.winepbarcode.DataModel.BarcodeInformation;
import ir.winep.winepbarcode.R;


/**
 * Created by ShaisteS on 5/24/2016.
 */
public class DialogGetTitleBarcode extends DialogFragment {

    private static DialogGetTitleBarcode dialogForGetTitleBarcode;
    private EditText edtBarcodeTitle;
    private Button btnOK;
    private BarcodeInformation barcodeInformation;

    public static DialogGetTitleBarcode getInstance() {
        if (dialogForGetTitleBarcode == null) {
            dialogForGetTitleBarcode = new DialogGetTitleBarcode();
        }
        return dialogForGetTitleBarcode;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View dialogView = inflater.inflate(R.layout.dialog_get_barcode_title, container, false);
        barcodeInformation= (BarcodeInformation)getArguments().getSerializable("barcodeInformation");
        edtBarcodeTitle=(EditText)dialogView.findViewById(R.id.edt_barcodeTitle);
        btnOK=(Button)dialogView.findViewById(R.id.btn_Ok);
        getDialog().setTitle(getResources().getString(R.string.dialog_Get_Title_Barcode_Title));

        btnOK.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                barcodeInformation.setBarcodeTitle(edtBarcodeTitle.getText().toString());
                Intent args = new Intent();
                args.putExtra("barcodeInformationWithTitle",barcodeInformation );
                setTargetFragment(getFragmentManager().findFragmentByTag("getTitleBarcode"), 0);
                //BarcodeCaptureActivity barcodeCaptureActivity=(BarcodeCaptureActivity)getActivity();
                //barcodeCaptureActivity.onActivityResult(getTargetRequestCode(), 0, args);
                ContinuousCaptureActivity b= (ContinuousCaptureActivity) getActivity();
                b.onActivityResult(getTargetRequestCode(),0,args);
                dismiss();
            }
        });
        return dialogView;
    }

}