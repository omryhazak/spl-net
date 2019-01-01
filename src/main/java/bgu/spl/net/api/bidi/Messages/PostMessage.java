package bgu.spl.net.api.bidi.Messages;

import bgu.spl.net.api.bidi.AllUsers;

import java.util.LinkedList;

public class PostMessage extends Message {

    private String content;
    private LinkedList<String> usersNameToSend;
    private short opCode =  5;

    public PostMessage(String content, LinkedList<String> usersNameToSend) {

        this.content = content;
        this.usersNameToSend = usersNameToSend;

    }

    @Override
    public short getOpCode() {
        return opCode;
    }

    public String getContent() {
        return content;
    }

    public LinkedList<String> getUsersNameToSend() {
        return usersNameToSend;
    }

    @Override
    public LinkedList<Integer> process(int connectId, AllUsers allUsers) {
        return allUsers.postMessage(connectId, content, usersNameToSend);
    }
}
