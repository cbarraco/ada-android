package ca.barraco.carlo.rhasspy.ui;

import android.os.Bundle;

import androidx.preference.PreferenceFragmentCompat;

import ca.barraco.carlo.rhasspy.R;

public class SettingsFragment extends PreferenceFragmentCompat {

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.root_preferences, rootKey);
    }
}