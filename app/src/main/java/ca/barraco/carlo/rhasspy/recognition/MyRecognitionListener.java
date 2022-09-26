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

        Logger.information("Speech recognition result: " + speechRecognitionResult);
        String processedResult = capitalizeFirstWord(speechRecognitionResult);
        processedResult = addQuestionMark(processedResult);
        Actions.showRecognitionResult(processedResult);

        Logger.debug("Processed recognition result: " + processedResult);
    }

    @NonNull
    private static String addQuestionMark(String capitalizedResult) {
        String[] questionWords = {
                "who", "what", "when", "where", "why", "how", "is", "are", "do", "does", "did", "can", "could", "will", "would", "shall", "should", "may", "might", "must"
        };
        for (String questionWord : questionWords) {
            if (capitalizedResult.toLowerCase().startsWith(questionWord)) {
                return capitalizedResult + "?";
            }
        }
        return capitalizedResult;
    }

    @Override
    public void onPartialResults(@NonNull Bundle bundle) {
        ArrayList<String> data = bundle.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
        String partialResult = data.get(data.size() - 1);
        if (partialResult.isEmpty()) {
            Logger.warning("Partial result is empty");
            return;
        }
        String capitalizedPartialResult = capitalizeFirstWord(partialResult);
        Logger.debug("Partial result: " + capitalizedPartialResult);
        Actions.showPartialResult(capitalizedPartialResult);
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
