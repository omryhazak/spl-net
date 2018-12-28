package bgu.spl.net.srv;

import com.sun.tools.internal.xjc.reader.xmlschema.bindinfo.BIConversion;

import java.util.LinkedList;
import java.util.concurrent.ConcurrentLinkedQueue;

public class User {

    private final int id;
    private String name;
    private String password;
    private boolean loggedIn;
    private LinkedList<String> followsThem;
    private LinkedList<String> followingMe;

    public User(int id, String name, String password) {
        this.id = id;
        this.name = name;
        this.password = password;
        loggedIn = false;
        followsThem = new LinkedList<>();
        followingMe = new LinkedList<>();
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
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

    public LinkedList<String> followThem(LinkedList<String> userNameList, boolean toFollow){

        //counter will count our "succeeds"
        LinkedList<String> ans = new LinkedList<>();

        //if we need to start follow the names in the list
        if (toFollow) {

            //so go over the names in the list
            for (String name : userNameList) {

                //for each name check if we already follow him
                if (!checkIfAlreadyFollowing(name)) {

                    //if not, start follow him
                    followsThem.add(name);
                    ans.add(name);
                }
            }
        }

        //if we need to unfollow the names in the list
        else{

            //go over the names in the list
            for(String name : userNameList){

                //for each name check if we dont follow him
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
}
