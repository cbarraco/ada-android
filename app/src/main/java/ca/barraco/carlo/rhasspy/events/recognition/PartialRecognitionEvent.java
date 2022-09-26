package ca.barraco.carlo.rhasspy.events.recognition;

public class PartialRecognitionEvent {
    private final String result;

    public PartialRecognitionEvent(String result) {
        this.result = result;
    }

    public String getResult() {
        return result;
    }
}
