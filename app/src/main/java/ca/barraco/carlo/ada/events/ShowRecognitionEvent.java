package ca.barraco.carlo.ada.events;

public class ShowRecognitionEvent {
    private final String message;

    public ShowRecognitionEvent(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}
