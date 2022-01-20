package ca.barraco.carlo.ada;

import android.os.Build;
import android.os.Bundle;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

public class AboutFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_about, container, false);
        TextView aboutTextView = view.findViewById(R.id.aboutTextView);
        String aboutText = getString(R.string.about_page);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            aboutTextView.setText(Html.fromHtml(aboutText, Html.FROM_HTML_MODE_LEGACY));
        } else {
            aboutTextView.setText(Html.fromHtml(aboutText));
        }
        return view;
    }
}