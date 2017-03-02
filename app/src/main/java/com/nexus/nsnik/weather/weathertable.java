package com.nexus.nsnik.weather;


import android.net.Uri;

public class weathertable {

    static final String mTableName = "weathertable";

    public static final String mScheme = "content://";
    public static final String mAuthority = "com.nexus.nsnik.weather";
    public static final Uri mBaseUri = Uri.parse(mScheme+mAuthority);
    public static final Uri mContentUri = Uri.withAppendedPath(mBaseUri,mTableName);
}

class weathertablecoloum {

    static final String mUid = "_id";
    static final String mWeatherIconId = "WeatherId";
    static final String mCurrentTemp = "Temp";
    static final String mDate = "Date";
    static final String mMinTemp = "MinTemp";
    static final String mMaxTemp = "MaxTemp";
    static final String mPressure = "Pressure";
    static final String mHumidity = "Humidity";
    static final String mCondition = "Condition";
    static final String mDetailCondition = "DetailCondition";
    static final String mSpeed = "WindSpeed";
}
