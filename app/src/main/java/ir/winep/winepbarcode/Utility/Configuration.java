package ir.winep.winepbarcode.Utility;

import android.content.Context;

/**
 * Created by ShaisteS on 5/25/2016.
 */
public class Configuration {

    private final static Configuration config = new Configuration();
    public static Configuration getInstance() {
        if (config != null) {
            return config;
        }
        else return new Configuration();
    }

    public Context applicationContext;




}
