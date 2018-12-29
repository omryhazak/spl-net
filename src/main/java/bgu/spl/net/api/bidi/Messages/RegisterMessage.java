package bgu.spl.net.api.bidi.Messages;

import bgu.spl.net.api.bidi.AllUsers;
import bgu.spl.net.srv.User;

public class RegisterMessage extends Message {

    private String userName;
    private String password;

    public RegisterMessage(String userName, String password) {

        this.userName = userName;
        this.password = password;

    }

    public String getUserName() {
        return userName;
    }

    public String getPassword() {
        return password;
    }

    @Override
    public Boolean process(int connectId, AllUsers allUsers){
        //initializing the user object we will use later in the data base AllUsers
        User user = new User(connectId, userName, password);

        //trying to register user to system
        return(allUsers.registerToSystem(user));
    }
}