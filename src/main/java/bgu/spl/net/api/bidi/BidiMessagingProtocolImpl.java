package bgu.spl.net.api.bidi;

import bgu.spl.net.api.bidi.Messages.Message;

public class BidiMessagingProtocolImpl implements BidiMessagingProtocol<Message>{

    private Connections connections;
    private int connectId;



    @Override
    public void start(int connectionId, Connections connections) {
        this.connectId = connectionId;
        this.connections = connections;
    }

    @Override
    public void process(Message message) {

    }

    @Override
    public boolean shouldTerminate() {
        return false;
    }
}
