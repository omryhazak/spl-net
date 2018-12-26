package bgu.spl.net.api.bidi.Messages;

public class LoginMessage implements Message {

    private String userName;
    private String password;
    private int opCode =  2;

    public LoginMessage(String userName, String password) {
        this.userName = userName;
        this.password = password;
    }

    public String getUserName() {
        return userName;
    }

    public String getPassword() {
        return password;
    }

}
