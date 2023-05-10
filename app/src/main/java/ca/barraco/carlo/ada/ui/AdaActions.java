package ca.barraco.carlo.ada.ui;

import org.greenrobot.eventbus.EventBus;

import ca.barraco.carlo.ada.events.ShowErrorEvent;
import ca.barraco.carlo.ada.events.ShowPartialResultEvent;
import ca.barraco.carlo.ada.events.ShowRecognitionEvent;
import ca.barraco.carlo.ada.events.ShowReplyEvent;
import ca.barraco.carlo.ada.events.StartListeningEvent;

public class AdaActions {

    private AdaActions() {
        // hidden constructor
    }

    public static void startListening() {
        EventBus.getDefault().post(new StartListeningEvent());
    }

    public static void showRecognitionResult(String reply) {
        EventBus.getDefault().post(new ShowRecognitionEvent(reply));
    }

    public static void showReply(String reply) {
        EventBus.getDefault().post(new ShowReplyEvent(reply));
    }

    public static void showError(String message) {
        EventBus.getDefault().post(new ShowErrorEvent(message));
    }

    public static void showPartialResult(String reply) {
        EventBus.getDefault().post(new ShowPartialResultEvent(reply));
    }
}
