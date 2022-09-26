package ca.barraco.carlo.rhasspy.ui;

import android.Manifest;
import android.content.Context;
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
import ca.barraco.carlo.rhasspy.events.recognition.PartialRecognitionEvent;
import ca.barraco.carlo.rhasspy.events.recognition.RecognitionErrorEvent;
import ca.barraco.carlo.rhasspy.events.recognition.ShowReplyEvent;
import ca.barraco.carlo.rhasspy.events.recognition.StartListeningEvent;
import ca.barraco.carlo.rhasspy.events.recognition.SuccessfulRecognitionEvent;
import ca.barraco.carlo.rhasspy.recognition.VoiceRecognitionService;

public class MainActivity extends AppCompatActivity {
    private boolean fromAssistantButton;
    private ca.barraco.carlo.rhasspy.databinding.MainActivityBinding binding;

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);

        handleAssistantButtonIfPressed();
    }

    private void handleAssistantButtonIfPressed() {
        Intent intent = getIntent();
        if (intent == null) {
            Logger.debug("Intent for MainActivity is null");
            return;
        }
        String action = intent.getAction();
        if (action == null) {
            Logger.debug("Action for MainActivity intent is null");
            return;
        }

        if (action.equals("android.intent.action.VOICE_COMMAND")) {
            Logger.information("Handling assistant button");
            fromAssistantButton = true;
            binding.fab.setVisibility(View.GONE);
            startListening();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        handleAssistantButtonIfPressed();

        binding = MainActivityBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.fab.setOnClickListener(view -> startListening());

        EventBus.getDefault().register(this);

        requestPermissions();
    }

    private void requestPermissions() {
        int selfPermission = ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO);
        if (selfPermission == PackageManager.PERMISSION_DENIED) {
            String[] permissions = new String[]{Manifest.permission.RECORD_AUDIO};
            requestPermissions(permissions, 1);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    private void startListening() {
        Logger.information("Starting speech recognition");

        Context applicationContext = getApplicationContext();
        Intent serviceIntent = new Intent(applicationContext, VoiceRecognitionService.class);
        ContextCompat.startForegroundService(this, serviceIntent);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void handleStartListeningEvent(@NonNull StartListeningEvent event) {
        binding.progressBar.setIndeterminate(true);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void handleShowRecognitionResult(@NonNull SuccessfulRecognitionEvent event) {
        Logger.information("Showing recognition result");

        String message = event.getResult();
        binding.textView.setText(message);

        if (fromAssistantButton) {
            finish();
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void handlePartialResult(@NonNull PartialRecognitionEvent event) {
        Logger.information("Showing partial result");
        String message = event.getResult();
        binding.textView.setText(message);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void handleShowReply(@NonNull ShowReplyEvent event) {
        if (fromAssistantButton) {
            finish();
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void handleShowError(@NonNull RecognitionErrorEvent event) {
        binding.textView2.setText(event.getMessage());
        if (fromAssistantButton) {
            binding.progressBar.setIndeterminate(false);
            finish();
        }
    }
}
