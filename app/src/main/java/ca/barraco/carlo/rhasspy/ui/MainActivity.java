package ca.barraco.carlo.rhasspy.ui;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import ca.barraco.carlo.rhasspy.Logger;
import ca.barraco.carlo.rhasspy.R;
import ca.barraco.carlo.rhasspy.databinding.ActivityMainBinding;
import ca.barraco.carlo.rhasspy.events.ShowErrorEvent;
import ca.barraco.carlo.rhasspy.events.ShowRecognitionEvent;
import ca.barraco.carlo.rhasspy.events.ShowReplyEvent;
import ca.barraco.carlo.rhasspy.recognition.VoiceRecognitionService;

public class MainActivity extends AppCompatActivity {
    private final SettingsFragment settingsFragment = new SettingsFragment();
    private final AboutFragment aboutFragment = new AboutFragment();
    private final ChatboxFragment chatboxFragment = new ChatboxFragment();
    private boolean fromAssistantButton;

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

        EventBus.getDefault().register(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
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

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void handleShowRecognitionResult(ShowRecognitionEvent showRecognitionEvent) {
        if (fromAssistantButton) {
            finish();
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void handleShowReply(ShowReplyEvent showReplyEvent) {
        if (fromAssistantButton) {
            finish();
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void handleShowError(ShowErrorEvent showErrorEvent) {
        if (fromAssistantButton) {
            finish();
        }
    }
}
