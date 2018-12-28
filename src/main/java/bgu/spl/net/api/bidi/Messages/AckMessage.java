package bgu.spl.net.api.bidi.Messages;

import java.util.LinkedList;

public class AckMessage {

    //--------------------fields--------------

    private int opCode = 10;
    private int messageOpCode;
    private int numOfUsers = 0;
    private LinkedList<String> userNameList = new LinkedList<>();

    //-------------------constructors for each option----------

    //constructor for Register, Login
    public AckMessage(int messageOpCode) {
        this.messageOpCode = messageOpCode;
    }

    //constructor for Follow
    public AckMessage(int messageOpCode, LinkedList userNameList){
        this.messageOpCode = messageOpCode;
        this.userNameList.addAll(userNameList);
        numOfUsers = userNameList.size();
    }

    //------------------methods---------------
}

