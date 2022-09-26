package ca.barraco.carlo.rhasspy;

import org.greenrobot.eventbus.EventBus;

import ca.barraco.carlo.rhasspy.events.recognition.RecognitionErrorEvent;
import ca.barraco.carlo.rhasspy.events.recognition.PartialRecognitionEvent;
import ca.barraco.carlo.rhasspy.events.recognition.SuccessfulRecognitionEvent;
import ca.barraco.carlo.rhasspy.events.recognition.ShowReplyEvent;
import ca.barraco.carlo.rhasspy.events.recognition.StartListeningEvent;

public class Actions {

    private Actions() {
        // hidden constructor
    }

    public static void startListening() {
        EventBus.getDefault().post(new StartListeningEvent());
    }

    public static void showRecognitionResult(String reply) {
        EventBus.getDefault().post(new SuccessfulRecognitionEvent(reply));
    }

    public static void showReply(String reply) {
        EventBus.getDefault().post(new ShowReplyEvent(reply));
    }

    public static void showError(String message) {
        EventBus.getDefault().post(new RecognitionErrorEvent(message));
    }

    public static void showPartialResult(String reply) {
        EventBus.getDefault().post(new PartialRecognitionEvent(reply));
    }
}
