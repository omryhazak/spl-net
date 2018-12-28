package bgu.spl.net.api.bidi.Messages;
import bgu.spl.net.api.bidi.AllUsers;

public abstract class Message {

    //--------------------fields---------------
    int opCode;

    //-------------------methods---------------
    public abstract Object process(int connectId, AllUsers allUsers);

    public int getOpCode() {
        return opCode;
    }
}
