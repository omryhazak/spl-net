package bgu.spl.net.api.bidi.Messages;

import java.util.LinkedList;

public class FollowMessage implements Message {

    private boolean toFollow;
    private int numOfUsers;
    private LinkedList<String> userNameList;
    private int opCode =  4;

    public FollowMessage(boolean toFollow, int numOfUsers, LinkedList<String> userNameList) {
        this.toFollow = toFollow;
        this.numOfUsers = numOfUsers;
        this.userNameList = userNameList;
    }

    public boolean isToFollow() {
        return toFollow;
    }

    public int getNumOfUsers() {
        return numOfUsers;
    }

    public LinkedList<String> getUserNameList() {
        return userNameList;
    }
}
