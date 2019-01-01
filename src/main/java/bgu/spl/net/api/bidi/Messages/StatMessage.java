package bgu.spl.net.api.bidi.Messages;

import bgu.spl.net.api.bidi.AllUsers;

public class StatMessage extends Message {

    private String userName;
    private short opCode =  8;

    public StatMessage(String message) {

        String toParse = message;
        this.userName = toParse.substring(2, toParse.indexOf('\0'));

    }

    @Override
    public short getOpCode() {
        return opCode;
    }

    public String getUserName() {
        return userName;
    }

    @Override
    public int[] process(int connectId, AllUsers allUsers) {
        return allUsers.getStatOfUser(userName, connectId);
    }
}
