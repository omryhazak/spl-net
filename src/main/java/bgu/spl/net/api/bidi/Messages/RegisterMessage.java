package bgu.spl.net.api.bidi.Messages;

public class RegisterMessage implements Message {

    private String userName;
    private String password;
    private int opCode =  1;

    public RegisterMessage(String message) {

        String toParse = message;                                              //getting the complete message as string
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
}
