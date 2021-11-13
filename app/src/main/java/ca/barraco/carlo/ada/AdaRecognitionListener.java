package ca.barraco.carlo.ada;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.speech.RecognitionListener;
import android.speech.SpeechRecognizer;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.preference.PreferenceManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;

public class AdaRecognitionListener implements RecognitionListener {

    private final Context context;

    public AdaRecognitionListener(Context context) {
        this.context = context;
    }

    @Override
    public void onReadyForSpeech(Bundle bundle) {
        AdaActions.startListening(context);
    }

    @Override
    public void onBeginningOfSpeech() {
        // not used
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
        // not used
    }

    @Override
    public void onError(int i) {
        Logger.warning("Error encountered during recognition");
        AdaActions.showError(context);
    }

    @Override
    public void onResults(Bundle bundle) {
        ArrayList<String> recognitionResults = bundle.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
        String speechRecognitionResult = recognitionResults.get(0);
        sendConversationText(speechRecognitionResult);
        AdaActions.showRecognitionResult(context, speechRecognitionResult);
        Logger.information("Recognized speech: " + speechRecognitionResult);
    }

    private void sendConversationText(String voiceRecognitionResult) {
        try {
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
        String webServerAddressKey = context.getString(R.string.home_assistant_url_key);
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        return sharedPreferences.getString(webServerAddressKey, "");
    }

    private String getHomeAssistantTokenFromPreferences() {
        String homeAssistantTokenKey = context.getString(R.string.home_assistant_access_token_key);
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        return sharedPreferences.getString(homeAssistantTokenKey, "");
    }

    @Override
    public void onPartialResults(Bundle bundle) {
        ArrayList<String> data = bundle.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
        String word = data.get(data.size() - 1);
        if (!word.isEmpty()) {
            AdaActions.showPartialResult(context, word);
        }
    }

    @Override
    public void onEvent(int i, Bundle bundle) {
        // not used
    }

    private class MyCallback implements Callback {
        @Override
        public void onFailure(@NonNull Call call, @NonNull IOException e) {
            // TODO implement failure handling for HTTP call
            // not used yet
        }

        @Override
        public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
            String reply = parseResponse(response);
            Logger.debug("Got " + response.code() + " reply from Home Assistant: " + reply);

            AdaActions.showReply(context, reply);
        }

        @Nullable
        private String parseResponse(@NonNull Response response) throws IOException {
            try {
                ResponseBody responseBody = response.body();
                if (responseBody != null) {
                    String responseJsonBody = responseBody.string();
                    Logger.debug(responseJsonBody);
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
