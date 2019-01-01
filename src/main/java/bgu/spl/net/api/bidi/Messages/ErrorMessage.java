package bgu.spl.net.api.bidi.Messages;

import bgu.spl.net.api.bidi.AllUsers;

public class ErrorMessage extends Message {

    private short opCode = 11;
    private short messageOpCode;

    //-------------------constructor----------

    public ErrorMessage(short messageOpCode) {
        this.messageOpCode = messageOpCode;
    }

    //------------------methods---------------


    @Override
    public short getOpCode() {
        return opCode;
    }

    public short getMessageOpCode() {
        return messageOpCode;
    }

    @Override
    public Object process(int connectId, AllUsers allUsers) {
        return null;
    }
}
