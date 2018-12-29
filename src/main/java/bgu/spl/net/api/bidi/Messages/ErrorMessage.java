package bgu.spl.net.api.bidi.Messages;

import bgu.spl.net.api.bidi.AllUsers;

public class ErrorMessage extends Message {

    private int opCode = 11;
    private int messageOpCode;

    //-------------------constructor----------

    public ErrorMessage(int messageOpCode) {
        this.messageOpCode = messageOpCode;
    }

    //------------------methods---------------


    @Override
    public Object process(int connectId, AllUsers allUsers) {
        return null;
    }
}
