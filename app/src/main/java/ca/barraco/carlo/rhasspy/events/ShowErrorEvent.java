package ca.barraco.carlo.rhasspy.events;

public class ShowErrorEvent {
    private final String message;

    public ShowErrorEvent(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}
