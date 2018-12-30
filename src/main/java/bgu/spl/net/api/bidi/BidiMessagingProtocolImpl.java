package bgu.spl.net.api.bidi;

import bgu.spl.net.api.Pair;
import bgu.spl.net.api.bidi.Messages.*;

import java.util.LinkedList;
import java.util.concurrent.ConcurrentLinkedQueue;

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

        //if it is Follow or UserList message
        if (message.getClass().equals(FollowMessage.class)){
            LinkedList<String> ans = (LinkedList<String>) message.process(connectId, allUsers);
            if (ans.size() == 0){
                connections.send(connectId, new ErrorMessage(message.getOpCode()));
            }
            else{
                connections.send(connectId, new AckMessage(message.getOpCode(), ans));
            }
        }

        //if it is Login message
        else if (message.getClass().equals(LoginMessage.class)){
            ConcurrentLinkedQueue<Pair> ans = (ConcurrentLinkedQueue<Pair>) message.process(connectId, allUsers);

            //if we cant log in
            if (ans == null){
                connections.send(connectId, new ErrorMessage(message.getOpCode()));
            }
            //if we can log in,
            else {
                connections.send(connectId, new AckMessage(message.getOpCode()));
                while (!ans.isEmpty()) {
                    Pair p = ans.poll();
                    connections.send(connectId, new NotificationMessage(allUsers.findUser(p.getFirst()).getName(), p.getSecond(), message.getOpCode()));
                }
            }

        }

        //if it is Post message
        else if (message.getClass().equals(PostMessage.class)){

            //get the id of the user we need to post to
            LinkedList<Integer> ans = (LinkedList<Integer>)message.process(connectId, allUsers);
            if(ans.size()!=0 && ans.getFirst()==-1){
                connections.send(connectId, new ErrorMessage(message.getOpCode()));
            }
            else {
                connections.send(connectId, new AckMessage(message.getOpCode()));
                for (Integer i : ans) {
                    connections.send(i, new NotificationMessage(allUsers.findUser(connectId).getName(), ((PostMessage) message).getContent(), message.getOpCode()));
                }
            }
        }

        //if it Stat message
        else if (message.getClass().equals(StatMessage.class)){
            int[] numbers  = (int[])message.process(connectId, allUsers);
            if(numbers != null){
                connections.send(connectId, new AckMessage(message.getOpCode(), numbers[0], numbers[1], numbers[2]));
            }else {
                connections.send(connectId, new ErrorMessage(message.getOpCode()));
            }
        }

        //if it is PM message
        else if (message.getClass().equals(PmMessage.class)){
            int ans = (int)message.process(connectId, allUsers);
            if (ans != -1){
                connections.send(connectId, new AckMessage(message.getOpCode()));
                connections.send(ans, ((PmMessage) message).getContent());
            }
            else{
                connections.send(connectId, new ErrorMessage(message.getOpCode()));
            }

        }

        //if it is Register or Logut message
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
