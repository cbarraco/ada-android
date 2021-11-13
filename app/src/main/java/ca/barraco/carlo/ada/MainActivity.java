package ca.barraco.carlo.ada;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import ca.barraco.carlo.ada.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {

    private static final String ACTION_START_LISTENING = "MainActivity.ACTION_START_LISTENING";
    private static final String ACTION_SHOW_RECOGNITION_RESULT = "MainActivity.ACTION_SHOW_RECOGNITION_RESULT";
    private static final String ACTION_SHOW_REPLY = "MainActivity.ACTION_SHOW_REPLY";
    private static final String ACTION_SHOW_ERROR = "MainActivity.ACTION_SHOW_ERROR";
    private static final String ACTION_SHOW_PARTIAL_RESULT = "MainActivity.ACTION_SHOW_PARTIAL_RESULT";
    private static final String EXTRA_MESSAGE = "MainActivity.EXTRA_MESSAGE";
    private static final String[] Actions = {
            ACTION_START_LISTENING,
            ACTION_SHOW_RECOGNITION_RESULT,
            ACTION_SHOW_REPLY,
            ACTION_SHOW_ERROR,
            ACTION_SHOW_PARTIAL_RESULT
    };
    private boolean fromAssistantButton;
    private LinearLayout chatLayout;
    private MyRecognitionListener myRecognitionListener;
    private MainActivityBroadcastReceiver mainActivityBroadcastReceiver;
    private TextView currentRequestTextView;

    public static void startListening(Context context) {
        LocalBroadcastManager localBroadcastManager = LocalBroadcastManager.getInstance(context);
        Intent intent = new Intent(ACTION_START_LISTENING);
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

    public static void showError(Context context) {
        LocalBroadcastManager localBroadcastManager = LocalBroadcastManager.getInstance(context);
        Intent intent = new Intent(ACTION_SHOW_ERROR);
        localBroadcastManager.sendBroadcast(intent);
    }

    public static void showPartialResult(Context context, String reply) {
        LocalBroadcastManager localBroadcastManager = LocalBroadcastManager.getInstance(context);
        Intent intent = new Intent(ACTION_SHOW_PARTIAL_RESULT);
        intent.putExtra(EXTRA_MESSAGE, reply);
        localBroadcastManager.sendBroadcast(intent);
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
            setTheme(R.style.Theme_AppCompat_DayNight_Dialog);
        }

        ActivityMainBinding binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        handleAssistantButton();

        chatLayout = findViewById(R.id.chatLayout);

        binding.fab.setOnClickListener(view -> startListening());

        setUpBroadcastReceiver();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        LocalBroadcastManager localBroadcastManager = LocalBroadcastManager.getInstance(this);
        localBroadcastManager.unregisterReceiver(mainActivityBroadcastReceiver);
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
                setTheme(R.style.Theme_AppCompat_DayNight_Dialog);
                startListening();
            }
        } catch (Exception exception) {
            Logger.error("Error handling assistant button", exception);
        }
    }

    private void startListening() {
        // TODO move listening and processing logic to service
        Logger.information("starting speech recognition");

        int selfPermission = ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO);
        if (selfPermission == PackageManager.PERMISSION_DENIED) {
            String[] permissions = new String[]{Manifest.permission.RECORD_AUDIO};
            requestPermissions(permissions, 1);
        }

        Context context = getApplicationContext();
        SpeechRecognizer speechRecognizer = SpeechRecognizer.createSpeechRecognizer(context);
        Intent recognizerIntent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        recognizerIntent.putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS, true);
        myRecognitionListener = new MyRecognitionListener(context);
        speechRecognizer.setRecognitionListener(myRecognitionListener);
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
            } else if (action.equals(ACTION_SHOW_RECOGNITION_RESULT)) {
                handleShowRecognitionResult(intent);
            } else if (action.equals(ACTION_SHOW_REPLY)) {
                handleShowReply(intent);
            } else if (action.equals(ACTION_SHOW_ERROR)) {
                handleShowError();
            } else if (action.equals(ACTION_SHOW_PARTIAL_RESULT)) {
                handleShowPartialResult(intent);
            }
        }

        private void handleStartListening() {
            currentRequestTextView = createTextView("...");

            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            params.gravity = Gravity.END;
            params.topMargin = spToPixels(chatLayout.getContext(), 6);
            currentRequestTextView.setLayoutParams(params);
            currentRequestTextView.setBackgroundColor(getResources().getColor(R.color.AdaRequest));
            currentRequestTextView.setTextColor(getResources().getColor(R.color.Black));

            chatLayout.addView(currentRequestTextView);
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
                currentRequestTextView.setText(message);
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
                TextView replyTextView = createTextView(message);

                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                params.gravity = Gravity.START;
                params.topMargin = spToPixels(replyTextView.getContext(), 6);
                replyTextView.setLayoutParams(params);
                replyTextView.setBackgroundColor(getResources().getColor(R.color.HomeAssistant));
                replyTextView.setTextColor(getResources().getColor(R.color.White));
                replyTextView.setFocusable(true);
                replyTextView.setFocusableInTouchMode(true);
                chatLayout.addView(replyTextView);
                replyTextView.requestFocus();
            }
        }

        private void handleShowError() {
            if (fromAssistantButton) {
                finish();
            } else {
                currentRequestTextView.setText("<Home Assistant did not hear anything>");
                currentRequestTextView.setBackgroundColor(getResources().getColor(R.color.RecognitionFailure));
                currentRequestTextView.setTextColor(getResources().getColor(R.color.White));
            }
        }

        private void handleShowPartialResult(Intent intent) {
            String message = intent.getStringExtra(EXTRA_MESSAGE);
            if (message == null) {
                Logger.warning("Received null message");
                return;
            }

            Logger.debug("Received partial results: %s", message);
            currentRequestTextView.setText(message);
        }

        @NonNull
        private TextView createTextView(String message) {
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