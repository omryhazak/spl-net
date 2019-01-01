package bgu.spl.net.api.bidi.Messages;

import bgu.spl.net.api.Pair;
import bgu.spl.net.api.bidi.AllUsers;

import java.util.concurrent.ConcurrentLinkedQueue;

public class LoginMessage extends Message {

    private String userName;
    private String password;
    private short opCode =  2;

    public LoginMessage(String userName, String password) {

        this.userName = userName;
        this.password = password;

    }

    @Override
    public short getOpCode() {
        return opCode;
    }

    public String getUserName() {
        return userName;
    }

    public String getPassword() {
        return password;
    }

    @Override
    public ConcurrentLinkedQueue<Pair> process(int connectId, AllUsers allUsers) {
        return allUsers.logInToSystem(userName, password, connectId);
    }
}