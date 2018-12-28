package bgu.spl.net.api.bidi.Messages;

import java.util.LinkedList;

public class AckMessage {

    //--------------------fields--------------

    private int opCode = 10;
    private int messageOpCode;
    private LinkedList<String> userNameList;

    //-------------------constructors for each option----------

    public AckMessage(int messageOpCode) {
        this.messageOpCode = messageOpCode;
    }

    //------------------methods---------------
}

