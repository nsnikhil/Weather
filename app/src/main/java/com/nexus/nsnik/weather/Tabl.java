package com.nexus.nsnik.weather;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;

public class Tabl extends SQLiteOpenHelper {

    private static final int mDataBaseVersion = 5;

    private static final String mDataBaseName = "Forecast";

    private static final String mDropTable = "DROP TABLE IF EXISTS " + weathertable.mTableName;


    public Tabl(Context context) {
        super(context, mDataBaseName, null, mDataBaseVersion);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {

        String tble = "CREATE TABLE "+ weathertable.mTableName + " ("
                + weathertablecoloum.mUid + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                +weathertablecoloum.mDate + " LONG,"
                +weathertablecoloum.mMinTemp + " VARCHAR(255),"
                +weathertablecoloum.mMaxTemp + " VARCHAR(255),"
                +weathertablecoloum.mPressure+ " VARCHAR(255),"
                +weathertablecoloum.mHumidity+ " VARCHAR(255),"
                +weathertablecoloum.mCondition+ " VARCHAR(255),"
                +weathertablecoloum.mDetailCondition+ " VARCHAR(255),"
                +weathertablecoloum.mSpeed+  " VARCHAR(255), "
                +weathertablecoloum.mWeatherIconId + " INTEGER DEFAULT 800, "
                +weathertablecoloum.mCurrentTemp + " VARCHAR(255)"
                + ");";
        sqLiteDatabase.execSQL(tble);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL(mDropTable);
        onCreate(sqLiteDatabase);
    }
}
