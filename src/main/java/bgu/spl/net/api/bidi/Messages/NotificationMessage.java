package bgu.spl.net.api.bidi.Messages;

import bgu.spl.net.api.bidi.AllUsers;

public class NotificationMessage extends Message {

    //--------------fields---------------//

    private String sentBy;
    private String content;
    private short opCode = 9;
    private short answerOpcode;

    //--------------constructor----------//


    public NotificationMessage(String sentBy, String content, short answerOpcode) {

        this.sentBy = sentBy;
        this.content = content;
        this.answerOpcode = answerOpcode;

    }

    public String getContent() {
        return content;
    }

    @Override
    public short getOpCode() {
        return opCode;
    }

    public short getAnswerOpcode() {
        return answerOpcode;
    }

    public String getSentBy() {
        return sentBy;
    }

    @Override
    public Object process(int connectId, AllUsers allUsers) {
        return null;
    }
}
