package bgu.spl.net.api.bidi;

public class BidiMessagingProtocolImpl<T> implements BidiMessagingProtocol<T>{

    private Connections<T> connections;
    private int connectId;



    @Override
    public void start(int connectionId, Connections connections) {
        this.connectId = connectionId;
        this.connections = connections;
    }

    @Override
    public void process(T message) {
        int firstChar = ((String) message).charAt(0);
    }

    @Override
    public boolean shouldTerminate() {
        return false;
    }
}
