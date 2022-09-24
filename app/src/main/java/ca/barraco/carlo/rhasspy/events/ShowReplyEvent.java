package ca.barraco.carlo.rhasspy.events;

public class ShowReplyEvent {
    private final String reply;

    public ShowReplyEvent(String reply) {
        this.reply = reply;
    }

    public String getReply() {
        return reply;
    }
}
