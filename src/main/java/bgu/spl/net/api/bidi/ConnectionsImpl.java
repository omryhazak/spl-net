package bgu.spl.net.api.bidi;

import bgu.spl.net.srv.ConnectionHandler;
import bgu.spl.net.srv.User;

import java.util.LinkedList;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

public class ConnectionsImpl<T> implements Connections<T> {


    private ConcurrentHashMap<Integer ,ConnectionHandler> registeredUsersMap;
    private ConcurrentHashMap<Integer, LinkedList<User>> followTheLeader;
    private AtomicInteger idCounter;


    public ConnectionsImpl(){
        this.registeredUsersMap = new ConcurrentHashMap<>();
        this.idCounter = new AtomicInteger(1);
    }

    public ConnectionsImpl(ConcurrentHashMap<Integer, ConnectionHandler> usersMap, ConcurrentHashMap<Integer, ConnectionHandler> loggedInUsersMap) {
        this.registeredUsersMap = usersMap;
        this.idCounter = idCounter;
    }

    //checking if the given client is active and then send him the msg
    @Override
    public boolean send(int connectionId, T msg) {
        synchronized (registeredUsersMap) {
            if (registeredUsersMap.containsKey(connectionId)) {
                ConnectionHandler c = registeredUsersMap.get(connectionId);
                c.send(msg);
                return true;
            } else
                return false;
        }
    }


    //looping through all active clients in the map and send the msg
    @Override
    public void broadcast(T msg) {
        synchronized (registeredUsersMap){
            for(ConnectionHandler c : registeredUsersMap.values()){
                c.send(msg);
            }
        }
    }

    @Override
    public void disconnect(int connectionId) {
        registeredUsersMap.remove(connectionId);
    }

    public void connect(ConnectionHandler c){
        registeredUsersMap.put(idCounter.get(), c);
        idCounter.incrementAndGet();
    }

}
