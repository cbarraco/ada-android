package ca.barraco.carlo.ada.events;

public class ShowErrorEvent {
    private final String message;

    public ShowErrorEvent(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}
