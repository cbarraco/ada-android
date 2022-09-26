package ca.barraco.carlo.rhasspy.recognition;

import android.content.Context;
import android.os.Bundle;
import android.speech.RecognitionListener;
import android.speech.SpeechRecognizer;

import androidx.annotation.NonNull;

import java.util.ArrayList;

import ca.barraco.carlo.rhasspy.Actions;
import ca.barraco.carlo.rhasspy.Logger;

public class MyRecognitionListener implements RecognitionListener {

    private final Context context;

    public MyRecognitionListener(Context context) {
        this.context = context;
    }

    @Override
    public void onReadyForSpeech(Bundle bundle) {
        Actions.startListening();
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
        Actions.showError("I didn't hear anything");
    }

    @Override
    public void onResults(@NonNull Bundle bundle) {
        ArrayList<String> recognitionResults = bundle.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
        String speechRecognitionResult = recognitionResults.get(0);
        String capitalizedResult = capitalizeFirstWord(speechRecognitionResult);

        // add question mark if the result is a question
        String[] questionWords = {"who", "what", "when", "where", "why", "how"};
        for (String questionWord : questionWords) {
            if (capitalizedResult.toLowerCase().startsWith(questionWord)) {
                capitalizedResult += "?";
                break;
            }
        }

        Actions.showRecognitionResult(capitalizedResult);
        Logger.information("Recognized speech: %s", speechRecognitionResult);
    }

    @Override
    public void onPartialResults(@NonNull Bundle bundle) {
        ArrayList<String> data = bundle.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
        String partialResult = data.get(data.size() - 1);
        if (!partialResult.isEmpty()) {
            String capitalizedPartialResult = capitalizeFirstWord(partialResult);
            Actions.showPartialResult(capitalizedPartialResult);
        }
    }

    @NonNull
    private String capitalizeFirstWord(@NonNull String partialResult) {
        return partialResult.substring(0, 1).toUpperCase() + partialResult.substring(1);
    }

    @Override
    public void onEvent(int i, Bundle bundle) {
        // not used
    }
}
