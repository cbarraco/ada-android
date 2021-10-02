package ca.barraco.carlo.ada;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.speech.RecognitionListener;
import android.speech.SpeechRecognizer;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.PreferenceManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

import ca.barraco.carlo.ada.databinding.ActivityMainBinding;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;

public class MainActivity extends AppCompatActivity {

    private TextView statusView;
    private boolean fromAssistantButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ActivityMainBinding binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.toolbar);

        handleAssistantButton();

        statusView = findViewById(R.id.statusView);

        binding.fab.setOnClickListener(view -> startListening());
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
        speechRecognizer.setRecognitionListener(new MyRecognitionListener());
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


    private class MyRecognitionListener implements RecognitionListener {

        @Override
        public void onReadyForSpeech(Bundle bundle) {
            statusView.setText(R.string.recognition_listening);
        }

        @Override
        public void onBeginningOfSpeech() {
            statusView.setText(R.string.recognition_recognizing);
        }

        @Override
        public void onRmsChanged(float v) {
            // not used
        }

        @Override
        public void onBufferReceived(byte[] bytes) {
            // not used
        }

        @Override
        public void onEndOfSpeech() {
            statusView.setText(R.string.recognition_idle);
        }

        @Override
        public void onError(int i) {
            // not used
        }

        @Override
        public void onResults(Bundle bundle) {
            ArrayList<String> recognitionResults = bundle.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
            String speechRecognitionResult = recognitionResults.get(0);
            sendConversationText(speechRecognitionResult);
            Logger.information("Recognized speech: " + speechRecognitionResult);
        }

        private void sendConversationText(String voiceRecognitionResult) {
            try {
                // TODO implement optional HTTPS cert validation
                Request request = buildRequest(voiceRecognitionResult);
                OkHttpClient okHttpClient = new OkHttpClient();
                Call call = okHttpClient.newCall(request);
                Callback callback = new MyCallback();
                call.enqueue(callback);
            } catch (Exception e) {
                Logger.error("Error sending conversation", e);
            }
        }

        @NonNull
        private Request buildRequest(String voiceRecognitionResult) {
            // TODO validate url
            String homeAssistantUrl = getServerAddressFromPreferences() + "/api/conversation/process";

            String authorizationHeaderName = "Authorization";
            String homeAssistantToken = getHomeAssistantTokenFromPreferences();
            String authorizationHeaderValue = "Bearer " + homeAssistantToken;

            MediaType requestMediaType = MediaType.get("application/json; charset=utf-8");
            String requestJsonBody = "{\"text\":\"" + voiceRecognitionResult + "\", \"conversation_id\":\"ada\"}";
            RequestBody requestBody = RequestBody.create(requestJsonBody, requestMediaType);

            return new Request.Builder()
                    .url(homeAssistantUrl)
                    .header(authorizationHeaderName, authorizationHeaderValue)
                    .post(requestBody)
                    .build();
        }

        private String getServerAddressFromPreferences() {
            Context context = getApplicationContext();
            String webServerAddressKey = context.getString(R.string.home_assistant_url_key);
            SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
            return sharedPreferences.getString(webServerAddressKey, "");
        }

        private String getHomeAssistantTokenFromPreferences() {
            Context context = getApplicationContext();
            String homeAssistantTokenKey = context.getString(R.string.home_assistant_access_token_key);
            SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
            return sharedPreferences.getString(homeAssistantTokenKey, "");
        }

        @Override
        public void onPartialResults(Bundle bundle) {
            // not used
        }

        @Override
        public void onEvent(int i, Bundle bundle) {
            // not used
        }
    }

    private class MyCallback implements Callback {
        @Override
        public void onFailure(@NonNull Call call, @NonNull IOException e) {
            // TODO implement failure handling for HTTP call
            // not used yet
        }

        @Override
        public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
            // TODO show response to user
            String reply = parseResponse(response);
            Logger.debug("Got reply from Home Assistant: " + reply);

            // exit immediately if triggered from assistant button
            if (fromAssistantButton) {
                finish();
            }
        }

        @Nullable
        private String parseResponse(@NonNull Response response) throws IOException {
            try {
                ResponseBody responseBody = response.body();
                if (responseBody != null) {
                    String responseJsonBody = responseBody.string();
                    JSONObject responseJsonObject = new JSONObject(responseJsonBody);
                    return responseJsonObject.getJSONObject("speech").getJSONObject("plain").getString("speech");
                }
                return null;
            } catch (JSONException exception) {
                Logger.error("Failed to parse response from Home Assistant", exception);
                return null;
            }
        }
    }
}