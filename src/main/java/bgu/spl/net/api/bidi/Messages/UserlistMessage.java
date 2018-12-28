package bgu.spl.net.api.bidi.Messages;

import bgu.spl.net.api.bidi.AllUsers;

import java.util.LinkedList;

public class UserlistMessage extends Message {

    private int opCode =  7;

    public UserlistMessage() {
    }

    @Override
    public LinkedList<String> process(int connectId, AllUsers allUsers) {
        return allUsers.returnUserList();
    }
}
