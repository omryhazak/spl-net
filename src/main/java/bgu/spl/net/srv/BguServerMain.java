package bgu.spl.net.srv;

import bgu.spl.net.api.MessageEncoderDecoderImpl;
import bgu.spl.net.api.bidi.AllUsers;
import bgu.spl.net.api.bidi.BidiMessagingProtocolImpl;

public class BguServerMain {
    public static void main(String [] args){
        AllUsers allUsers = new AllUsers();
        Server.threadPerClient(1111, ()-> new BidiMessagingProtocolImpl(allUsers), ()->new MessageEncoderDecoderImpl()).serve();
//        Server.reactor(Runtime.getRuntime().availableProcessors(), 111, ()-> new BidiMessagingProtocolImpl(allUsers), ()-> new MessageEncoderDecoderImpl()).serve();
    }
}
