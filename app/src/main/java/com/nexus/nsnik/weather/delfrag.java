package com.nexus.nsnik.weather;


import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * A simple {@link Fragment} subclass.
 */
public class delfrag extends android.support.v4.app.Fragment {

    TextView currentTemp,detailDescription,detailPressure,detailHumidity,detailWindSpeed;
    ImageView bannerImage;
    Uri u = null;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = LayoutInflater.from(getContext()).inflate(R.layout.detail_view, container, false);
        initilize(v);
        Bundle arguments = getArguments();
        if (arguments != null){
            u = arguments.getParcelable("urdi");
        }else {
            if(getActivity().getIntent()!=null) {
                u = getActivity().getIntent().getData();
            }
        }
        if (u == null) {
            u = Uri.withAppendedPath(weathertable.mContentUri, String.valueOf(0));
        }
        Cursor q = getActivity().getContentResolver().query(u, null, null, null, null);
        if (q.moveToFirst()) {
            setdat(q);
            bannerImage.setImageResource(getImageBanner(q));
        }
        return v;
    }

    private int getImageBanner(Cursor c){
        SimpleDateFormat sdf = new SimpleDateFormat("HH");
        String dateString = sdf.format(new Date());
        int s = c.getInt(c.getColumnIndex(weathertablecoloum.mWeatherIconId));
        int time = Integer.parseInt(dateString);
        if (s >= 200 && s <= 232) {
            if(time>=18){
                return R.drawable.night_storm;
            }else {
                return R.drawable.day_storm;
            }
        } else if (s >= 300 && s <= 321) {
            if(time>=18){
                return R.drawable.night_light_rain;
            }else {
                return R.drawable.day_light_rain;
            }
        } else if (s >= 500 && s <= 504) {
            if(time>=18){
                return R.drawable.night_rain;
            }else {
                return R.drawable.day_rain;
            }
        } else if (s == 511) {
            if(time>=18){
                return R.drawable.night_snow;
            }else {
                return R.drawable.day_snow;
            }
        } else if (s >= 520 && s <= 531) {
            if(time>=18){
                return R.drawable.night_rain;
            }else {
                return R.drawable.day_rain;
            }
        } else if (s >= 600 && s <= 622) {
            if(time>=18){
                return R.drawable.night_snow;
            }else {
                return R.drawable.day_snow;
            }
        } else if (s >= 701 && s <= 761) {
            if(time>=18){
                return R.drawable.night_fog;
            }else {
                return R.drawable.day_fog;
            }
        } else if (s == 761 || s == 781) {
            if(time>=18){
                return R.drawable.night_storm;
            }else {
                return R.drawable.day_storm;
            }
        } else if (s == 800) {
            if(time>=18){
                return R.drawable.night_clear;
            }else {
                return R.drawable.day_clear;
            }
        } else if (s == 801) {
            if(time>=18){
                return R.drawable.night_light_clouds;
            }else {
                return R.drawable.day_light_cloudy;
            }
        } else if (s >= 802 && s <= 804) {
            if(time>=18){
                return R.drawable.night_clouds;
            }else {
                return R.drawable.day_clouds;
            }
        }
        return R.drawable.dummy;
    }

    private void setdat(Cursor q) {
        SharedPreferences cspa = PreferenceManager.getDefaultSharedPreferences(getContext());
        String tempunit = cspa.getString(getContext().getResources().getString(R.string.prefrenceTempUnit),"Celsius");
        if(tempunit.equalsIgnoreCase("Kelvin")){
            currentTemp.setText(q.getString(q.getColumnIndex(weathertablecoloum.mCurrentTemp)).substring(0,3));
        }else {
            currentTemp.setText(getString(R.string.formatTemp,q.getDouble(q.getColumnIndex(weathertablecoloum.mCurrentTemp))));
        }
        detailDescription.setText(q.getString(q.getColumnIndex(weathertablecoloum.mDetailCondition)));
        detailHumidity.setText(q.getString(q.getColumnIndex(weathertablecoloum.mHumidity)));
        detailPressure.setText(q.getString(q.getColumnIndex(weathertablecoloum.mPressure)));
        detailWindSpeed.setText(q.getString(q.getColumnIndex(weathertablecoloum.mSpeed)));
    }

    private void initilize(View v) {
        currentTemp = (TextView) v.findViewById(R.id.detail_currenTemp);
        detailDescription = (TextView)v.findViewById(R.id.detail_description);
        bannerImage = (ImageView)v.findViewById(R.id.detail_banner);
        detailHumidity = (TextView)v.findViewById(R.id.detail_humidity);
        detailPressure = (TextView)v.findViewById(R.id.detail_pressure);
        detailWindSpeed = (TextView)v.findViewById(R.id.detail_windspeed);
    }
}
