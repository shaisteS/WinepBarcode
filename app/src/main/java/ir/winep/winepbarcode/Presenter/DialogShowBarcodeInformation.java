package ir.winep.winepbarcode.Presenter;

import android.app.Activity;
import android.app.DialogFragment;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import ir.winep.winepbarcode.DataModel.BarcodeInformation;
import ir.winep.winepbarcode.R;
import ir.winep.winepbarcode.Utility.UserToolBarManage;


/**
 * Created by ShaisteS on 5/24/2016.
 */
public class DialogShowBarcodeInformation extends DialogFragment {

    private static DialogShowBarcodeInformation dialogShowBarcodeInformation;
    private BarcodeInformation barcodeInformation;
    private TextView txtBarcodeContent;
    private LinearLayout linearLayoutUserToolbar;
    private ImageButton btnSearch;
    private ImageButton btnVisitWeb;
    private ImageButton btnShare;
    private ImageButton btnCallPhone;
    private UserToolBarManage userToolBarManage;
    private Context context;
    private Activity activity;

    public static DialogShowBarcodeInformation getInstance() {
        if (dialogShowBarcodeInformation == null) {
            dialogShowBarcodeInformation = new DialogShowBarcodeInformation();
        }
        return dialogShowBarcodeInformation;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View dialogView = inflater.inflate(R.layout.dialog_show_barcode_information, container, false);
        context=dialogView.getContext();
        activity=getActivity();

        barcodeInformation= (BarcodeInformation)getArguments().getSerializable("barcodeInformation");
        userToolBarManage=UserToolBarManage.getInstance();

        txtBarcodeContent=(TextView)dialogView.findViewById(R.id.txt_contentBarcode);
        btnSearch=(ImageButton)dialogView.findViewById(R.id.btnSearch);
        btnVisitWeb=(ImageButton)dialogView.findViewById(R.id.btnVisitWeb);
        btnShare=(ImageButton)dialogView.findViewById(R.id.btnShare);
        btnCallPhone=(ImageButton)dialogView.findViewById(R.id.btnCall);
        linearLayoutUserToolbar=(LinearLayout)dialogView.findViewById(R.id.userToolBar);
        linearLayoutUserToolbar.setVisibility(View.VISIBLE);
        float weightSumLinearLayout=linearLayoutUserToolbar.getWeightSum();

        getDialog().setTitle(barcodeInformation.getBarcodeTitle());
        txtBarcodeContent.setText(userToolBarManage.clearBarcodeInformationForShow(barcodeInformation.getBarcodeContent()));
        //txtBarcodeContent.setText(barcodeInformation.getBarcodeContent());

        if (barcodeInformation.getBarcodeContentPhone()==null) {
            btnCallPhone.setVisibility(View.GONE);
            weightSumLinearLayout=weightSumLinearLayout-1;
            linearLayoutUserToolbar.setWeightSum(weightSumLinearLayout);
        }
        btnCallPhone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
                userToolBarManage.callPhone(context,barcodeInformation.getBarcodeContentPhone());
                userToolBarManage.phoneManager(context);
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
        if (barcodeInformation.getBarcodeContentURL()==null) {
            btnVisitWeb.setVisibility(View.GONE);
            weightSumLinearLayout=weightSumLinearLayout-1;
            linearLayoutUserToolbar.setWeightSum(weightSumLinearLayout);
        }
        btnVisitWeb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String url=barcodeInformation.getBarcodeContentURL();
                userToolBarManage.openBrowser(context,url);
            }
        });

        btnShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                userToolBarManage.shareContent(context,barcodeInformation.getBarcodeContent());
            }
        });
        return dialogView;
    }
}

