package com.example.visualkhh.myapplication.view.util;

import android.util.Log;
import com.example.visualkhh.myapplication.BuildConfig;

/**
 * Created by lht-Mac on 2017/9/29.
 */

public class LogUtil {

    private static final String TAG = "PaintView";

    public static void d(String msg) {
        if (BuildConfig.DEBUG) {
            Log.d(TAG, msg);
        }
    }
}
