package bgu.spl.net.api.bidi.Messages;

public class StatMessage implements Message {

    private String userName;

    public StatMessage(String userName) {
        this.userName = userName;
    }

    public String getUserName() {
        return userName;
    }
}
