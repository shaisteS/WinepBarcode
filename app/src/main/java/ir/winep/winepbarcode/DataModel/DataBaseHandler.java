package ir.winep.winepbarcode.DataModel;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;

/**
 * Created by ShaisteS on 5/22/2016.
 */
public class DataBaseHandler extends SQLiteOpenHelper {

    private static DataBaseHandler sInstance;
    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "Barcode";
    private final String TABLE_Barcode_Scan = "tblBarcodeScan";
    private String BarcodeScanTable_Column_Primary_Id = "Id";
    private String BarcodeScanTable_Column_Barcode_Title = "title";
    private String BarcodeScanTable_Column_Barcode_Content = "barcodeContent";
    private String BarcodeScanTable_Column_Barcode_Scan_Date = "barcodeScanDate";
    private String BarcodeScanTable_Column_Barcode_Type = "barcodeType";
    private String BarcodeScanTable_Column_Barcode_Content_URL = "barcodeContentURL";
    private String BarcodeScanTable_Column_Barcode_Content_Phone = "barcodeContentPhone";

    public static synchronized DataBaseHandler getInstance(Context context) {

        if (sInstance == null) {
            sInstance = new DataBaseHandler(context.getApplicationContext());
        }
        return sInstance;
    }


    private DataBaseHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        db.execSQL("create table " + TABLE_Barcode_Scan +
                "(" +
                BarcodeScanTable_Column_Primary_Id + " Integer primary key AUTOINCREMENT," +
                BarcodeScanTable_Column_Barcode_Title + " text," +
                BarcodeScanTable_Column_Barcode_Content + " text," +
                BarcodeScanTable_Column_Barcode_Scan_Date + " text," +
                BarcodeScanTable_Column_Barcode_Type + " text," +
                BarcodeScanTable_Column_Barcode_Content_URL + " text," +
                BarcodeScanTable_Column_Barcode_Content_Phone + " text)");
        Log.v("create", "Create Barcode Scan Table");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public Boolean emptyBarcodeScanTable() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor rs = db.rawQuery("select * from " + TABLE_Barcode_Scan, null);
        if (rs.moveToFirst()) {
            //Not empty
            rs.close();
            return false;
        } else {
            //Is Empty
            rs.close();
            return true;
        }
    }

    public ContentValues addFieldBarcodeInformation(BarcodeInformation barcode) {
        ContentValues values = new ContentValues();
        values.put(BarcodeScanTable_Column_Barcode_Title, barcode.getBarcodeTitle());
        values.put(BarcodeScanTable_Column_Barcode_Content, barcode.getBarcodeContent());
        values.put(BarcodeScanTable_Column_Barcode_Scan_Date, barcode.getBarcodeScanDate());
        values.put(BarcodeScanTable_Column_Barcode_Type, barcode.getBarcodeType());
        values.put(BarcodeScanTable_Column_Barcode_Content_URL, barcode.getBarcodeContentURL());
        values.put(BarcodeScanTable_Column_Barcode_Content_Phone, barcode.getBarcodeContentPhone());
        return values;
    }

    public void insertBarcodeScan(BarcodeInformation barcode) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.insert(TABLE_Barcode_Scan, null, addFieldBarcodeInformation(barcode));
        Log.v("insert", "insert Barcode Scan to BarcodeScan Table");
    }

    public ArrayList<BarcodeInformation> selectAllBarcodeScanContent() {
        String query = "select *" +
                " from " + TABLE_Barcode_Scan;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor rs = db.rawQuery(query, null);
        ArrayList<BarcodeInformation> allBarcodeScan = new ArrayList<>();
        if (rs != null) {
            if (rs.moveToFirst()) {
                do {
                    allBarcodeScan.add(createABarcodeInformation(rs));
                }
                while (rs.moveToNext());
            }
            rs.close();
        }
        Log.v("select", "Select All Barcode Scan ");
        return allBarcodeScan;
    }

    public BarcodeInformation createABarcodeInformation(Cursor rs) {
        BarcodeInformation barcode=new BarcodeInformation();
        barcode.setBarcodeId(rs.getInt(rs.getColumnIndex(BarcodeScanTable_Column_Primary_Id)));
        barcode.setBarcodeTitle(rs.getString(rs.getColumnIndex(BarcodeScanTable_Column_Barcode_Title)));
        barcode.setBarcodeContent(rs.getString(rs.getColumnIndex(BarcodeScanTable_Column_Barcode_Content)));
        barcode.setBarcodeScanDate(rs.getString(rs.getColumnIndex(BarcodeScanTable_Column_Barcode_Scan_Date)));
        barcode.setBarcodeType(rs.getString(rs.getColumnIndex(BarcodeScanTable_Column_Barcode_Type)));
        barcode.setBarcodeContentPhone(rs.getString(rs.getColumnIndex(BarcodeScanTable_Column_Barcode_Content_Phone)));
        barcode.setBarcodeContentURL(rs.getString(rs.getColumnIndex(BarcodeScanTable_Column_Barcode_Content_URL)));
        return barcode;
    }

    public void deleteABarcode(int barcodeId){
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_Barcode_Scan,BarcodeScanTable_Column_Primary_Id+"="+barcodeId,null);
        Log.v("delete", "Delete A Category from Category Table");
    }
}