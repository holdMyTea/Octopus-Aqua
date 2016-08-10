package ua.com.octopus_aqua.preferences;


import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.SwitchPreference;
import android.util.Log;

import ua.com.octopus_aqua.inteface.R;

public class PreferencesFragment extends PreferenceFragment{

    public static final String SHARED_PREFERENCE_NAME = "preference";

    SwitchPreference switchWaterEnd;
    SwitchPreference switchPlantWater;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences);

        Log.d("MY_TAG", "Got pref:" + getResources().getString(R.string.notifications));

        switchWaterEnd = (SwitchPreference) findPreference(getResources().getString(R.string.notif_water_end));
        switchWaterEnd.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                SharedPreferences sharedPreferences = getActivity().getSharedPreferences(SHARED_PREFERENCE_NAME,Context.MODE_PRIVATE);
                sharedPreferences.edit().putBoolean(switchWaterEnd.getKey(),switchWaterEnd.isChecked()).apply();
                return true;
            }
        });

        switchPlantWater = (SwitchPreference) findPreference(getResources().getString(R.string.notif_plant_water));
        switchPlantWater.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                SharedPreferences sharedPreferences = getActivity().getSharedPreferences(SHARED_PREFERENCE_NAME,Context.MODE_PRIVATE);
                sharedPreferences.edit().putBoolean(switchPlantWater.getKey(),switchPlantWater.isChecked()).apply();
                return true;
            }
        });


    }


}
