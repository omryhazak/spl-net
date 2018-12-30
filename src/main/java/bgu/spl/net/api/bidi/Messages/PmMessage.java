package bgu.spl.net.api.bidi.Messages;

import bgu.spl.net.api.bidi.AllUsers;

public class PmMessage extends Message {

    private String toSend;
    private String content;
    private int opCode =  6;

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

    @Override
    public Integer process(int connectId, AllUsers allUsers) {
        return allUsers.sendPM(connectId, toSend);
    }
}
