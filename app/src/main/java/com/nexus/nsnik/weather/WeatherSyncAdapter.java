package com.nexus.nsnik.weather;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.AbstractThreadedSyncAdapter;
import android.content.ContentProviderClient;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SyncRequest;
import android.content.SyncResult;
import android.database.Cursor;
import android.location.Location;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;

import org.json.JSONException;

public class WeatherSyncAdapter extends AbstractThreadedSyncAdapter{

    private static final int WEATHER_NOTIFICATION_ID = 5004;
    private static final int SYNC_INTERVAL = 30;
    private static final int SYNC_FLEXTIME = SYNC_INTERVAL/3;

    private static final String[] NOTIFY_WEATHER_PROJECTION = new String[] {weathertablecoloum.mWeatherIconId,weathertablecoloum.mMaxTemp,weathertablecoloum.mMinTemp,weathertablecoloum.mCondition};

    public static final String LOG_TAG = WeatherSyncAdapter.class.getSimpleName();

    public static String oLongtitude;
    public static String oLatitude;

    public WeatherSyncAdapter(Context context, boolean autoInitialize) {
        super(context, autoInitialize);
    }

    @Override
    public void onPerformSync(Account account, Bundle extras, String authority, ContentProviderClient provider, SyncResult syncResult) {
        getContext().getContentResolver().delete(weathertable.mContentUri,null,null);
        try {
            boolean t = GetList.makelist(getContext());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        notifyWeather();
    }

    public static Account getSyncAccount(Context context) {
        AccountManager accountManager =  (AccountManager) context.getSystemService(Context.ACCOUNT_SERVICE);
        Account newAccount = new Account(context.getString(R.string.app_name), context.getString(R.string.sync_account_type));
        if ( null == accountManager.getPassword(newAccount) ) {
            if (!accountManager.addAccountExplicitly(newAccount, "", null)) {
                return null;
            }
            onAccountCreated(newAccount,context);
        }
        return newAccount;
    }


    public static void syncImmediately(Context context) {
        Bundle bundle = new Bundle();
        bundle.putBoolean(ContentResolver.SYNC_EXTRAS_EXPEDITED, true);
        bundle.putBoolean(ContentResolver.SYNC_EXTRAS_MANUAL, true);
        ContentResolver.requestSync(getSyncAccount(context), context.getString(R.string.content_authority), bundle);
    }

    public static void configurePeriodicSync(Context context, int syncInterval, int flexTime) {
        Account account = getSyncAccount(context);
        String authority = context.getString(R.string.content_authority);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            SyncRequest request = new SyncRequest.Builder().syncPeriodic(syncInterval, flexTime).setSyncAdapter(account, authority).setExtras(new Bundle()).build();
            ContentResolver.requestSync(request);
        } else {
            ContentResolver.addPeriodicSync(account,authority, new Bundle(), syncInterval);
        }
    }

    private static void onAccountCreated(Account newAccount, Context context) {
        configurePeriodicSync(context, SYNC_INTERVAL, SYNC_FLEXTIME);
        ContentResolver.setSyncAutomatically(newAccount, context.getString(R.string.content_authority), true);
        syncImmediately(context);
    }

    public static void initializeSyncAdapter(Context context) {
      getSyncAccount(context);
    }

    private void notifyWeather() {
        Context context = getContext();
            Uri weatherUri = Uri.withAppendedPath(weathertable.mContentUri,"");
            Cursor cursor = context.getContentResolver().query(weatherUri, null, null, null, null);
            if (cursor.moveToFirst()) {
                int iconId = CustomA.getImgId(cursor);
                SharedPreferences cspa = PreferenceManager.getDefaultSharedPreferences(context);
                String nm = cspa.getString(context.getResources().getString(R.string.prefrenceCityName),"Default");
                String title = (context.getString(R.string.formatTemp,cursor.getDouble(cursor.getColumnIndex(weathertablecoloum.mCurrentTemp))))+ " in " + nm;
                String contentText = cursor.getString(cursor.getColumnIndex(weathertablecoloum.mCondition));
                NotificationCompat.Builder mBuilder =  new NotificationCompat.Builder(getContext()).setSmallIcon(iconId).setContentTitle(title).setContentText(contentText);
                Intent resultIntent = new Intent(context, MainActivity.class);
                TaskStackBuilder stackBuilder = TaskStackBuilder.create(getContext());
                stackBuilder.addNextIntent(resultIntent);
                PendingIntent resultPendingIntent =  stackBuilder.getPendingIntent(0,PendingIntent.FLAG_UPDATE_CURRENT);
                mBuilder.setContentIntent(resultPendingIntent);
                NotificationManager mNotificationManager = (NotificationManager) getContext().getSystemService(Context.NOTIFICATION_SERVICE);
                mNotificationManager.notify(WEATHER_NOTIFICATION_ID, mBuilder.build());
        }
    }
}
