package bgu.spl.net.api.bidi.Messages;

import java.util.LinkedList;

public class PostMessage implements Message {

    private String content;
    private LinkedList<String> usersNametoSend;
    private int opCode =  5;

    public PostMessage(String content, LinkedList<String> usersNametoSend) {
        this.content = content;
        this.usersNametoSend = usersNametoSend;
    }

    public String getContent() {
        return content;
    }

    public LinkedList<String> getUsersNametoSend() {
        return usersNametoSend;
    }
}
