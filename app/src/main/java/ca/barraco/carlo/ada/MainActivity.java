package ca.barraco.carlo.ada;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.PowerManager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import ca.barraco.carlo.ada.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {
    private static final String[] Actions = {
            AdaActions.ACTION_SHOW_RECOGNITION_RESULT,
            AdaActions.ACTION_SHOW_REPLY,
            AdaActions.ACTION_SHOW_ERROR,
    };
    private boolean fromAssistantButton;

    private AdaRecognitionListener adaRecognitionListener;

    private MainActivityBroadcastReceiver mainActivityBroadcastReceiver;
    private PowerManager.WakeLock wakeLock_partial = null;

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);

        if (intent == null) {
            Logger.warning("Intent for MainActivity is null");
            return;
        }
        String action = intent.getAction();
        if (action == null) {
            Logger.information("Action for MainActivity intent is null");
            return;
        }

        if (action.equals("android.intent.action.VOICE_COMMAND")) {
            Logger.information("Handling assistant button");
            fromAssistantButton = true;
            startListening();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent = getIntent();
        if (intent == null) {
            Logger.warning("Intent for MainActivity is null");
            return;
        }
        String action = intent.getAction();
        if (action == null) {
            Logger.information("Action for MainActivity intent is null");
            return;
        }

        if (action.equals("android.intent.action.VOICE_COMMAND")) {
            Logger.information("Handling assistant button");
            fromAssistantButton = true;
            startListening();
        }

        ActivityMainBinding binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        if (fromAssistantButton) {
            binding.fab.setVisibility(View.GONE);
        } else {
            binding.fab.setOnClickListener(view -> startListening());
        }

        setUpBroadcastReceiver();
    }

    private void setUpBroadcastReceiver() {
        IntentFilter intentFilter = new IntentFilter();
        for (String action : Actions) {
            intentFilter.addAction(action);
        }

        LocalBroadcastManager localBroadcastManager = LocalBroadcastManager.getInstance(this);
        mainActivityBroadcastReceiver = new MainActivityBroadcastReceiver();
        localBroadcastManager.registerReceiver(mainActivityBroadcastReceiver, intentFilter);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        LocalBroadcastManager localBroadcastManager = LocalBroadcastManager.getInstance(this);
        localBroadcastManager.unregisterReceiver(mainActivityBroadcastReceiver);
    }

    private void startListening() {
        Logger.information("Starting speech recognition");

        int selfPermission = ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO);
        if (selfPermission == PackageManager.PERMISSION_DENIED) {
            String[] permissions = new String[]{Manifest.permission.RECORD_AUDIO};
            requestPermissions(permissions, 1);
        }

        Intent serviceIntent = new Intent(getApplicationContext(), VoiceRecognitionService.class);
        serviceIntent.putExtra("inputExtra", "Foreground Service Example in Android");
        ContextCompat.startForegroundService(this, serviceIntent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            Logger.information("opening SettingsActivity");
            Intent intent = new Intent(this, SettingsActivity.class);
            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public class MainActivityBroadcastReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, @NonNull Intent intent) {
            String action = intent.getAction();
            if (action == null) {
                Logger.warning("Received null action");
                return;
            }

            Logger.debug("Handling %s", action);
            if (action.equals(AdaActions.ACTION_SHOW_RECOGNITION_RESULT)) {
                handleShowRecognitionResult();
            } else if (action.equals(AdaActions.ACTION_SHOW_REPLY)) {
                handleShowReply();
            } else if (action.equals(AdaActions.ACTION_SHOW_ERROR)) {
                handleShowError();
            }
        }

        private void handleShowRecognitionResult() {
            if (fromAssistantButton) {
                finish();
            }
        }

        private void handleShowReply() {
            if (fromAssistantButton) {
                finish();
            }
        }

        private void handleShowError() {
            if (fromAssistantButton) {
                finish();
            }
        }


    }
}