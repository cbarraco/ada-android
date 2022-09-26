package ca.barraco.carlo.rhasspy.events.recognition;

public class RecognitionErrorEvent {
    private final String message;

    public RecognitionErrorEvent(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}
