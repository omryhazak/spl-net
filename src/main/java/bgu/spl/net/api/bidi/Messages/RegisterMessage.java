package bgu.spl.net.api.bidi.Messages;

public class RegisterMessage implements Message {

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
}
