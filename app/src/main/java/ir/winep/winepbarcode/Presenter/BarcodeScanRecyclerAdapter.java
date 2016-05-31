package ir.winep.winepbarcode.Presenter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;



import java.util.ArrayList;

import ir.winep.winepbarcode.DataModel.BarcodeInformation;
import ir.winep.winepbarcode.R;

/**
 * Created by ShaisteS on 5/22/2016.
 */
public class BarcodeScanRecyclerAdapter extends RecyclerView.Adapter<BarcodeScanRecyclerAdapter.Holder> {

    private ArrayList<BarcodeInformation> allBarcodeScans;
    private static LayoutInflater inflater;
    private Context myContext;



    public BarcodeScanRecyclerAdapter(Context context, ArrayList<BarcodeInformation> barcodeScanList){
        myContext=context;
        inflater = ( LayoutInflater ) myContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        allBarcodeScans = barcodeScanList;
    }

    @Override
    public Holder onCreateViewHolder(ViewGroup parent, int viewType) {
        View rowView;
        rowView = inflater.inflate(R.layout.barcode_scan_recycler_view_item, null);
        return new Holder(rowView);
    }

    @Override
    public void onBindViewHolder(BarcodeScanRecyclerAdapter.Holder holder, int position) {
        holder.textViewTitle.setText(allBarcodeScans.get(position).getBarcodeTitle());
        //holder.textViewDate.setText(allBarcodeScans.get(position).getBarcodeScanDate());
    }

    public static class Holder extends RecyclerView.ViewHolder {
        TextView textViewTitle;
        //TextView textViewDate;
        public Holder(View itemView) {
            super(itemView);
            textViewTitle=(TextView)itemView.findViewById(R.id.txt_barcode_title_recycler);
            //textViewDate=(TextView)itemView.findViewById(R.id.txt_barcode_date_scan_recycler);
        }
    }


    @Override
    public int getItemCount() {
        return allBarcodeScans.size();
    }

    public void remove(int position) {
        allBarcodeScans.remove(position);
        notifyItemRemoved(position);
    }

    public BarcodeInformation getItemContent(int position){
        return allBarcodeScans.get(position);
    }

}