package bgu.spl.net.api.bidi.Messages;

import bgu.spl.net.api.bidi.AllUsers;

public class LogoutMessage extends Message {

    private short opCode =  3;

    public LogoutMessage() {
    }


    @Override
    public short getOpCode() {
        return opCode;
    }

    @Override
    public Boolean process(int connectId, AllUsers allUsers) {
        return allUsers.logOut(connectId);
    }
}
