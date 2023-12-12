package me.neversleep.plusplus;

import android.annotation.TargetApi;
import android.content.SharedPreferences;
import android.os.Build;
import android.service.quicksettings.TileService;
import android.util.Log;
import android.widget.Toast;

@TargetApi(Build.VERSION_CODES.N)
public class QuickStartService extends TileService {
    public static final String TAG = "QurkStartService";
    private SharedPreferences xConf;

    @Override // android.app.Service
    public void onCreate() {
        super.onCreate();
        try {
            this.xConf = getSharedPreferences("x_conf", 1);
            Log.e(TAG, "onCreate: xConf" + this.xConf);
        } catch (SecurityException e) {
            Toast.makeText(this, "error: " + e.getMessage(), 1).show();
        }
    }

    @Override // android.service.quicksettings.TileService
    public void onClick() {
        super.onClick();
        if (this.xConf == null) {
            return;
        }
        int state = getQsTile().getState();
        if (state == 1) {
            getQsTile().setState(2);
            this.xConf.edit().putBoolean("power", true).apply();
        } else if (state == 2) {
            getQsTile().setState(1);
            this.xConf.edit().putBoolean("power", false).apply();
        }
        getQsTile().updateTile();
        Log.e(TAG, "onClick: " + getQsTile().getState());
    }

    @Override // android.service.quicksettings.TileService
    public void onTileAdded() {
        super.onTileAdded();
        if (this.xConf == null) {
            return;
        }
        getQsTile().setState(this.xConf.getBoolean("power", false) ? 2 : 1);
        getQsTile().updateTile();
        Log.e(TAG, "onTileAdded: " + this.xConf.getBoolean("power", false) + ":" + getQsTile().getState());
    }

    @Override // android.service.quicksettings.TileService
    public void onStartListening() {
        super.onStartListening();
        if (this.xConf == null) {
            return;
        }
        getQsTile().setState(this.xConf.getBoolean("power", false) ? 2 : 1);
        getQsTile().updateTile();
        Log.e(TAG, "onStartListening: update" + this.xConf.getBoolean("power", false) + ":" + getQsTile().getState());
    }
}
