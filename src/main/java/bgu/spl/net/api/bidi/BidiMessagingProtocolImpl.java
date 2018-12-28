package bgu.spl.net.api.bidi;

import bgu.spl.net.api.bidi.Messages.*;
import bgu.spl.net.srv.User;

import java.util.LinkedList;

public class BidiMessagingProtocolImpl implements BidiMessagingProtocol<Message>{

    private Connections connections;
    private int connectId;

    //**********where is this shit coming from???***************
    private AllUsers allUsers;



    @Override
    public void start(int connectionId, Connections connections) {
        this.connectId = connectionId;
        this.connections = connections;
    }

    @Override
    public void process(Message message) {

        //if it is follow message
        if (message.getClass().equals(FollowMessage.class)){
            LinkedList<String> ans = ((FollowMessage)message).process(connectId, allUsers);
            if (ans.size() == 0){
                connections.send(connectId, new ErrorMessage(message.getOpCode()));
            }
            else{
                connections.send(connectId, new AckMessage(message.getOpCode(), ans));
            }

        }
        else {
            boolean succeed = (boolean)message.process(connectId, allUsers);
            if (succeed) {
                connections.send(connectId, new AckMessage(message.getOpCode()));
            } else {
                connections.send(connectId, new ErrorMessage(message.getOpCode()));
            }
        }
    }

    @Override
    public boolean shouldTerminate() {
        return false;
    }
}
