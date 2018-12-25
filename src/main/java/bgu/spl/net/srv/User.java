package bgu.spl.net.srv;

import com.sun.tools.internal.xjc.reader.xmlschema.bindinfo.BIConversion;

import java.util.LinkedList;
import java.util.concurrent.ConcurrentLinkedQueue;

public class User {

    private final int id;
    private String name;
    private String password;
    private ConcurrentLinkedQueue<String> messageHistory;

    public User(int id, String name, String password, LinkedList<User> folowers, ConcurrentLinkedQueue<String> messageHistory) {
        this.id = id;
        this.name = name;
        this.password = password;
        this.messageHistory = messageHistory;
    }



}
