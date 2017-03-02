package com.nexus.nsnik.weather;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.Nullable;

/**
 * Created by nsnik on 25-Oct-16.
 */

public class WeatherProvider extends ContentProvider{


    Tabl newtb;

    private static final int uAllItems = 5001;
    private static final int uSingleItem = 5002;

    static UriMatcher sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    static {
        sUriMatcher.addURI(weathertable.mAuthority,weathertable.mTableName,uAllItems);
        sUriMatcher.addURI(weathertable.mAuthority,weathertable.mTableName+"/#",uSingleItem);
    }

    @Override
    public boolean onCreate() {
        newtb = new Tabl(getContext());
        return false;
    }

    @Nullable
    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        SQLiteDatabase sdb = newtb.getReadableDatabase();
        Cursor c;
        switch (sUriMatcher.match(uri)){
            case uAllItems:
                c = sdb.query(weathertable.mTableName,null,null,null,null,null,null);
                break;
            case uSingleItem:
                selection = weathertablecoloum.mUid + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                c = sdb.query(weathertable.mTableName,null,selection,selectionArgs,null,null,null);
                break;
            default:
                throw new IllegalArgumentException("Cannot parse from the uri"+uri);
        }
        c.setNotificationUri(getContext().getContentResolver(),uri);
        return c;
    }

    @Nullable
    @Override
    public String getType(Uri uri) {
        return null;
    }

    @Nullable
    @Override
    public Uri insert(Uri uri, ContentValues values) {
        int match = sUriMatcher.match(uri);
        switch (match){
            case uAllItems:
                return insertVale(uri,values);
            default:
                throw new IllegalArgumentException("Cannot insert into the uri :"+ uri);
        }
    }

    private Uri insertVale(Uri u, ContentValues c){
        SQLiteDatabase sdb  = newtb.getWritableDatabase();
        long id = sdb.insert(weathertable.mTableName,null,c);
        if(id==0){
            return null;
        }else {
            getContext().getContentResolver().notifyChange(u,null);
            return ContentUris.withAppendedId(u,id);
        }
    }

    @Override
    public int bulkInsert(Uri uri, ContentValues[] values) {
        SQLiteDatabase sdb  = newtb.getWritableDatabase();
        sdb.beginTransaction();
        int match = sUriMatcher.match(uri);
        int i = 0;
        switch (match){
            case uAllItems:
                try {
                    for (ContentValues cv : values) {
                        long newID = sdb.insertOrThrow(weathertable.mTableName, null, cv);
                        if (newID <= 0) {
                            throw new SQLException("Failed to insert row into " + uri);
                        }
                    }
                    sdb.setTransactionSuccessful();
                    getContext().getContentResolver().notifyChange(uri, null);
                    i = values.length;}
                    finally {
                        sdb.endTransaction();
                    }
                    return i;
            default:
                throw new IllegalArgumentException("Cannot insert into the uri :"+ uri);
        }
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        SQLiteDatabase sdb  = newtb.getWritableDatabase();
        int match = sUriMatcher.match(uri);
        switch (match){
            case uAllItems:
                getContext().getContentResolver().notifyChange(uri,null);
                return sdb.delete(weathertable.mTableName,null,null);
            case uSingleItem:
                selection = weathertablecoloum.mUid + " >=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                getContext().getContentResolver().notifyChange(uri,null);
                return sdb.delete(weathertable.mTableName,selection,selectionArgs);
            default:
                throw new IllegalArgumentException("Cannot delete from the uri :"+ uri);
        }
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        int match = sUriMatcher.match(uri);
        switch (match){
            case uAllItems:
                return upadteVal(uri,values,null,null);
            case uSingleItem:
                selection = weathertablecoloum.mUid + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                return upadteVal(uri,values,selection,selectionArgs);
            default:
                throw new IllegalArgumentException("Cannot update into the uri :"+ uri);
        }
    }

    private int upadteVal(Uri u, ContentValues c,String s,String[] sa){
        SQLiteDatabase sdb  = newtb.getWritableDatabase();
        int id =sdb.update(weathertable.mTableName,c,s,sa);
        if(id==0){
            return 0;
        }else {
            getContext().getContentResolver().notifyChange(u,null);
            return id;
        }
    }
}
