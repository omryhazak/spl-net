package bgu.spl.net.api.bidi.Messages;

public class PmMessage implements Message {

    private String toSend;
    private String content;

    public PmMessage(String toSend, String content) {
        this.toSend = toSend;
        this.content = content;
    }

    public String getToSend() {
        return toSend;
    }

    public String getContent() {
        return content;
    }
}
