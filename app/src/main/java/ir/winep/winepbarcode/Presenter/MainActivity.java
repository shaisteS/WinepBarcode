package ir.winep.winepbarcode.Presenter;

import android.app.DialogFragment;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;


import com.appodeal.ads.Appodeal;

import java.util.ArrayList;

import ir.winep.winepbarcode.Camera.ContinuousCaptureActivity;
import ir.winep.winepbarcode.DataModel.BarcodeInformation;
import ir.winep.winepbarcode.DataModel.DataBaseHandler;
import ir.winep.winepbarcode.R;
import ir.winep.winepbarcode.Utility.DividerItemDecorationRecyclerView;
import ir.winep.winepbarcode.Utility.RecyclerTouchListenerInterface;
import ir.winep.winepbarcode.Utility.RecyclerViewAdapter;
import ir.winep.winepbarcode.Utility.SwipeAbleItemClickListener;
import ir.winep.winepbarcode.Utility.SwipeToDismissTouchListener;


public class MainActivity extends AppCompatActivity {

    private RecyclerView barcodeScanRecyclerView;
    private BarcodeScanRecyclerAdapter adapter;
    private Context context;
    private CoordinatorLayout coordinatorLayout;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        context=this;

        Appodeal.show(this, Appodeal.INTERSTITIAL);
        Appodeal.confirm(Appodeal.NON_SKIPPABLE_VIDEO);



        coordinatorLayout=(CoordinatorLayout)findViewById(R.id.mainCoordinator_layout);
        barcodeScanRecyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        barcodeScanRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        barcodeScanRecyclerView.addItemDecoration(new DividerItemDecorationRecyclerView(this, LinearLayoutManager.VERTICAL));



        ArrayList<BarcodeInformation> barcodeScanList = new ArrayList<BarcodeInformation>();
        barcodeScanList = DataBaseHandler.getInstance(getBaseContext()).selectAllBarcodeScanContent();
        adapter = new BarcodeScanRecyclerAdapter(this, barcodeScanList);
        barcodeScanRecyclerView.setAdapter(adapter);

        //swipe on item recycler view
        final SwipeToDismissTouchListener<RecyclerViewAdapter> touchListener =
                new SwipeToDismissTouchListener<>(
                        new RecyclerViewAdapter(barcodeScanRecyclerView),
                        new SwipeToDismissTouchListener.DismissCallbacks<RecyclerViewAdapter>() {
                            @Override
                            public boolean canDismiss(int position) {
                                return true;
                            }

                            @Override
                            public void onDismiss(RecyclerViewAdapter view, int position) {
                                adapter.remove(position);
                            }
                        });
        barcodeScanRecyclerView.setOnTouchListener(touchListener);
        barcodeScanRecyclerView.addOnScrollListener((RecyclerView.OnScrollListener) touchListener.makeScrollListener());
        barcodeScanRecyclerView.addOnItemTouchListener(new SwipeAbleItemClickListener(this,
                new RecyclerTouchListenerInterface() {
                    @Override
                    public void onClick(View view, int position) {
                        if (view.getId() == R.id.txt_delete) {
                            DataBaseHandler.getInstance(context).deleteABarcode(adapter.getItemContent(position).getBarcodeId());

                            touchListener.processPendingDismisses();
                        } else if (view.getId() == R.id.txt_undo) {
                            touchListener.undoPendingDismiss();
                        }
                        else {
                            Bundle bundle=new Bundle();
                            bundle.putSerializable("barcodeInformation",adapter.getItemContent(position));
                            DialogShowBarcodeInformation dialogShowBarcodeInformation=DialogShowBarcodeInformation.getInstance();
                            dialogShowBarcodeInformation.setArguments(bundle);
                            dialogShowBarcodeInformation.show(getFragmentManager(),"showBarcodeInformation");
                        }

                    }

                    @Override
                    public void onLongClick(View view, int position) {

                    }

                }));

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                /*Intent intent = new Intent(getBaseContext(), BarcodeCaptureActivity.class);
                startActivity(intent);
                finish();*/
                Intent intent = new Intent(getBaseContext(),ContinuousCaptureActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }

    @Override
    public void onBackPressed() {
        //Clear Cache
        DialogExitFromApplication.getInstance().show(getFragmentManager(),"exit");
    }
}
