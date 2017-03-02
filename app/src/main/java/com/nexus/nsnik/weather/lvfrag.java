package com.nexus.nsnik.weather;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

/**
 * A simple {@link Fragment} subclass.
 */
public class lvfrag extends android.support.v4.app.Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final int mId = 1001;
    CustomA ca;
    ListView lv;
    ImageView error;
    int mPosition;
    String kposikey = "posi";

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.list_vw, container, false);
        error = (ImageView) v.findViewById(R.id.error);
        lv = (ListView) v.findViewById(R.id.frag_list);
        if (chkcon()) {
            ca = new CustomA(getActivity(), null);
            lv.setAdapter(ca);
            loadfData();
            lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    mPosition = position;
                    ((Callback)getActivity()).onItemSelected(Uri.withAppendedPath(weathertable.mContentUri, String.valueOf(id)));
                }
            });
        } else {
            Cursor c = getActivity().getContentResolver().query(weathertable.mContentUri, null, null, null, null);
            if (c.getCount() != 0) {
                ca = new CustomA(getActivity(), c);
                lv.setAdapter(ca);
            } else {
                error.setVisibility(View.VISIBLE);
            }
        }
        if(savedInstanceState!=null&&savedInstanceState.containsKey(kposikey)){
            mPosition = savedInstanceState.getInt(kposikey);
        }
        return v;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if(mPosition!=ListView.INVALID_POSITION){
            outState.putInt(kposikey,mPosition);
        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        switch (id) {
            case mId:
                return new CursorLoader(getActivity(), weathertable.mContentUri, null, null, null, null);
        }
        return null;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        ca.swapCursor(data);
        if(mPosition!=ListView.INVALID_POSITION){
            lv.setSelection(mPosition);
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        ca.swapCursor(null);
    }

    private void loadfData() {
        if (getActivity().getSupportLoaderManager().getLoader(mId) == null) {
            getActivity().getSupportLoaderManager().initLoader(mId, null, this).forceLoad();
        } else {
            getActivity().getSupportLoaderManager().restartLoader(mId, null, this).forceLoad();
        }
    }

    private boolean chkcon() {
        ConnectivityManager cm = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null && activeNetwork.isConnectedOrConnecting();
        return isConnected;
    }

    public interface Callback {
        public void onItemSelected(Uri dateUri);
    }
}


