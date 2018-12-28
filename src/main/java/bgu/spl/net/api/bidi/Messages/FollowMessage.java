package bgu.spl.net.api.bidi.Messages;

import bgu.spl.net.api.bidi.AllUsers;

import java.util.LinkedList;

public class FollowMessage extends Message {

    private boolean toFollow;
    private int numOfUsers;
    private LinkedList<String> userNameList;
    private int opCode =  4;

    public FollowMessage(String message) {

        String toParse = message;                                             //getting the complete message as string
        this.toFollow = (toParse.charAt(2) == ('1'));                          //follow or unfollow
        this.numOfUsers = toParse.charAt(3);                                       //getting the num of users to un/follow
        String usersList = toParse.substring(4);
        LinkedList<String> userLinkedList = new LinkedList<>();
        while(usersList.length()!=0){                                               //every iteration add the nexy username to the list and then cut it from string
            userLinkedList.add(usersList.substring(0, usersList.indexOf('\0')));
            usersList = usersList.substring(usersList.indexOf('\0')+1);
        }
        this.userNameList = userLinkedList;
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
        return (allUsers.followThem(connectId, numOfUsers ,userNameList, toFollow));
    }
}
