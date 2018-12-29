package bgu.spl.net.api.bidi.Messages;

import bgu.spl.net.api.bidi.AllUsers;

public class StatMessage extends Message {

    private String userName;
    private int opCode =  8;

    public StatMessage(String message) {

        String toParse = message;
        this.userName = toParse.substring(2, toParse.indexOf('\0'));

    }

    public String getUserName() {
        return userName;
    }

    @Override
    public int[] process(int connectId, AllUsers allUsers) {
        return allUsers.getStatOfUser(userName, connectId);
    }
}
