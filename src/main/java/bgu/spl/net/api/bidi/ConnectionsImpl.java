package bgu.spl.net.api.bidi;

import bgu.spl.net.srv.ConnectionHandler;
import bgu.spl.net.srv.User;

import java.util.LinkedList;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

public class ConnectionsImpl<T> implements Connections<T> {

    private ConcurrentHashMap<Integer, ConnectionHandler> allSockets;


    private AtomicInteger idCounter;


    public ConnectionsImpl(){
        this.idCounter = new AtomicInteger(1);
    }


    //checking if the given client is active and then send him the msg
    @Override
    public boolean send(int connectionId, T msg) {
        synchronized (allSockets) {
            if (allSockets.containsKey(connectionId)) {
                ConnectionHandler c = allSockets.get(connectionId);
                c.send(msg);
                return true;
            } else
                return false;
        }
    }


    //looping through all active clients in the map and send the msg
    @Override
    public void broadcast(T msg) {
        synchronized (allSockets){
            for(ConnectionHandler c : allSockets.values()){
                c.send(msg);
            }
        }
    }

    @Override
    public void disconnect(int connectionId) {
        allSockets.remove(connectionId);
    }

    public void connectToSystem(ConnectionHandler c){
        allSockets.put(idCounter.get(), c);
        idCounter.incrementAndGet();
    }

}
