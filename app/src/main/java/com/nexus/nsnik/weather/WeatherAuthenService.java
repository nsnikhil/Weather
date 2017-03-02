package com.nexus.nsnik.weather;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import java.net.Authenticator;

/**
 * Created by nsnik on 03-Nov-16.
 */

public class WeatherAuthenService extends Service {

    private WeatherAunthen mAuthenticator;
    @Override
    public void onCreate() {
        mAuthenticator = new WeatherAunthen(this);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return mAuthenticator.getIBinder();
    }
}
