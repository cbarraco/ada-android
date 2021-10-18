package ca.barraco.carlo.ada;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.speech.SpeechRecognizer;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import ca.barraco.carlo.ada.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {

    private static final String ACTION_START_RECOGNIZING = "MainActivity.ACTION_START_RECOGNIZING";
    private static final String ACTION_START_LISTENING = "MainActivity.ACTION_START_LISTENING";
    private static final String ACTION_STOP_LISTENING = "MainActivity.ACTION_STOP_LISTENING";
    private static final String ACTION_SHOW_RECOGNITION_RESULT = "MainActivity.ACTION_SHOW_RECOGNITION_RESULT";
    private static final String ACTION_SHOW_REPLY = "MainActivity.ACTION_SHOW_REPLY";
    private static final String EXTRA_MESSAGE = "MainActivity.EXTRA_MESSAGE";
    private static final String[] Actions = {
            ACTION_START_RECOGNIZING,
            ACTION_START_LISTENING,
            ACTION_STOP_LISTENING,
            ACTION_SHOW_RECOGNITION_RESULT,
            ACTION_SHOW_REPLY
    };
    private TextView statusView;
    private boolean fromAssistantButton;
    private LinearLayout chatLayout;

    public static void startRecognizing(Context context) {
        LocalBroadcastManager localBroadcastManager = LocalBroadcastManager.getInstance(context);
        Intent intent = new Intent(ACTION_START_RECOGNIZING);
        localBroadcastManager.sendBroadcast(intent);
    }

    public static void startListening(Context context) {
        LocalBroadcastManager localBroadcastManager = LocalBroadcastManager.getInstance(context);
        Intent intent = new Intent(ACTION_START_LISTENING);
        localBroadcastManager.sendBroadcast(intent);
    }

    public static void stopListening(Context context) {
        LocalBroadcastManager localBroadcastManager = LocalBroadcastManager.getInstance(context);
        Intent intent = new Intent(ACTION_STOP_LISTENING);
        localBroadcastManager.sendBroadcast(intent);
    }

    public static void showRecognitionResult(Context context, String reply) {
        LocalBroadcastManager localBroadcastManager = LocalBroadcastManager.getInstance(context);
        Intent intent = new Intent(ACTION_SHOW_RECOGNITION_RESULT);
        intent.putExtra(EXTRA_MESSAGE, reply);
        localBroadcastManager.sendBroadcast(intent);
    }

    public static void showReply(Context context, String reply) {
        LocalBroadcastManager localBroadcastManager = LocalBroadcastManager.getInstance(context);
        Intent intent = new Intent(ACTION_SHOW_REPLY);
        intent.putExtra(EXTRA_MESSAGE, reply);
        localBroadcastManager.sendBroadcast(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ActivityMainBinding binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.toolbar);

        handleAssistantButton();

        statusView = findViewById(R.id.statusView);

        chatLayout = findViewById(R.id.chatLayout);

        binding.fab.setOnClickListener(view -> startListening());

        setUpBroadcastReceiver();
    }

    private void setUpBroadcastReceiver() {
        IntentFilter intentFilter = new IntentFilter();
        for (String action : Actions) {
            intentFilter.addAction(action);
        }

        LocalBroadcastManager localBroadcastManager = LocalBroadcastManager.getInstance(this);
        MainActivityBroadcastReceiver mainActivityBroadcastReceiver = new MainActivityBroadcastReceiver();
        localBroadcastManager.registerReceiver(mainActivityBroadcastReceiver, intentFilter);
    }

    private void handleAssistantButton() {
        try {
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
        } catch (Exception exception) {
            Logger.error("Error handling assistant button", exception);
        }
    }

    private void startListening() {
        // TODO move listening and processing logic to service
        Logger.information("starting speech recognition");
        Context context = getApplicationContext();
        SpeechRecognizer speechRecognizer = SpeechRecognizer.createSpeechRecognizer(context);
        Intent recognizerIntent = new Intent();
        speechRecognizer.setRecognitionListener(new MyRecognitionListener(this));
        speechRecognizer.startListening(recognizerIntent);
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
            if (action.equals(ACTION_START_LISTENING)) {
                handleStartListening();
            } else if (action.equals(ACTION_STOP_LISTENING)) {
                handleStopListening();
            } else if (action.equals(ACTION_START_RECOGNIZING)) {
                handleStartRecognizing();
            } else if (action.equals(ACTION_SHOW_RECOGNITION_RESULT)) {
                handleShowRecognitionResult(intent);
            } else if (action.equals(ACTION_SHOW_REPLY)) {
                handleShowReply(intent);
            }
        }

        private void handleStartListening() {
            statusView.setText(R.string.recognition_listening);
        }

        private void handleStartRecognizing() {
            statusView.setText(R.string.recognition_recognizing);
        }

        private void handleStopListening() {
            statusView.setText(R.string.recognition_idle);
        }

        private void handleShowRecognitionResult(@NonNull Intent intent) {
            String message = intent.getStringExtra(EXTRA_MESSAGE);
            if (message == null) {
                Logger.warning("Received null message");
                return;
            }

            Logger.debug("Received message: %s", message);
            if (fromAssistantButton) {
                finish();
            } else {
                TextView textView = getTextView(message);

                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                params.gravity = Gravity.END;
                params.topMargin = spToPixels(chatLayout.getContext(), 6);
                textView.setLayoutParams(params);
                textView.setBackgroundColor(getResources().getColor(R.color.AdaRequest));
                textView.setTextColor(getResources().getColor(R.color.Black));

                chatLayout.addView(textView);
            }
        }

        private void handleShowReply(@NonNull Intent intent) {
            String message = intent.getStringExtra(EXTRA_MESSAGE);
            if (message == null) {
                Logger.warning("Received null message");
                return;
            }

            Logger.debug("Received message: %s", message);
            if (fromAssistantButton) {
                finish();
            } else {
                TextView textView = getTextView(message);

                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                params.gravity = Gravity.START;
                params.topMargin = spToPixels(chatLayout.getContext(), 6);
                textView.setLayoutParams(params);
                textView.setBackgroundColor(getResources().getColor(R.color.HomeAssistant));
                textView.setTextColor(getResources().getColor(R.color.White));

                chatLayout.addView(textView);
            }
        }

        @NonNull
        private TextView getTextView(String message) {
            TextView textView = new TextView(chatLayout.getContext());
            int padding = spToPixels(chatLayout.getContext(), 6);
            textView.setPadding(padding, padding, padding, padding);
            textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);
            textView.setText(message);
            return textView;
        }

        private int spToPixels(@NonNull Context context, int sp) {
            float scaledDensity = context.getResources().getDisplayMetrics().scaledDensity;
            return (int) (sp * scaledDensity);
        }
    }
}