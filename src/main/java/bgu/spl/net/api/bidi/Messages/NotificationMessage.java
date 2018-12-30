package bgu.spl.net.api.bidi.Messages;

import bgu.spl.net.api.bidi.AllUsers;

public class NotificationMessage extends Message {

    //--------------fields---------------//

    private String content;
    private short opCode = 9;
    private short answerOpcode;

    //--------------constructor----------//


    public NotificationMessage(String content, short answerOpcode) {

        this.content = content;
        this.answerOpcode = answerOpcode;

    }

    @Override
    public short getOpCode() {
        return opCode;
    }

    @Override
    public Object process(int connectId, AllUsers allUsers) {
        return null;
    }
}
