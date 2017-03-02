package com.nexus.nsnik.weather;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;


public class MainActivity extends AppCompatActivity implements lvfrag.Callback, GoogleApiClient.ConnectionCallbacks,GoogleApiClient.OnConnectionFailedListener{

    Toolbar tb;
    Snackbar sb;
    private static final String fDetailsTag = "DTAG";
    public static boolean mTwoPane = false;
    public static final int pRequestCode = 1080;
    private static final String[] mPermissions = {Manifest.permission.ACCESS_FINE_LOCATION};
    GoogleApiClient  mGoogleApiClient;
    Location mLastLocation;
    public  String mLontitude;
    public  String mLatitude;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        checkper();
        initilize();
        getLoc(this);
        setSupportActionBar(tb);
        tb.setTitleTextColor(getResources().getColor(R.color.white));
        newdat();
        if(savedInstanceState==null){
            getSupportFragmentManager().beginTransaction().add(R.id.main_list,new lvfrag()).commit();
        }
        if(findViewById(R.id.fragDetails)!=null){
            mTwoPane = true;
        }else {
            mTwoPane = false;
        }
        WeatherSyncAdapter.initializeSyncAdapter(this);
    }




    private void checkper() {
        if((ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION))!= PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,mPermissions,pRequestCode);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case pRequestCode: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(this, "Location Allowed", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, "Location Denied", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    private boolean checkcon(){
        ConnectivityManager cm = (ConnectivityManager)this.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null && activeNetwork.isConnectedOrConnecting();
        return isConnected;
    }

    private boolean newdat(){
        inibar();
        if(checkcon()){
            loadService();
            return true;
        }
        setbar();
        return false;
    }

    private void inibar(){
        sb = Snackbar.make(findViewById(R.id.main_layout),getResources().getString(R.string.noint),Snackbar.LENGTH_INDEFINITE);
    }

    private void setbar(){
        sb.setAction("Retry", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                newdat();
            }
        });
        sb.show();
    }

    private void initilize() {
        tb = (Toolbar)findViewById(R.id.main_toolbar);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater mi = getMenuInflater();
        mi.inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id)
        {
            case R.id.menu_setting :
                Intent is = new Intent(MainActivity.this,Prefs.class);
                startActivity(is);
                break;
            case R.id.menu_about:
                AlertDialog.Builder adb = new AlertDialog.Builder(this);
                adb.setTitle(getResources().getString(R.string.app_name));
                adb.setMessage(getResources().getString(R.string.abt));
                adb.create();
                adb.show();
                break;
        }
        return true;
    }

    private void loadService(){
        WeatherSyncAdapter.syncImmediately(this);
    }

    @Override
    public void onItemSelected(Uri dateUri) {
        if(mTwoPane){
            Bundle b = new Bundle();
            b.putParcelable("urdi",dateUri);
            delfrag fg = new delfrag();
            fg.setArguments(b);
            getSupportFragmentManager().beginTransaction().replace(R.id.fragDetails,fg,fDetailsTag).commit();
        }else {
            Intent dtl = new Intent(MainActivity.this,Detail.class).setData(dateUri);
            startActivity(dtl);
        }
    }

    private void getLoc(Context t) {
        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(t)
                    .addApi(LocationServices.API)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .build();
        }
    }

    @Override
    protected void onStart() {
        mGoogleApiClient.connect();
        super.onStart();
    }

    @Override
    protected void onStop() {
        mGoogleApiClient.disconnect();
        super.onStop();
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        if (mLastLocation != null) {
            mLatitude = String.valueOf(mLastLocation.getLatitude());
            mLontitude = String.valueOf(mLastLocation.getLongitude());
            SharedPreferences spf = PreferenceManager.getDefaultSharedPreferences(this);
            SharedPreferences.Editor spfe = spf.edit();
            spfe.putString(getResources().getString(R.string.bundleLongitude),mLontitude);
            spfe.putString(getResources().getString(R.string.bundleLatitude),mLatitude);
            spfe.commit();
        }

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }
}


