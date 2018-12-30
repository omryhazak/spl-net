package bgu.spl.net.api.bidi.Messages;

import bgu.spl.net.api.bidi.AllUsers;

import java.util.LinkedList;

public class AckMessage extends Message {

    //--------------------fields--------------

    private short opCode = 10;
    private int messageOpCode;
    private int numOfUsers = 0;
    private int numOfPosts = 0;
    private int numOfFollowers = 0;
    private int numOfFollowing = 0;
    private String userNameList = "";

    //-------------------constructors for each option----------

    //constructor for Register, Login
    public AckMessage(short messageOpCode) {
        this.messageOpCode = messageOpCode;
    }

    //constructor for Follow and UserList
    public AckMessage(int messageOpCode, LinkedList<String> userNameList){
        this.messageOpCode = messageOpCode;
        numOfUsers = userNameList.size();
        for (String s : userNameList){
            this.userNameList = this.userNameList + s + '\0';
        }
    }

    //constructor for Stat
    public AckMessage(short opCode, int numOfPosts, int numOfFollowers, int numOfFollowing){
        this.opCode = opCode;
        this.numOfPosts = numOfPosts;
        this.numOfFollowers = numOfFollowers;
        this.numOfFollowing = numOfFollowing;
    }

    //------------------methods---------------


    @Override
    public Object process(int connectId, AllUsers allUsers) {
        return null;
    }
}

