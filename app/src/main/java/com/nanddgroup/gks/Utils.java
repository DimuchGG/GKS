package com.nanddgroup.gks;

import android.content.Context;

import java.io.IOException;
import java.io.InputStream;

/**
 * Created by Dimuch on 20.10.2016.
 */

public class Utils {

    public static String loadDataFromFile(Context context, String asset_name) {
        String dataInFile = null;
        try {
            InputStream is = context.getAssets().open(asset_name);
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            dataInFile = new String(buffer, "UTF-8");
        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
        return dataInFile;
    }
}
