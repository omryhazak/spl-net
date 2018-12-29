package bgu.spl.net.api.bidi.Messages;

import bgu.spl.net.api.bidi.AllUsers;

public class PmMessage extends Message {

    private String toSend;
    private String content;
    private int opCode =  6;

    public PmMessage(String message) {

        String toParse = message;
        toParse = toParse.substring(2);
        this.toSend = toParse.substring(0, toParse.indexOf('\0'));
        toParse = toParse.substring(0, toParse.indexOf('\0')+1);
        this.content = toParse.substring(0, toParse.indexOf('\0'));
    }

    public String getToSend() {
        return toSend;
    }

    public String getContent() {
        return content;
    }

    @Override
    public Integer process(int connectId, AllUsers allUsers) {
        return allUsers.sendPM(connectId, toSend);
    }
}
