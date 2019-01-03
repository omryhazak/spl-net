package bgu.spl.net.api.bidi;

import bgu.spl.net.api.Pair;
import bgu.spl.net.srv.User;

import java.util.LinkedList;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;


//Data structure holding all the users info
public class AllUsers {

    //-----------------fields----------------------------------

    private ConcurrentHashMap<Integer , User> registeredUsersMap;
    private ConcurrentHashMap<String , User> usersByName;
    private LinkedList<String> spyMe;
    private LinkedList<String> usersByOrder;
    private Object lock;


    // ---------------constructor-------------------------------

    public AllUsers() {

        registeredUsersMap = new ConcurrentHashMap<>();
        usersByName = new ConcurrentHashMap<>();
        usersByOrder = new LinkedList<>();
        spyMe = new LinkedList<>();
        lock = new Object();
    }


    //--------------------methods-----------------------------


    public boolean registerToSystem(User user){

        //goes over registered users map to check if user is already registered
        //synchronizing in order to prevent an occasion when two clients with same name tries to register the same time
        synchronized (registeredUsersMap) {
            for (Integer key : registeredUsersMap.keySet()) {

                //checks if exist user with the same name in system
                if (registeredUsersMap.get(key).getName().equals(user.getName())) return false;
            }

            //if there is no other user with the same id, name or password register this user to system
            registeredUsersMap.put(user.getConnectId(), user);
            usersByName.put(user.getName(), user);
            usersByOrder.add(user.getName());
            System.out.println(usersByName.get(user.getName()).getName()+ " is innnnnnn");
        }
        return true;
    }



    public ConcurrentLinkedQueue<Pair> logInToSystem(String userName, String password, int connectId) {

        ConcurrentLinkedQueue<Pair> ans;

        // checks if the user exist in system by his name
        if (usersByName.containsKey(userName)){
            System.out.println(1);
            User user = usersByName.get(userName);

            //if it is the user we are looking for, checks if the password is the same as we got
            if (user.getPassword().equals(password)) {
                System.out.println(2);
                //synchronizing the user to changes, so there wont be two threads trying to log in the same time
                synchronized (usersByName.get(userName)) {

                    //if the password is the same, we check if the user is already logged in
                    if (!user.hasLoggedIn()) {
                        System.out.println(3);
                        //if not logged in already, we log him in
                        usersByName.get(userName).setLoggedIn(true);

                        //go over his queue of messages, and send it to client
                        ans = usersByName.get(userName).getMessages();

                        //if connection ID is different we change it to the current connection ID
                        //than we change the map so it will find the user by the new connection Id
                        if (user.getConnectId() != connectId) {
                            System.out.println(4);
                            int oldId = user.getConnectId();
                            user.setConnectId(connectId);
                            registeredUsersMap.put(connectId, user);
                            registeredUsersMap.remove(oldId);
                        }

                        return ans;
                    }
                }
            }

        }

        return null;
    }


    public boolean logOut(int connectId) {

//       *********????????************* //synchronizing logOut and post, so we know where to put the message (to the user himself, or in his queue of messages) and
//        //also to prevent an occasion when we try to post a message to some one whom logged out and connected with other connect ID
//        synchronized (lock) {

            //checks if user logged into system
            if (isLoggedIn(connectId)) {

                //if so, logging out
                registeredUsersMap.get(connectId).setLoggedIn(false);
                return true;
            }
            return false;


//        }



    }


    private boolean isLoggedIn(int connectId) {
        //synchronizing user in order to prevent occasion when one thread trying to check if user logged in and other thread trying to unregister him
        synchronized (registeredUsersMap.get(connectId)) {

            //checks if the user exists in system
            if (registeredUsersMap.containsKey(connectId)) {

                //checks if user logged in to system
                return registeredUsersMap.get(connectId).hasLoggedIn();
            }
        }
        return false;
    }


    public LinkedList<String> followThem(int connectId, int numOfUsers, LinkedList<String> userNameList, boolean toFollow) {
        LinkedList<String> ans = new LinkedList<>();

        //checks if I am logged in
        if (isLoggedIn(connectId)) {

            //if so, I try to add all the names in the list to my followThem list or to unfollow them
            ans = registeredUsersMap.get(connectId).followThem(userNameList, toFollow);

            //add me as folllower to all the
            addAsFollower(connectId, ans);
        }
        return ans;
    }

    private void addAsFollower(int connectId, LinkedList<String> names) {
        String nameOfMe = registeredUsersMap.get(connectId).getName();

        //goes over list of names I need to follow, and adds me as follower
        for (String name : names){
            usersByName.get(name).addFollower(nameOfMe);
        }

    }

    public LinkedList<String> returnUserList(){
        return usersByOrder;
    }


    public LinkedList<Integer> postMessage(int connectId, String content, LinkedList<String> usersNameToSend) {

        //create new list we will return, contains users we need to send them the message
        LinkedList<Integer> output = new LinkedList<>();

        //checks if we are logged in
        if(isLoggedIn(connectId)){

            //synchronizing registeredUsersMap so no one will not change the list while we are iterating over it
            synchronized (registeredUsersMap){

                //going over the list of users following me, and add their connection ID to the list
                for (String name : registeredUsersMap.get(connectId).getFollowingMe()){

                    //check if user logged in to system
                    if (usersByName.get(name).hasLoggedIn()){

                        //if so, we add hi ID to our list of connections ID
                        output.add(usersByName.get(name).getConnectId());
                    }

                    //if not logged in, add this message to users queue of messages
                    else{
                        usersByName.get(name).addMessage(connectId, content);
                    }
                }

                //for each name in the usersname list, we chek if it is a user who registered to the system,
                for(String name  : usersNameToSend){

                    if(usersByName.containsKey(name)){

                        //check if user logged in to system
                        if (usersByName.get(name).hasLoggedIn()){

                            //if so, we add hi ID to our list of connections ID
                            output.add(usersByName.get(name).getConnectId());
                        }

                        //if not logged in, add this message to users queue of messages
                        else{
                            usersByName.get(name).addMessage(connectId, content);
                        }
                    }
                }

            }

            //if message was sent, increment num of posts for user and spy him forever
            registeredUsersMap.get(connectId).setNumOfPosts();
            spyMe.add(content);
            return output;
        }

        output.add(-1);
        return output;
    }

    public int sendPM(int connectId, String toSend) {
        if (isLoggedIn(connectId) && usersByName.containsKey(toSend)){
            return usersByName.get(toSend).getConnectId();
        };
        return -1;
    }

    public int[] getStatOfUser(String name, int connectId){
        int[] output = new int[3];
        if (usersByName.contains(name) && isLoggedIn(connectId)){
            output[0] = usersByName.get(name).getNumOfPosts();
            output[1] = registeredUsersMap.get(connectId).numOfFollowers();
            output[2] = registeredUsersMap.get(connectId).numOfFollowing();
            return output;
        }

        return null;
    }

    public User findUser(int connId){
        return registeredUsersMap.get(connId);
    }

    public User findUser(String name){
        return this.usersByName.get(name);
    }

}

