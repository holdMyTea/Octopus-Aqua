package ua.com.octopus_aqua.preferences;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import ua.com.octopus_aqua.inteface.R;

public class PreferencesActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        getFragmentManager().beginTransaction().replace(R.id.settingsFragmentContainer, new PreferencesFragment()).commit();
    }
}
