package bgu.spl.net.api.bidi.Messages;

public class ErrorMessage {

    private int opCode = 11;
    private int messageOpCode;

    //-------------------constructor----------

    public ErrorMessage(int messageOpCode) {
        this.messageOpCode = messageOpCode;
    }

    //------------------methods---------------
}
