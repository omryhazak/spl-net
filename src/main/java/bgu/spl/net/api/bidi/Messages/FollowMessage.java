package bgu.spl.net.api.bidi.Messages;

import bgu.spl.net.api.bidi.AllUsers;

import java.util.LinkedList;

public class FollowMessage extends Message {

    private boolean toFollow;
    private int numOfUsers;
    private LinkedList<String> userNameList;
    private short opCode = 4;

    public FollowMessage(boolean toFollow, int numOfUsers, LinkedList<String> userNameList) {
        this.numOfUsers = numOfUsers;
        this.toFollow = toFollow;
        this.userNameList = userNameList;
    }

    @Override
    public short getOpCode() {
        return opCode;
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

    @Override
    public LinkedList<String> process(int connectId, AllUsers allUsers) {
        return (allUsers.followThem(connectId, numOfUsers, userNameList, toFollow));
    }
}
