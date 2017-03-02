package com.nexus.nsnik.weather;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;

/**
 * Created by nsnik on 03-Nov-16.
 */

public class WeatherSyncService extends Service{

    private static final Object lwas = new Object();
    private static WeatherSyncAdapter wsa  = null;

    @Override
    public void onCreate() {
        synchronized (lwas){
            if(wsa==null){
                wsa = new WeatherSyncAdapter(getApplicationContext(),true);
            }
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return wsa.getSyncAdapterBinder();
    }
}
