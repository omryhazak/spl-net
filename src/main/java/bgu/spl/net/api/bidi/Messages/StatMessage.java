package bgu.spl.net.api.bidi.Messages;

import bgu.spl.net.api.bidi.AllUsers;

public class StatMessage extends Message {

    private String userName;
    private short opCode =  8;

    public StatMessage(String name) {
        userName = name;
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
