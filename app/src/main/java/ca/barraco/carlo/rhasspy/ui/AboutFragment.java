package ca.barraco.carlo.rhasspy.ui;

import android.os.Build;
import android.os.Bundle;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import ca.barraco.carlo.rhasspy.R;

public class AboutFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_about, container, false);

        TextView aboutTextView = view.findViewById(R.id.aboutTextView);
        String aboutText = getString(R.string.about_page);
        String aboutHtml = renderHtml(aboutText);
        aboutTextView.setText(aboutHtml);

        return view;
    }

    @NonNull
    private String renderHtml(String aboutText) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            aboutText = Html.fromHtml(aboutText, Html.FROM_HTML_MODE_LEGACY).toString();
        } else {
            aboutText = Html.fromHtml(aboutText).toString();
        }
        return aboutText;
    }
}