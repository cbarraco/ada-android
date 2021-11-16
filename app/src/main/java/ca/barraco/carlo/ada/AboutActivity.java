package ca.barraco.carlo.ada;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Build;
import android.os.Bundle;
import android.text.Html;
import android.widget.TextView;

public class AboutActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);

        TextView aboutTextView = findViewById(R.id.aboutTextView);
        String aboutText = getString(R.string.about_page);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            aboutTextView.setText(Html.fromHtml(aboutText, Html.FROM_HTML_MODE_LEGACY));
        } else {
            aboutTextView.setText(Html.fromHtml(aboutText));
        }
    }
}