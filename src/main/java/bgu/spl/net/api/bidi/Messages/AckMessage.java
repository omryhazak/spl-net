package bgu.spl.net.api.bidi.Messages;

import bgu.spl.net.api.bidi.AllUsers;

import java.util.LinkedList;

public class AckMessage extends Message {

    //--------------------fields--------------

    private short opCode = 10;
    private short messageOpCode;
    private int numOfUsers = 0;
    private int numOfPosts = 0;
    private int numOfFollowers = 0;
    private int numOfFollowing = 0;
    private LinkedList<String> userNameList;

    //-------------------constructors for each option----------

    //constructor for Register, Login, logout, post, PM
    public AckMessage(short messageOpCode) {
        this.messageOpCode = messageOpCode;
    }

    //constructor for Follow and UserList
    public AckMessage(short messageOpCode, LinkedList<String> userNameList){
        this.messageOpCode = messageOpCode;
        numOfUsers = userNameList.size();
        this.userNameList = userNameList;

    }

    //constructor for Stat
    public AckMessage(short opCode, int numOfPosts, int numOfFollowers, int numOfFollowing){
        this.opCode = opCode;
        this.numOfPosts = numOfPosts;
        this.numOfFollowers = numOfFollowers;
        this.numOfFollowing = numOfFollowing;
    }

    //------------------methods---------------


    public short getMessageOpCode() {
        return messageOpCode;
    }

    @Override
    public short getOpCode() {
        return opCode;
    }

    public int getNumOfUsers() {
        return numOfUsers;
    }

    public int getNumOfPosts() {
        return numOfPosts;
    }

    public int getNumOfFollowers() {
        return numOfFollowers;
    }

    public int getNumOfFollowing() {
        return numOfFollowing;
    }

    public LinkedList<String> getUserNameList() {
        return userNameList;
    }

    @Override
    public Object process(int connectId, AllUsers allUsers) {
        return null;
    }
}

