package bgu.spl.net.api.bidi.Messages;
import bgu.spl.net.api.bidi.AllUsers;

public abstract class Message {

    //--------------------fields---------------
    short opCode;

    //-------------------methods---------------
    public abstract Object process(int connectId, AllUsers allUsers);

    public short getOpCode() {
        return opCode;
    }
}
