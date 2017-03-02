package com.nexus.nsnik.weather;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;



public class CustomA extends CursorAdapter {

    private static final int vTypeToday = 0;
    private static final int vTypeRestOfTheWeek = 1;

    public CustomA(Context context, Cursor c) {
        super(context, c);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        int viewType = getItemViewType(cursor.getPosition());
        int layoutId = -1;
        if(viewType==vTypeToday){
            layoutId = R.layout.today_layout;
        }else if (viewType==vTypeRestOfTheWeek){
            layoutId = R.layout.custom_view;
        }
        View v =  LayoutInflater.from(context).inflate(layoutId, parent, false);
        MyViewHolder vh = new MyViewHolder(v);
        v.setTag(vh);
        return v;
    }

    @Override
    public int getItemViewType(int position) {
        return (position==0) &&(!MainActivity.mTwoPane)? vTypeToday : vTypeRestOfTheWeek ;
    }

    @Override
    public int getViewTypeCount() {
        return 2;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {

        SharedPreferences cspa = PreferenceManager.getDefaultSharedPreferences(context);
        String tempunit = cspa.getString(context.getResources().getString(R.string.prefrenceTempUnit),"Celsius");
        String cur = cursor.getString(cursor.getColumnIndex(weathertablecoloum.mCurrentTemp));
        String min = cursor.getString(cursor.getColumnIndex(weathertablecoloum.mMinTemp));
        String max = cursor.getString(cursor.getColumnIndex(weathertablecoloum.mMaxTemp));

        MyViewHolder myh = (MyViewHolder) view.getTag();
        myh.tv.setText(getdt(cursor));
        myh.tv2.setText(cursor.getString(cursor.getColumnIndex(weathertablecoloum.mCondition)));
        if(cursor.getPosition()==0){
            if(tempunit.equalsIgnoreCase("Kelvin")){
                myh.tv3.setText(cur.substring(0,3));
            }else {
                myh.tv3.setText(context.getString(R.string.formatTemp,Double.parseDouble(cur)));
            }if(tempunit.equalsIgnoreCase("Kelvin")){
                myh.tv4.setText(max.substring(0,3)+"/"+min.substring(0,3));
            }else {
                myh.tv4.setText(max.substring(0,2)+"/"+min.substring(0,2));
            }
        }else {
            if (tempunit.equalsIgnoreCase("Kelvin")) {
                myh.tv3.setText(max.substring(0,3));
                myh.tv4.setText(min.substring(0,3));
            }else {
                myh.tv3.setText(max.substring(0,2));
                myh.tv4.setText(min.substring(0,2));
            }
        }
        myh.img1.setImageResource(getImgId(cursor));
        if(cursor.getPosition()==0){
            String nm = cspa.getString(context.getResources().getString(R.string.prefrenceCityName),"Default");
            myh.ctyNam.setText(nm);
        }
    }

    public static int getImgId(Cursor c) {
        int s = c.getInt(c.getColumnIndex(weathertablecoloum.mWeatherIconId));
        if (s >= 200 && s <= 232) {
            if(c.getPosition()==0&&(!MainActivity.mTwoPane)){
                return R.drawable.art_storm;
            }else {
                return R.drawable.ic_storm;
            }

        } else if (s >= 300 && s <= 321) {
            if(c.getPosition()==0&&(!MainActivity.mTwoPane)){
                return R.drawable.art_light_rain;
            }else {
                return R.drawable.ic_light_rain;
            }
        } else if (s >= 500 && s <= 504) {
            if(c.getPosition()==0&&(!MainActivity.mTwoPane)){
                return R.drawable.art_rain;
            }else {
                return R.drawable.ic_rain;
            }
        } else if (s == 511) {
            if(c.getPosition()==0&&(!MainActivity.mTwoPane)){
                return R.drawable.art_snow;
            }else {
                return R.drawable.ic_snow;
            }
        } else if (s >= 520 && s <= 531) {
            if(c.getPosition()==0&&(!MainActivity.mTwoPane)){
                return R.drawable.art_rain;
            }else {
                return R.drawable.ic_rain;
            }
        } else if (s >= 600 && s <= 622) {
            if(c.getPosition()==0&&(!MainActivity.mTwoPane)){
                return R.drawable.art_snow;
            }else {
                return R.drawable.ic_snow;
            }
        } else if (s >= 701 && s <= 761) {
            if(c.getPosition()==0&&(!MainActivity.mTwoPane)){
                return R.drawable.art_fog;
            }else {
                return R.drawable.ic_fog;
            }
        } else if (s == 761 || s == 781) {
            if(c.getPosition()==0&&(!MainActivity.mTwoPane)){
                return R.drawable.art_storm;
            }else {
                return R.drawable.ic_storm;
            }
        } else if (s == 800) {
            if(c.getPosition()==0&&(!MainActivity.mTwoPane)){
                return R.drawable.art_clear;
            }else {
                return R.drawable.ic_clear;
            }
        } else if (s == 801) {
            if(c.getPosition()==0&&(!MainActivity.mTwoPane)){
                return R.drawable.art_light_clouds;
            }else {
                return R.drawable.ic_light_clouds;
            }
        } else if (s >= 802 && s <= 804) {
            if(c.getPosition()==0&&(!MainActivity.mTwoPane)){
                return R.drawable.art_clouds;
            }else {
                return R.drawable.ic_cloudy;
            }
        }
        return -1;
    }

    private String getdt(Cursor cursor) {
        long dt = cursor.getLong(cursor.getColumnIndex(weathertablecoloum.mDate));
        Date date = new Date(dt * 1000L);
        SimpleDateFormat sdf = new SimpleDateFormat("EEEE, MMM d, yy");
        if (gettoday().equals(sdf.format(date))) {
            SimpleDateFormat sdf3 = new SimpleDateFormat("EEE, MMM d");
            return "Today" + "\n" + sdf3.format(date);
        }else if(gettomorrow().equals(sdf.format(date))){
            return "Tomorrow";
        }else {
            SimpleDateFormat sdf2 = new SimpleDateFormat("EEEE");
            return sdf2.format(date);
        }
    }

    private String gettoday(){
        Calendar c = Calendar.getInstance();
        SimpleDateFormat df = new SimpleDateFormat("EEEE, MMM d, yy");
        String formattedDate = df.format(c.getTime());
        return formattedDate;
    }

    private String gettomorrow(){
        Calendar c = Calendar.getInstance();
        c.add(Calendar.DAY_OF_YEAR, 1);
        SimpleDateFormat df = new SimpleDateFormat("EEEE, MMM d, yy");
        String formattedDate = df.format(c.getTime());
        return formattedDate;
    }

    public static class MyViewHolder{

        public final TextView tv;
        public final TextView tv2;
        public final TextView tv3;
        public final TextView tv4;
        public final ImageView img1;
        public final TextView ctyNam;

        public MyViewHolder(View view){
            tv = (TextView) view.findViewById(R.id.customListItem);
            tv2 = (TextView) view.findViewById(R.id.customListItem2);
            tv3 = (TextView) view.findViewById(R.id.customListItem3);
            tv4 = (TextView) view.findViewById(R.id.customListItem4);
            img1 = (ImageView) view.findViewById(R.id.customListImage);
            ctyNam = (TextView) view.findViewById(R.id.customListCityName);
        }
    }

}
