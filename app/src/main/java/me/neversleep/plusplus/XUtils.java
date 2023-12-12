package me.neversleep.plusplus;

import android.util.Log;

import de.robv.android.xposed.XposedBridge;

public class XUtils {
    public static final String TAG = "Utils";

    public static void xLog(String str, String str2) {
        xLog(str, str2, null);
    }

    public static void xLog(String str, String str2, Throwable th) {
        XposedBridge.log("me.neversleep.plusplus::" + str + "::" + str2);
        if (th != null) {
            XposedBridge.log(th);
        }
        if (th != null) {
            Log.e(str, str2, th);
        } else {
            Log.e(str, str2);
        }
    }
}