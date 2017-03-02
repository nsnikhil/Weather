package com.nexus.nsnik.weather;

import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class GetList {

    private static final String LOG_TAG = GetList.class.getSimpleName();
    private static final String mTest = "http://api.openweathermap.org/data/2.5/forecast?lat=22.595738&lon=88.3437847&appid=447a251308f12cae7fbc74bd4083c191&cnt=7&units=metric";
    private static final String mTest2 = "http://api.openweathermap.org/data/2.5/weather?lat=22.5957369&lon=88.3437863&appid=447a251308f12cae7fbc74bd4083c191&units=metric";
    private static final String uScheme = "http";
    private static final String uAuthority = "api.openweathermap.org";
    private static final String uPath1 = "data";
    private static final String uPath2 = "2.5";
    private static final String uForecastPath = "forecast";
    private static final String uForecasePath2 = "daily";
    private static final String uWeatherPath = "weather";

    private static final String uLatitudeQuery = "lat";
    private static final String uLongititudeQuery = "lon";
    private static final String uApiId = "appid";
    private static final String uApiIdValue = "447a251308f12cae7fbc74bd4083c191";
    private static final String uMetrics = "units";
    private static final String uCount = "cnt";
    private static final String uCountValue = "7";

    private static String buildForecastUri(Context y,String lat,String lgo){
        SharedPreferences cspa = PreferenceManager.getDefaultSharedPreferences(y);
        String tempunit = cspa.getString(y.getResources().getString(R.string.prefrenceTempUnit),"Celsius");
        String uMetricsValue;
        if(tempunit.equalsIgnoreCase("Farenheit")){
            uMetricsValue = "imperial";
        }else if(tempunit.equalsIgnoreCase("Kelvin")){
            uMetricsValue = "";
        }else {
            uMetricsValue = "metric";
        }

        Uri.Builder ub = new Uri.Builder();
        ub.scheme(uScheme).authority(uAuthority)
                .appendPath(uPath1)
                .appendPath(uPath2)
                .appendPath(uForecastPath)
                .appendPath(uForecasePath2)
                .appendQueryParameter(uLatitudeQuery,lat)
                .appendQueryParameter(uLongititudeQuery,lgo)
                .appendQueryParameter(uApiId,uApiIdValue)
                .appendQueryParameter(uCount,uCountValue)
                .appendQueryParameter(uMetrics,uMetricsValue);
        String fn = ub.build().toString();
        return fn;
    }

    private static String buildCurrenttUri(Context y,String lat,String lgo){
        SharedPreferences cspa = PreferenceManager.getDefaultSharedPreferences(y);
        String tempunit = cspa.getString(y.getResources().getString(R.string.prefrenceTempUnit),"Celsius");
        String uMetricsValue;
        if(tempunit.equalsIgnoreCase("Farenheit")){
            uMetricsValue = "imperial";
        }else if(tempunit.equalsIgnoreCase("Kelvin")){
            uMetricsValue = "";
        }else {
            uMetricsValue = "metric";
        }

        Uri.Builder ub = new Uri.Builder();
        ub.scheme(uScheme).authority(uAuthority)
                .appendPath(uPath1)
                .appendPath(uPath2)
                .appendPath(uWeatherPath)
                .appendQueryParameter(uLatitudeQuery,lat)
                .appendQueryParameter(uLongititudeQuery,lgo)
                .appendQueryParameter(uApiId,uApiIdValue)
                .appendQueryParameter(uCount,uCountValue)
                .appendQueryParameter(uMetrics,uMetricsValue);
        String fn = ub.build().toString();
        return fn;
    }


    private static URL makeUrl(String y)
    {
        URL u = null;
        try {
            u = new URL(y);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        return u;
    }

    public static String makeHttp(URL ur)
    {
        String json = "";
        HttpURLConnection conn = null;
        InputStream is = null;
        try {
            conn = (HttpURLConnection) ur.openConnection();
            conn.setRequestMethod("GET");
            conn.connect();
            int respo = conn.getResponseCode();
            if(respo==200)
            {
                is = conn.getInputStream();
                json = readFromStream(is);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            if(conn!=null)
            {
                conn.disconnect();
            }
        }
        return json;
    }

    private static String readFromStream(InputStream is) throws IOException {
        StringBuilder sb = new StringBuilder();
        InputStreamReader ir = new InputStreamReader(is, Charset.forName("UTF-8"));
        BufferedReader br = new BufferedReader(ir);
        String ln = br.readLine();
        while (ln!=null)
        {
            sb.append(ln);
            ln = br.readLine();
        }
        return sb.toString();
    }

    public static boolean makelist(Context c) throws JSONException {
        Boolean rt = false;
        ContentValues cvi = new ContentValues();
        int i;
        SharedPreferences scf = PreferenceManager.getDefaultSharedPreferences(c);
        String lat = scf.getString(c.getResources().getString(R.string.bundleLatitude),"22.5957387");
        String lgo = scf.getString(c.getResources().getString(R.string.bundleLongitude),"88.3437852");
        String forecastUri = buildForecastUri(c,lat,lgo);
        String currentUri = buildCurrenttUri(c,lat,lgo);
        URL forecastUrl = makeUrl(forecastUri);
        URL currentUrl = makeUrl(currentUri);

        String cJson = makeHttp(currentUrl);
        JSONObject fullCurrent = new JSONObject(cJson);
        String cityName = fullCurrent.getString("name");
        scf = PreferenceManager.getDefaultSharedPreferences(c);
        SharedPreferences.Editor sef = scf.edit();
        sef.putString(c.getResources().getString(R.string.prefrenceCityName),cityName);
        sef.commit();
        long cDate = fullCurrent.getLong("dt");
        cvi.put(weathertablecoloum.mDate,cDate);
        JSONArray cWth = fullCurrent.getJSONArray("weather");
        JSONObject cWthIn  = cWth.getJSONObject(0);
        String id = cWthIn.getString("id");
        cvi.put(weathertablecoloum.mWeatherIconId,id);
        String dec = cWthIn.getString("main");
        String ddec = cWthIn.getString("description");
        cvi.put(weathertablecoloum.mCondition,dec);
        cvi.put(weathertablecoloum.mDetailCondition,ddec);
        JSONObject mWth = fullCurrent.getJSONObject("main");
        String cTemp = mWth.getString("temp");
        cvi.put(weathertablecoloum.mCurrentTemp,cTemp);
        String cminTemp= mWth.getString("temp_min");
        cvi.put(weathertablecoloum.mMinTemp,cminTemp);
        String cmaxTemp= mWth.getString("temp_max");
        cvi.put(weathertablecoloum.mMaxTemp,cmaxTemp);
        String cpressure= mWth.getString("pressure");
        cvi.put(weathertablecoloum.mPressure,cpressure);
        String chumidity= mWth.getString("humidity");
        cvi.put(weathertablecoloum.mHumidity,chumidity);
        JSONObject mSpd = fullCurrent.getJSONObject("wind");
        String wdspd = mSpd.getString("speed");
        cvi.put(weathertablecoloum.mSpeed,wdspd);
        Uri cua =c.getContentResolver().insert(weathertable.mContentUri,cvi);

        String tJson = makeHttp(forecastUrl);
        JSONObject fullJson = new JSONObject(tJson);
        JSONArray listArray = fullJson.getJSONArray("list");
        for(i=0;i<listArray.length()-1;i++)
        {
            JSONObject listContent = listArray.getJSONObject(i+1);
            long date = listContent.getLong("dt");
            cvi.put(weathertablecoloum.mDate,date);
            JSONObject temps = listContent.getJSONObject("temp");
            String minTemp = temps.getString("min");
            String maxTemp = temps.getString("max");
            String currentTemp = temps.getString("day");
            cvi.put(weathertablecoloum.mMinTemp,minTemp);
            cvi.put(weathertablecoloum.mMaxTemp,maxTemp);
            cvi.put(weathertablecoloum.mCurrentTemp,currentTemp);
            String pressure = listContent.getString("pressure");
            cvi.put(weathertablecoloum.mPressure,pressure);
            String humidity = listContent.getString("humidity");
            cvi.put(weathertablecoloum.mHumidity,humidity);
            JSONArray weatherConditions = listContent.getJSONArray("weather");
            JSONObject weatherConditionContent = weatherConditions.getJSONObject(0);
            String condtiton = weatherConditionContent.getString("main");
            cvi.put(weathertablecoloum.mCondition,condtiton);
            String dCondition = weatherConditionContent.getString("description");
            cvi.put(weathertablecoloum.mDetailCondition,dCondition);
            String iconId = weatherConditionContent.getString("id");
            cvi.put(weathertablecoloum.mWeatherIconId,iconId);
            String windSpeed = listContent.getString("speed");
            cvi.put(weathertablecoloum.mSpeed,windSpeed);
            Uri ca =c.getContentResolver().insert(weathertable.mContentUri,cvi);
            if(cua==null&&ca==null){
                rt = false;
                break;
            }else {
                rt = true;
            }
        }
        return rt;
    }
}
