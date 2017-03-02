package com.nexus.nsnik.weather;

import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

public class Prefs extends AppCompatActivity {

    Toolbar ptb;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pref_view);
        getFragmentManager().beginTransaction().add(R.id.pref_view,new prefrag()).commit();
        ptb = (Toolbar)findViewById(R.id.pref_toolbar);
        setSupportActionBar(ptb);
        ptb.setTitleTextColor(getResources().getColor(R.color.white));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }
    public static class prefrag extends PreferenceFragment {
        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.prefs);
            ListPreference lp = (ListPreference) findPreference(getString(R.string.prefrenceTempUnit));
            lp.setSummary(lp.getEntry().toString());
            lp.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                @Override
                public boolean onPreferenceChange(Preference preference, Object newValue) {
                    preference.setSummary(newValue.toString());
                    WeatherSyncAdapter.syncImmediately(getContext());
                    return true;
                }
            });
        }
    }
}


