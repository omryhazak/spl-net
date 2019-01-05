package bgu.spl.net.api.bidi;

import bgu.spl.net.api.objectOfThree;
import bgu.spl.net.srv.User;

import java.util.LinkedList;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.Semaphore;


//Data structure holding all the users info
public class AllUsers {

    //-----------------fields----------------------------------

    private ConcurrentHashMap<Integer , User> loggedInUsersMap;
    private ConcurrentHashMap<String , User> usersByName;
    private LinkedList<String> spyMe;
    private LinkedList<String> usersByOrder;
    private Semaphore sem;


    // ---------------constructor-------------------------------

    public AllUsers() {

        loggedInUsersMap = new ConcurrentHashMap<>();
        usersByName = new ConcurrentHashMap<>();
        usersByOrder = new LinkedList<>();
        spyMe = new LinkedList<>();
        sem = new Semaphore(1);
    }


    //--------------------methods-----------------------------


    public boolean registerToSystem(User user){

        //starts semaphore that will not let two clients with same name to register together
        try {
            sem.acquire();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        //checks if there is an user with the same name
        if (!usersByName.containsKey(user.getName())) {
            usersByName.put(user.getName(), user);
            usersByOrder.add(user.getName());
            sem.release();
            return true;
        }

        //releases semaphore
        sem.release();
        return false;
    }



    public ConcurrentLinkedQueue<objectOfThree> logInToSystem(String userName, String password, int connectId) {

        ConcurrentLinkedQueue<objectOfThree> ans = new ConcurrentLinkedQueue<>();

        // checks if the user exist in system by his name
        if (usersByName.containsKey(userName)){

            usersByName.get(userName).getLock().writeLock().lock();

            //if it is the user we are looking for, checks if the password is the same as we got
            if (usersByName.get(userName).getPassword().equals(password)) {

                    //if the password is the same, we check if the user is already logged in
                    if (!isLoggedIn(usersByName.get(userName).getConnectId())) {

                        //if not logged in already, we log him in
                        usersByName.get(userName).setLoggedIn(true);

                        //if connection ID is different we change it to the current connection ID
                        //than we change the map so it will find the user by the new connection Id
                        if (usersByName.get(userName).getConnectId() != connectId) {
                            int oldId = usersByName.get(userName).getConnectId();
                            usersByName.get(userName).setConnectId(connectId);
                            loggedInUsersMap.remove(oldId);
                            loggedInUsersMap.put(connectId, usersByName.get(userName));
                        }
                        else{
                            loggedInUsersMap.put(connectId, usersByName.get(userName));
                        }

                        //go over his queue of messages, and send it to client
                        ans = usersByName.get(userName).getMessages();

                        //if we have no message and we dont want to return null ans
                        if(ans.size() == 0){
                            ans.add(new objectOfThree(-3, "", 0));
                        }

                        return ans;
                    }
            }

            //if we have user with that name but the password is not good - we still need to unlock him, same if he is already logged in
            ans.add(new objectOfThree(-2, "", 0));
            return ans;

        }

        //if we didnt even have user with this name so we cant lock him at all
        ans.add(new objectOfThree(-1, "", 0));
        return ans;
    }


    public boolean logOut(int connectId) {

        synchronized (loggedInUsersMap) {

            //checks if user logged into system
            if (isLoggedIn(connectId)) {

                //blocking user to changes while we read from it. if someone wants to change users status he will need to wait until we unlock.
                //we unlock this key at the protocol after(!) we finish the process and sends by connectionhandler this message of disconnect
                loggedInUsersMap.get(connectId).getLock().writeLock().lock();

                //if so, logging out
                loggedInUsersMap.get(connectId).setLoggedIn(false);
                return true;

            }
            return false;
        }

    }


    //we dont need to lock the operation, we lock user everytime we call this function!
    private boolean isLoggedIn(int connectId) {

        synchronized (loggedInUsersMap) {

            //checks if the user exists in system
            if (loggedInUsersMap.containsKey(connectId)) {

                //checks if user logged in to system
                return loggedInUsersMap.get(connectId).hasLoggedIn();
            }
        }
        return false;
    }


    public LinkedList<String> followThem(int connectId, int numOfUsers, LinkedList<String> userNameList, boolean toFollow) {
        LinkedList<String> ans = new LinkedList<>();

        //checks if I am logged in
        //no need to synchronize because this thread is the only one capable of logging out
        if (isLoggedIn(connectId)) {

            //checks if user out of list is exist
            for(String name : userNameList){
                if(!usersByName.containsKey(name)){
                    userNameList.remove(name);
                }
            }

            //if so, I try to add all the names in the list to my followThem list or to unfollow them
            ans = loggedInUsersMap.get(connectId).followThem(userNameList, toFollow);

            if(ans.size()>0) {

                //add me as folllower to all the names in the list if necessary
                if (toFollow) {
                    addAsFollower(connectId, ans);
                }
                //else remove me
                else {
                    removeAsFollower(connectId, ans);
                }
            }
        }

        return ans;
    }

    private void addAsFollower(int connectId, LinkedList<String> names) {
        String nameOfMe = loggedInUsersMap.get(connectId).getName();

        //goes over list of names I need to follow, and adds me as follower
        for (String name : names){

            //adds me as follower of user
            usersByName.get(name).addFollower(nameOfMe);
        }

    }

    private void removeAsFollower(int connectId, LinkedList<String> names) {
        String nameOfMe = loggedInUsersMap.get(connectId).getName();

        //goes over list of names I need to follow, and adds me as follower
        for (String name : names){

            //removes me from followers list of user
            usersByName.get(name).removeFollower(nameOfMe);
        }
    }

    //here we dont need any synchronizing because the same user is the only one who can log himself out,
    //but it is the same thread so it will happend by order and wont be any problem
    public LinkedList<String> returnUserList(int connectId){

        //blocks user for writing, let other threads to read details from user
        if (isLoggedIn(connectId)) {
            return usersByOrder;
        }

        //we dont need the user any more
        loggedInUsersMap.get(connectId).getLock().readLock().unlock();

        LinkedList<String> error = new LinkedList<>();
        return error;
    }


    public LinkedList<Integer> postMessage(int connectId, String content, LinkedList<String> usersNameToSend) {

        //create new list we will return, contains users we need to send them the message
        LinkedList<Integer> output = new LinkedList<>();

        //check if I am logged in
        if(loggedInUsersMap.get(connectId).hasLoggedIn()){

            //getts all the users following me
            User me = loggedInUsersMap.get(connectId);

            //create new list for temporal time, holds all the users follow me
            LinkedList<String> tmp = me.getFollowingMe();

            //going over names in the list,
            for(String name : usersNameToSend){

                //checks if every name is real name of user and if so,
                if(usersByName.containsKey(name)){

                    //checks if he is not already at list,
                    if(!tmp.contains(name)){
                        tmp.add(name);
                    }
                }
            }

            //now we go over tmp list, contains all the names we need to post them the message
            for(String name : tmp){

                User user = usersByName.get(name);

                //loock user to changes
                user.getLock().readLock().lock();

                //now we can check if he is logged in. if so we will send him the message
                if(usersByName.get(name).hasLoggedIn()){
                    output.add(user.getConnectId());
                }

                //if not logged in, we will push the message to his queue
                else{
                    user.addMessage(connectId, content, 1);

                    //we can now unlock him for changes
                    user.getLock().readLock().unlock();
                }
            }

            return output;
        }

        output.add(-1);
        return output;
    }

    private void handleUser (int connectId, String name, String content, LinkedList<Integer> output) {
        //check if user logged in to system
        if (usersByName.get(name).hasLoggedIn()) {

            //if so, we add hi ID to our list of connections ID
            output.add(usersByName.get(name).getConnectId());
        }

        //if not logged in, add this message to users queue of messages
        else {
            usersByName.get(name).addMessage(connectId, content,1 );

            //we finished changes for follower, now we can release his lock
            usersByName.get(name).getLock().writeLock().unlock();

        }
    }


    public int sendPM(int connectId, String toSend, String content) {

        if (isLoggedIn(connectId) && usersByName.containsKey(toSend)){

            User user = usersByName.get(toSend);
            //blocks user to changes, so we will be able to send him messages
            user.getLock().readLock().lock();

            if(isLoggedIn(user.getConnectId())){
                return usersByName.get(toSend).getConnectId();
            }

            else{
                user.addMessage(connectId, content, 0);
                user.getLock().readLock().unlock();
                return -2;
            }
        }
        return -1;
    }

    public int[] getStatOfUser(String name, int connectId){
        int[] output = new int[3];
        if (usersByName.containsKey(name) && isLoggedIn(connectId)){
            output[0] = usersByName.get(name).getNumOfPosts();
            output[1] = loggedInUsersMap.get(connectId).numOfFollowers();
            output[2] = loggedInUsersMap.get(connectId).numOfFollowing();
            return output;
        }

        return null;
    }

    public User findUser(String name){
        return usersByName.get(name);
    }

    public User getUserById(int id) {
        return loggedInUsersMap.get(id);
    }
}

