package bgu.spl.net.api.bidi.Messages;

import bgu.spl.net.api.bidi.AllUsers;

import java.util.LinkedList;

public class UserlistMessage extends Message {

    private short opCode =  7;

    public UserlistMessage() {
    }

    @Override
    public short getOpCode() {
        return opCode;
    }

    @Override
    public LinkedList<String> process(int connectId, AllUsers allUsers) {
        return allUsers.returnUserList();
    }
}
