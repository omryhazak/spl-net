package bgu.spl.net.api.bidi;

import bgu.spl.net.srv.ConnectionHandler;
import bgu.spl.net.srv.User;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.concurrent.ConcurrentHashMap;


//Data structure holding all the users info
public class AllUsers {

    //-----------------fields----------------------------------

    private ConcurrentHashMap<Integer , User> registeredUsersMap;



    // ---------------constructor-------------------------------
    public AllUsers() {
        registeredUsersMap = new ConcurrentHashMap<>();
    }


    //--------------------methods-----------------------------


    public boolean registerToSystem(User user){
        //goes over registered users map to check if user is already registered
        //synchronizing in order to prevent an occasion when two clients with same name tries to register the same time
        synchronized (registeredUsersMap) {
            for (Integer key : registeredUsersMap.keySet()) {
                if (key == user.getId()) return false;
                if (registeredUsersMap.get(key).getName().equals(user.getName())) return false;
            }

            //if there is no other user with the same id, name or password register this user to system
            registeredUsersMap.put(user.getId(), user);
        }
        return true;
    }



    public boolean logInToSystem(String userName, String password) {
        //goes over registered users map to check if user exist, if so checks if password given matches the password he was registered with
        for (Integer key : registeredUsersMap.keySet()){

                //checks if the name of this user equals the name we are looking for
                if (registeredUsersMap.get(key).getName().equals(userName)) {

                    //if it is the user we are looking for, checks if the password is the same as we got
                    if (registeredUsersMap.get(key).getPassword().equals(password)){

                        //synchronizing the user to changes, so there wont be two threads trying to log in the same time
                        synchronized (registeredUsersMap.get(key)) {

                            //if the password is the same, we check if the user is already logged in
                            if (!registeredUsersMap.get(key).hasLoggedIn()) {

                                //if not logged in already, we log him in
                                registeredUsersMap.get(key).setLoggedIn(true);
                                return true;
                            }
                            //if the user is already logged in, we can break out of loop because there will be no more user with the same name
                            else break;
                        }
                    }
                    //if the password is incorrect, we can break out of loop because there will be no more user with the same name
                    else break;
                }
        }
        return false;
    }


    public boolean logOut(int connectId) {
        //checks if user logged into system
        if(isLoggedIn(connectId)) {

            //if so, logging out
            registeredUsersMap.get(connectId).setLoggedIn(false);
            return true;
        }
        return false;
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

            //if so, I try to add all the names in the list to my followThem list
            ans = registeredUsersMap.get(connectId).followThem(userNameList, toFollow);

            //add me as folllower to all the
            addAsFollower(connectId, ans);
        }
        return ans;
    }

    private void addAsFollower(int connectId, LinkedList<String> names) {
        String nameOfMe = registeredUsersMap.get(connectId).getName();
        int counter = names.size();

        while (counter > 0) {
            //goes over all my list
            for (Integer key : registeredUsersMap.keySet()) {

                //if my list of names contains users name
                if (names.contains(registeredUsersMap.get(key).getName())) {

                    //add me to him as a follower
                    registeredUsersMap.get(key).addFollower(nameOfMe);
                    counter = counter - 1;
                }
            }
        }
    }

    public LinkedList<String> returnUserList(){
        Arrays a = registeredUsersMap.keySet();
    }


}

