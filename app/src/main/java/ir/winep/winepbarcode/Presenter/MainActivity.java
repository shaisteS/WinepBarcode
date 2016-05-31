package ir.winep.winepbarcode.Presenter;

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
    private int exitSafeCounter = 0;
    private CoordinatorLayout coordinatorLayout;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        context=this;

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
        exitSafeCounter++;
        //Clear Cache
        if (exitSafeCounter == 1) {
            Snackbar.make(coordinatorLayout,getString(R.string.sure_to_exit), Snackbar.LENGTH_LONG).show();
        } else if (exitSafeCounter > 1) {
            Intent intent = new Intent(Intent.ACTION_MAIN);
            intent.addCategory(Intent.CATEGORY_HOME);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            finish();
            System.exit(0);
        }
    }
}
