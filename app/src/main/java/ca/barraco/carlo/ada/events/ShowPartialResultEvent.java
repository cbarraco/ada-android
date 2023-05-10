package ca.barraco.carlo.ada.events;

public class ShowPartialResultEvent {
    private final String message;

    public ShowPartialResultEvent(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}
