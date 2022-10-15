package ca.barraco.carlo.rhasspy.ui;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.adonmo.killerbee.AndroidMQTTClient;
import com.adonmo.killerbee.IMQTTConnectionCallback;
import com.adonmo.killerbee.action.MQTTActionStatus;
import com.adonmo.killerbee.adapter.ConnectOptions;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.Arrays;
import java.util.concurrent.ScheduledThreadPoolExecutor;

import ca.barraco.carlo.rhasspy.Logger;
import ca.barraco.carlo.rhasspy.databinding.MainActivityBinding;
import ca.barraco.carlo.rhasspy.events.recognition.PartialRecognitionEvent;
import ca.barraco.carlo.rhasspy.events.recognition.RecognitionErrorEvent;
import ca.barraco.carlo.rhasspy.events.recognition.ShowReplyEvent;
import ca.barraco.carlo.rhasspy.events.recognition.StartListeningEvent;
import ca.barraco.carlo.rhasspy.events.recognition.SuccessfulRecognitionEvent;
import ca.barraco.carlo.rhasspy.recognition.VoiceRecognitionService;

public class MainActivity extends AppCompatActivity implements IMQTTConnectionCallback {
    private boolean fromAssistantButton;
    private ca.barraco.carlo.rhasspy.databinding.MainActivityBinding binding;
    private AndroidMQTTClient mqttClient;

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

        HandlerThread mqttThread = new HandlerThread("mqttThread");
        mqttThread.start();
        Handler mqttHandler = new Handler(mqttThread.getLooper());

        /* As it stands a minimum of 4 threads seems to be necessary to let the MQTT client run
            as it blocks a few of them(3 based on testing) with a looper  most likely */
        ScheduledThreadPoolExecutor executor = new ScheduledThreadPoolExecutor(4);
        mqttClient = new AndroidMQTTClient(
                new ConnectOptions(
                        "RhasspyAssistant",
                        "tcp://10.0.0.42:1883",
                        null,
                        null,
                        30,
                        10,
                        null,
                        null,
                        1,
                        true,
                        null,
                        null,
                        false,
                        null,
                        true,
                        30,
                        null,
                        0,
                        true,
                        60000,
                        null,
                        1
                ),
                mqttHandler,
                this,
                executor
        );
        mqttClient.connect();

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
        binding.recognizedSpeechTextView.setText(message);

        binding.progressBar.setIndeterminate(false);

        if (fromAssistantButton) {
            finish();
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void handlePartialResult(@NonNull PartialRecognitionEvent event) {
        Logger.information("Showing partial result");
        String message = event.getResult();
        binding.recognizedSpeechTextView.setText(message);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void handleShowReply(@NonNull ShowReplyEvent event) {
        Logger.information("Showing reply");
        if (fromAssistantButton) {
            finish();
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void handleShowError(@NonNull RecognitionErrorEvent event) {
        Logger.information("Showing error");
        binding.recognizedIntentTextView.setText(event.getMessage());
        binding.progressBar.setIndeterminate(false);
        if (fromAssistantButton) {
            finish();
        }
    }

    @Override
    public void connectActionFinished(@NonNull MQTTActionStatus status, @NonNull ConnectOptions connectOptions, @Nullable Throwable throwable) {
        if (status == MQTTActionStatus.SUCCESS) {
            mqttClient.subscribe("Hello", 1);
            mqttClient.publish("HelloFromRhasspy", "Hello from Android".getBytes(), 1, false);
        } else {
            Logger.error("Connection Action Failed for [${connectOptions.clientID}] to [${connectOptions.serverURI}]");
        }
    }

    @Override
    public void disconnectActionFinished(@NonNull MQTTActionStatus status, @Nullable Throwable throwable) {
        Logger.debug("Disconnect Action Finished: %s", status);
    }

    @Override
    public void publishActionFinished(@NonNull MQTTActionStatus status, @NonNull byte[] messagePayload, @Nullable Throwable throwable) {
        if (status == MQTTActionStatus.SUCCESS) {
            Logger.debug("Published message %s", new String(messagePayload));
        }
    }

    @Override
    public void subscribeActionFinished(@NonNull MQTTActionStatus status, @NonNull String topic, @Nullable Throwable throwable) {
        if (status == MQTTActionStatus.SUCCESS) {
            Logger.debug("Subscribed to topic %s", topic);
        }
    }

    @Override
    public void subscribeMultipleActionFinished(@NonNull MQTTActionStatus status, @NonNull String[] topics, @Nullable Throwable throwable) {
        if (status == MQTTActionStatus.SUCCESS) {
            Logger.debug("Subscribed to topics %s", Arrays.toString(topics));
        }
    }

    @Override
    public void connectionLost(@NonNull ConnectOptions connectOptions, @Nullable Throwable throwable) {
        Logger.debug("Connection lost for %s to %s", connectOptions.getClientID(), connectOptions.getServerURI());
    }

    @Override
    public void messageArrived(@Nullable String topic, @Nullable byte[] message) {
        Logger.debug("Received message %s", new String(message));
    }
}
