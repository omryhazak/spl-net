package bgu.spl.net.api.bidi.Messages;

import bgu.spl.net.api.bidi.AllUsers;

public class LogoutMessage extends Message {

    private int opCode =  3;

    public LogoutMessage() {
    }

    @Override
    public boolean process(int connectId, AllUsers allUsers) {
        return allUsers.logOut(connectId);
    }
}
