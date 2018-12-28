package bgu.spl.net.api.bidi.Messages;

import bgu.spl.net.api.bidi.AllUsers;
import bgu.spl.net.srv.User;

public class RegisterMessage extends Message {

    private String userName;
    private String password;

    public RegisterMessage(String message) {

        String toParse = message;                                              //getting the complete message as string
        this.userName = toParse.substring(2, toParse.indexOf('\0'));
        toParse = toParse.substring(toParse.indexOf('\0'));
        this.password = toParse.substring(0, toParse.length()-1);
        super.opCode = 1;

    }

    public String getUserName() {
        return userName;
    }

    public String getPassword() {
        return password;
    }

    @Override
    public boolean process(int connectId, AllUsers allUsers){
        //initializing the user object we will use later in the data base AllUsers
        User user = new User(connectId, userName, password);

        //trying to register user to system
        return(allUsers.registerToSystem(user));
    }
}
