package bgu.spl.net.api.bidi.Messages;

public class StatMessage implements Message {

    private String userName;
    private int opCode =  8;

    public StatMessage(String message) {

        String toParse = message;
        this.userName = toParse.substring(2, toParse.indexOf('\0'));

    }

    public String getUserName() {
        return userName;
    }
}
