package ca.barraco.carlo.rhasspy.ui;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import ca.barraco.carlo.rhasspy.Logger;
import ca.barraco.carlo.rhasspy.databinding.MainActivityBinding;
import ca.barraco.carlo.rhasspy.events.ShowErrorEvent;
import ca.barraco.carlo.rhasspy.events.ShowPartialResultEvent;
import ca.barraco.carlo.rhasspy.events.ShowRecognitionEvent;
import ca.barraco.carlo.rhasspy.events.ShowReplyEvent;
import ca.barraco.carlo.rhasspy.events.StartListeningEvent;
import ca.barraco.carlo.rhasspy.recognition.VoiceRecognitionService;

public class MainActivity extends AppCompatActivity {
    private boolean fromAssistantButton;
    private ca.barraco.carlo.rhasspy.databinding.MainActivityBinding binding;

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

        binding = MainActivityBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        if (fromAssistantButton) {
            binding.fab.setVisibility(View.GONE);
        } else {
            binding.fab.setOnClickListener(view -> startListening());
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
    public void handleStartListeningEvent(StartListeningEvent event) {
        binding.progressBar.setIndeterminate(true);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void handleShowRecognitionResult(@NonNull ShowRecognitionEvent showRecognitionEvent) {
        Logger.information("Showing recognition result");
        String message = showRecognitionEvent.getMessage();
        binding.textView.setText(message);
        if (fromAssistantButton) {
            finish();
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void handlePartialResult(@NonNull ShowPartialResultEvent showPartialResultEvent) {
        Logger.information("Showing partial result");
        String message = showPartialResultEvent.getMessage();
        binding.textView.setText(message);
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
            binding.progressBar.setIndeterminate(false);
            finish();
        }
    }
}
