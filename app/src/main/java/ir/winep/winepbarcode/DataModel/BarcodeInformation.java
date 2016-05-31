package ir.winep.winepbarcode.DataModel;

import java.io.Serializable;

/**
 * Created by ShaisteS on 5/24/2016.
 */
public class BarcodeInformation implements Serializable {

    private int barcodeId;
    private String barcodeTitle;
    private String barcodeContent;
    private String barcodeScanDate;
    private String barcodeType;
    private String barcodeContentURL;
    private String barcodeContentPhone;

    public int getBarcodeId() {
        return barcodeId;
    }

    public void setBarcodeId(int barcodeId) {
        this.barcodeId = barcodeId;
    }

    public String getBarcodeTitle() {
        return barcodeTitle;
    }

    public void setBarcodeTitle(String barcodeTitle) {
        this.barcodeTitle = barcodeTitle;
    }

    public String getBarcodeContent() {
        return barcodeContent;
    }

    public void setBarcodeContent(String barcodeContent) {
        this.barcodeContent = barcodeContent;
    }

    public String getBarcodeScanDate() {
        return barcodeScanDate;
    }

    public void setBarcodeScanDate(String barcodeScanDate) {
        this.barcodeScanDate = barcodeScanDate;
    }

    public String getBarcodeType() {
        return barcodeType;
    }

    public void setBarcodeType(String barcodeType) {
        this.barcodeType = barcodeType;
    }

    public String getBarcodeContentURL() {
        return barcodeContentURL;
    }

    public void setBarcodeContentURL(String barcodeContentURL) {
        this.barcodeContentURL = barcodeContentURL;
    }

    public String getBarcodeContentPhone() {
        return barcodeContentPhone;
    }

    public void setBarcodeContentPhone(String barcodeContentPhone) {
        this.barcodeContentPhone = barcodeContentPhone;
    }
}
