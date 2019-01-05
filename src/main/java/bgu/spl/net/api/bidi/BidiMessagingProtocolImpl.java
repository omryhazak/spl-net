package bgu.spl.net.api.bidi;

import bgu.spl.net.api.objectOfThree;
import bgu.spl.net.api.bidi.Messages.*;
import java.util.LinkedList;
import java.util.concurrent.ConcurrentLinkedQueue;

public class BidiMessagingProtocolImpl implements BidiMessagingProtocol<Message>{

    private Connections connections;
    private int connectId;
    private AllUsers allUsers;
    boolean toTerminate;

    public BidiMessagingProtocolImpl(AllUsers allUsers) {
        this.allUsers = allUsers;
    }

    // we need to write a constructor in order to pass the shared object with it

    @Override
    public void start(int connectionId, Connections connections) {
        this.connectId = connectionId;
        this.connections = connections;
        this.toTerminate = false;
    }

    @Override
    public void process(Message message) {

        //if it is Follow or UserList message
        if (message.getClass().equals(FollowMessage.class) || message.getClass().equals(UserlistMessage.class)){

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

            ConcurrentLinkedQueue<objectOfThree> ans = (ConcurrentLinkedQueue<objectOfThree>) message.process(connectId, allUsers);

            if(ans.size() == 1) {
                objectOfThree tmp = ans.poll();

                //if we dont have user with that name
                if (tmp.getFirst() == -1) {
                    connections.send(connectId, new ErrorMessage(message.getOpCode()));
                }

                //if we have user with that name whom we locked, but we dont have the correct password or he is already logged in
                else if (tmp.getFirst() == -2) {
                    connections.send(connectId, new ErrorMessage(message.getOpCode()));


                    allUsers.getUserById(connectId);

                    String name = ((LoginMessage) message).getUserName();

                    allUsers.findUser(name).getLock().writeLock().unlock();
                }

                //if we have user with that name whom we locked, and the password is correct but there are no messages waiting
                else if (tmp.getFirst() == -3) {
                    connections.send(connectId, new AckMessage(message.getOpCode()));
                    allUsers.getUserById(connectId).getLock().writeLock().unlock();
                }

                //if we did login and only one message is waiting for us
                else{
                    connections.send(connectId, new AckMessage(message.getOpCode()));
                    connections.send(connectId, new NotificationMessage(allUsers.getUserById(tmp.getFirst()).getName(), tmp.getSecond(), (short)tmp.getThird()));
                    allUsers.getUserById(connectId).getLock().writeLock().unlock();
                }

                //if we can log in, and we have more than one message
            }  else {
                connections.send(connectId, new AckMessage(message.getOpCode()));

                while (!ans.isEmpty()) {
                    objectOfThree p = ans.poll();

                    connections.send(connectId, new NotificationMessage(allUsers.getUserById(p.getFirst()).getName(), p.getSecond(), (short)p.getThird()));

                }

                //finished working on user, now we can release him for changes
                allUsers.getUserById(connectId).getLock().writeLock().unlock();
            }

        }

        //if it is Post message
        else if (message.getClass().equals(PostMessage.class)){

            //get the id of the user we need to post to
            LinkedList<Integer> ans = (LinkedList<Integer>)message.process(connectId, allUsers);

            if(ans.size()!=0 && ans.getFirst()==-1){
                connections.send(connectId, new ErrorMessage(message.getOpCode()));

                //finished checking users followers, we can release him for changes
                allUsers.getUserById(connectId).getLock().readLock().unlock();

            }
            else {
                for (Integer i : ans) {
                    connections.send(i, new NotificationMessage(allUsers.getUserById(connectId).getName(), ((PostMessage) message).getContent(), message.getOpCode()));

                    //finished using user, we can release him for changes
                    allUsers.getUserById(i).getLock().readLock().unlock();
                }
                connections.send(connectId, new AckMessage(message.getOpCode()));

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

            int ans = ((PmMessage)message).process(connectId, allUsers);
            if (ans == -1){
                connections.send(connectId, new ErrorMessage(message.getOpCode()));
            }
            else if(ans == -2){
               connections.send(connectId, new AckMessage(message.getOpCode()));
            }
            else{
                connections.send(ans, new NotificationMessage(allUsers.getUserById(connectId).getName(), ((PmMessage) message).getContent(), (short) 0));

                //we sent the message to user, now we can unlock him
                allUsers.getUserById(ans).getLock().readLock().unlock();

                connections.send(connectId, new AckMessage(message.getOpCode()));
            }

        }

        //if it is Register message
        else if (message.getClass().equals(RegisterMessage.class)){

            boolean succeed = (boolean)message.process(connectId, allUsers);
            if (succeed) {
                connections.send(connectId, new AckMessage(message.getOpCode()));

                //if it is logout, kill the connection handler
                if(message.getClass()==LogoutMessage.class)
                    toTerminate = true;
            } else {
                connections.send(connectId, new ErrorMessage(message.getOpCode()));
            }
        }

        //if it is Logout message
        else{
            boolean succeed = (boolean)message.process(connectId, allUsers);
            if (succeed) {
                connections.send(connectId, new AckMessage(message.getOpCode()));
                toTerminate = true;

                //unlocking the lock so other threads can write to user
                allUsers.getUserById(connectId).getLock().writeLock().unlock();

            } else {

                //we already unlocked the user at allUsers action
                connections.send(connectId, new ErrorMessage(message.getOpCode()));
            }
        }
    }

    @Override
    public boolean shouldTerminate() {
        return toTerminate;
    }
}
