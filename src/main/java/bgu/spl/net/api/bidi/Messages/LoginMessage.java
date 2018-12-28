package bgu.spl.net.api.bidi.Messages;

import bgu.spl.net.api.bidi.AllUsers;

public class LoginMessage extends Message {

    private String userName;
    private String password;
    private int opCode =  2;

    public LoginMessage(String message) {

        String toParse = message;
        this.userName = toParse.substring(2, toParse.indexOf('\0'));
        toParse = toParse.substring(toParse.indexOf('\0'));
        this.password = toParse.substring(0, toParse.length()-1);

    }

    public String getUserName() {
        return userName;
    }

    public String getPassword() {
        return password;
    }

    @Override
    public Boolean process(int connectId, AllUsers allUsers) {
        return allUsers.logInToSystem(userName, password);
    }
}
