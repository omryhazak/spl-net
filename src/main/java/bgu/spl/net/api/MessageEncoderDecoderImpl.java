package bgu.spl.net.api;

import bgu.spl.net.api.bidi.Messages.*;
import bgu.spl.net.srv.User;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.LinkedList;

public class MessageEncoderDecoderImpl implements  MessageEncoderDecoder<Message> {


    private byte[] bytes = new byte[1024];
    private int len = 0;
    private short opCode = -1;

    public Message decodeNextByte(byte nextByte) {
        if(nextByte == '\n'){


            //sending the message as string to the specific message object.
            //the message object constructor parses the popString
            switch (opCode){
                case(1):
                    //create new register
                    return new RegisterMessage(popString());
                case (2):
                    //create new login
                    return new LoginMessage(popString());
                case (3):
                    //create new logout
                    popString(); //empty the bytes array manually
                    return new LogoutMessage();
                case (4):
                    //create new follow
                    return new FollowMessage(popString());
                case (5):
                    //create new post
                    return new PostMessage(popString());
                case (6):
                    //create new pm
                    return new PmMessage(popString());
                case (7):
                    //create new userlist
                    popString(); //empty the bytes array manually
                    return new UserlistMessage();
                case (8):
                    //create new stat
                    return new StatMessage(popString());

            }
        }else {
            if (len != 2) pushByte(nextByte);
            if (len == 2) {
                this.opCode = stringToShort(new String(bytes, 0, len, StandardCharsets.UTF_8));
            }
        }

        return null; // not a full message yet
    }


    public byte[] encode(Message message){
        return (message + "\n").getBytes(); //uses utf8 by default

    }

    private void pushByte(byte nextByte) {
        if (len >= bytes.length) {
            bytes = Arrays.copyOf(bytes, len*2);
        }

        bytes[len++] = nextByte;
    }

    private String popString(){
        String result = new String(bytes, 0, len, StandardCharsets.UTF_8);
        len = 0;
        return result;
    }


    private short stringToShort(String s){
        return Short.parseShort(s, 16);
    }



}


