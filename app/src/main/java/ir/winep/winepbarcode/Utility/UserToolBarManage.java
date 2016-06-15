package ir.winep.winepbarcode.Utility;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.support.v4.app.ActivityCompat;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;

import ir.winep.winepbarcode.DataModel.BarcodeInformation;

/**
 * Created by ShaisteS on 5/25/2016.
 */
public class UserToolBarManage {

    private final static UserToolBarManage userToolBarManage = new UserToolBarManage();

    public static UserToolBarManage getInstance() {
        if (userToolBarManage != null) {
            return userToolBarManage;
        } else return new UserToolBarManage();
    }

    public BarcodeInformation createBarcodeInformation(String barcodeCapture, String barcodeCaptureDate){
        BarcodeInformation barcodeInformation=new BarcodeInformation();
        barcodeInformation.setBarcodeContent(barcodeCapture);
        barcodeInformation.setBarcodeScanDate(barcodeCaptureDate);
        String phone= UserToolBarManage.getInstance().detectAStringInAString("TEL",barcodeInformation.getBarcodeContent());
        if(!phone.equals(""))
            barcodeInformation.setBarcodeContentPhone(phone);
        String url=UserToolBarManage.getInstance().detectAStringInAString("URL",barcodeInformation.getBarcodeContent());
        if(!url.equals(""))
            barcodeInformation.setBarcodeContentURL(url);
        else {
            url=UserToolBarManage.getInstance().detectAStringInAString("http",barcodeInformation.getBarcodeContent());
            if (!url.equals("")){
                url="http:"+url;
                barcodeInformation.setBarcodeContentURL(url);
            }
        }
        return barcodeInformation;
    }

    public void callPhone(Context context, String barcodePhone){

        Intent callIntent = new Intent(Intent.ACTION_DIAL);
        callIntent.setData(Uri.parse("tel:"+barcodePhone));
        context.startActivity(callIntent);
    }

    public void phoneManager(Context context) {
        PhoneCallListener phoneListener = new PhoneCallListener();
        TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        telephonyManager.listen(phoneListener, PhoneStateListener.LISTEN_CALL_STATE);

    }

    public String createURlForSearchInGoogle(String url){
        if(!url.toLowerCase().startsWith("http://")) {
            url = "http://www.google.com/#q=" + url;
        }
        return url;
    }

    public void openBrowser(Context context,String url){
        if(!url.toLowerCase().contains("http://".toLowerCase())) {
            url = "http://" + url;
        }
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_VIEW);
        intent.addCategory(Intent.CATEGORY_BROWSABLE);
        intent.setData(Uri.parse(url));
        context.startActivity(intent);
    }

    public void shareContent(Context context,String content){
        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_TEXT, content);
        sendIntent.setType("text/plain");
        context.startActivity(sendIntent);
    }

    public String clearBarcodeInformationForShow(String barcodeContent){
        String barcodeContentShow="";
        Character c;
        boolean okColon=false;
        boolean okSemicolon=false;

       for (int i=0;i<barcodeContent.length();i++){
           c=barcodeContent.charAt(i);
           if(okColon) {
               barcodeContentShow = barcodeContentShow + c.toString();
               okSemicolon=false;
           }
           if (c.toString().equals(":"))
               okColon=true;
           if (okSemicolon==false && c.toString().equals(";")) {
               okColon=false;
               barcodeContentShow = barcodeContentShow + "\n";
           }

        }

        if (barcodeContentShow.equals("")&& !barcodeContent.equals(""))
            barcodeContentShow=barcodeContent;
        if(barcodeContentShow.startsWith("//"))
            barcodeContentShow="http:"+barcodeContentShow;
        barcodeContentShow=cleanBarcodeInformationFromKeyWords(barcodeContentShow);
        return barcodeContentShow;
    }

    public String cleanBarcodeInformationFromKeyWords(String barcodeInformation){
        barcodeInformation=barcodeInformation.replace("FN:","");
        barcodeInformation=barcodeInformation.replace("TEL;","");
        barcodeInformation=barcodeInformation.replace("TEL:","");
        barcodeInformation=barcodeInformation.replace("TEL;Home;","");
        barcodeInformation=barcodeInformation.replace("TEL;WORK;","");
        barcodeInformation=barcodeInformation.replace("TEL;CELL;","");
        barcodeInformation=barcodeInformation.replace("TEL;WORK;FAX","");
        barcodeInformation=barcodeInformation.replace("EMAIL:","");
        barcodeInformation=barcodeInformation.replace("NOTE:","");
        barcodeInformation=barcodeInformation.replace("BDAY:","");
        barcodeInformation=barcodeInformation.replace("ADR:","");
        barcodeInformation=barcodeInformation.replace("URL:","");
        barcodeInformation=barcodeInformation.replace("ORG:","");
        barcodeInformation=barcodeInformation.replace("TITLE:","");
        barcodeInformation=barcodeInformation.replace("END:VCARD","");
        barcodeInformation=barcodeInformation.replace("VCARD","");
        barcodeInformation=barcodeInformation.replace("TO:","");
        barcodeInformation=barcodeInformation.replace("END:VEVENT","");
        barcodeInformation=barcodeInformation.replace("VEVENT","");
        barcodeInformation=barcodeInformation.replace("DTSTART:","Data Start: ");
        barcodeInformation=barcodeInformation.replace("DTEND:","Data End: ");
        barcodeInformation=barcodeInformation.replace("BEGIN:","");
        barcodeInformation=barcodeInformation.replace("N:","");
        barcodeInformation=barcodeInformation.replace("END:VCALENDAR","");
        barcodeInformation=barcodeInformation.replace("VCALENDAR","");
        barcodeInformation=barcodeInformation.replace("DESCRIPTIO","Description:");
        barcodeInformation=barcodeInformation.replace("LOCATIO","Loacation:");
        barcodeInformation=barcodeInformation.replace("SUMMARY","Summary");
        barcodeInformation=barcodeInformation.replace(";","");
        barcodeInformation=barcodeInformation.replace("\n\n\n","\n");
        barcodeInformation=barcodeInformation.replace("\n\n","\n");
        return barcodeInformation;
    }

    public String detectAStringInAString(String goalString,String mainString){
        mainString=mainString.replace("\r\n",";");
        mainString=mainString.replace("\n",";");
        mainString=mainString.replace("\r",";");
        String goalStringContent="";
        if (mainString.contains(goalString)){
            int index=mainString.indexOf(goalString)+goalString.length();
            Character numberPhoneChar=mainString.charAt(index);
            while(!numberPhoneChar.toString().equals(":")){
                index++;
                numberPhoneChar=mainString.charAt(index);
            }
            //for cross from ":"
            index++;
            numberPhoneChar=mainString.charAt(index);
            while (!numberPhoneChar.toString().equals(";") && index<mainString.length()){
                goalStringContent=goalStringContent+numberPhoneChar;
                index++;
                numberPhoneChar=mainString.charAt(index);

            }

        }
        return goalStringContent;
    }
}
