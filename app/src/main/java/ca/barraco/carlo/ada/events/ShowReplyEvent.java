package ca.barraco.carlo.ada.events;

public class ShowReplyEvent {
    private final String reply;

    public ShowReplyEvent(String reply) {
        this.reply = reply;
    }

    public String getReply() {
        return reply;
    }
}
