package ca.barraco.carlo.rhasspy.events.recognition;

public class SuccessfulRecognitionEvent {
    private final String result;

    public SuccessfulRecognitionEvent(String result) {
        this.result = result;
    }

    public String getResult() {
        return result;
    }
}
