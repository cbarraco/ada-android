package ca.barraco.carlo.ada.ui;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import ca.barraco.carlo.ada.Logger;
import ca.barraco.carlo.ada.R;
import ca.barraco.carlo.ada.databinding.ActivityMainBinding;
import ca.barraco.carlo.ada.recognition.VoiceRecognitionService;

public class MainActivity extends AppCompatActivity {
    private static final String[] Actions = {
            AdaActions.ACTION_SHOW_RECOGNITION_RESULT,
            AdaActions.ACTION_SHOW_REPLY,
            AdaActions.ACTION_SHOW_ERROR,
    };
    private final SettingsFragment settingsFragment = new SettingsFragment();
    private final AboutFragment aboutFragment = new AboutFragment();
    private final ChatboxFragment chatboxFragment = new ChatboxFragment();
    private boolean fromAssistantButton;
    private MainActivityBroadcastReceiver mainActivityBroadcastReceiver;

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

        ca.barraco.carlo.ada.databinding.ActivityMainBinding binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.bottomAppBar.setOnMenuItemClickListener(item -> {
            int id = item.getItemId();
            FragmentManager supportFragmentManager = getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = supportFragmentManager.beginTransaction();

            if (id == R.id.action_settings) {
                Logger.information("Opening Settings from action bar");
                fragmentTransaction
                        .replace(R.id.mainFragmentContainer, settingsFragment, "SETTINGS")
                        .commit();
            } else if (id == R.id.action_about) {
                Logger.debug("Opening About from action bar");
                fragmentTransaction
                        .replace(R.id.mainFragmentContainer, aboutFragment, "ABOUT")
                        .commit();
            }
            return true;
        });

        binding.bottomAppBar.setNavigationOnClickListener(v -> {
            Logger.information("Opening Home from action bar");
            FragmentManager supportFragmentManager = getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = supportFragmentManager.beginTransaction();
            fragmentTransaction
                    .replace(R.id.mainFragmentContainer, chatboxFragment, "CHATBOX")
                    .commit();
        });

        if (fromAssistantButton) {
            binding.fab.setVisibility(View.GONE);
        } else {
            binding.fab.setOnClickListener(view -> {
                FragmentManager supportFragmentManager = getSupportFragmentManager();
                supportFragmentManager.beginTransaction().replace(R.id.mainFragmentContainer, chatboxFragment).commit();
                startListening();
            });
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