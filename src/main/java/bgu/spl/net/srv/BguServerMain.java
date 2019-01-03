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


/**
 * 1. fixing the decoder for follow. it doest get the 0/1 (un/follow) byte
 * 2. decoder doesnt read the posr right
 *
 */
