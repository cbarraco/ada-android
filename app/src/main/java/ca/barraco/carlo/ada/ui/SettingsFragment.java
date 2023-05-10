package ca.barraco.carlo.ada.ui;

import android.os.Bundle;

import androidx.preference.PreferenceFragmentCompat;

import ca.barraco.carlo.ada.R;

public class SettingsFragment extends PreferenceFragmentCompat {

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.root_preferences, rootKey);
    }
}