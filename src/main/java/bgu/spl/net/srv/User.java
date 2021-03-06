package bgu.spl.net.srv;

import bgu.spl.net.api.objectOfThree;

import java.util.LinkedList;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.locks.ReentrantReadWriteLock;


public class User {

    private int connectId;
    private int numOfPosts = 0;
    private String name;
    private String password;
    private boolean loggedIn;
    private LinkedList<String> followsThem;
    private LinkedList<String> followingMe;
    private ConcurrentLinkedQueue<objectOfThree> messages;
    private ReentrantReadWriteLock lock = new ReentrantReadWriteLock(true);

    public User(String name, String password) {
        this.connectId = -1;
        this.name = name;
        this.password = password;
        loggedIn = false;
        followsThem = new LinkedList<>();
        followingMe = new LinkedList<>();
        messages = new ConcurrentLinkedQueue<>();
    }

    public int getConnectId() {
        return connectId;
    }

    public void setConnectId(int connectId) {
        this.connectId = connectId;
    }

    public String getName() {
        return this.name;
    }

    public String getPassword() {
        return password;
    }

    public void setLoggedIn(boolean loggedIn) {
            this.loggedIn = loggedIn;
    }

    public boolean hasLoggedIn(){
        return loggedIn;
    }

    public int getNumOfPosts() {
        return numOfPosts;
    }

    public int numOfFollowers(){
        return this.followingMe.size();
    }
    public int numOfFollowing(){
        return this.followsThem.size();
    }

    public LinkedList<String> followThem(LinkedList<String> userNameList, boolean toFollow){
        //counter list will count our "succeeds" and save them
        LinkedList<String> ans = new LinkedList<>();

        //if we need to start follow the names in the list
        if (toFollow) {

            //so go over the names in the list
            for (String name : userNameList) {

                //for each name check if we dont follow him yet
                if (!checkIfAlreadyFollowing(name)) {

                    //if so, start follow him
                    followsThem.add(name);
                    ans.add(name);
                }
            }
        }

        //if we need to unfollow the names in the list
        else{

            //go over the names in the list
            for(String name : userNameList){

                //for each name check if we follow him
                if(checkIfAlreadyFollowing(name)){

                    //if so, unfollow him
                    followsThem.remove(name);
                    ans.add(name);
                }
            }
        }
        return ans;
    }


    private boolean checkIfAlreadyFollowing(String name){
        //function that goes over the list I follow and checks if the name is in the list
        for (String name2 : followsThem){
            if (name2.equals(name)) return true;
        }
        return false;
    }


    public void addFollower(String name){
        followingMe.add(name);
    }

    public void removeFollower(String nameOfMe) {
        followingMe.remove(nameOfMe);
    }

    public LinkedList<String> getFollowingMe() {
        return followingMe;
    }

    //type:
    //0 for PM
    //1 for PUBLIC
    public void addMessage(int userId, String message, int type) {

        messages.add(new objectOfThree(userId, message, type));
    }

    public ConcurrentLinkedQueue<objectOfThree> getMessages() {
        return messages;
    }

    public void setNumOfPosts() {
        this.numOfPosts = numOfPosts + 1;
    }

    public ReentrantReadWriteLock getLock() {
        return lock;
    }


}
