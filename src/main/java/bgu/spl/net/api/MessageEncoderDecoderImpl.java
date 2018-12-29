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
    private int zero;

    public Message decodeNextByte(byte nextByte) {
        while (opCode==-1) {
            pushByte(nextByte);
            if(len==2) this.opCode = stringToShort(popString());
        }

        //register message
        if(opCode==1){
            parseRegister(nextByte);
        }

        //login message
        else if(opCode==2){
            parseLogin(nextByte);
        }

        //logout message
        else if(opCode==3){
            return new LogoutMessage();
        }

        //follow message
        else if(opCode==4){
            boolean toFollow;
            int numOfUsers;
            if(zero==0) {
                if ((int)nextByte == 1) {
                    toFollow = true;
                    zero++;
                } else {
                    toFollow = false;
                    zero++;
                }

            }
            if(zero==1){
                while(len<2){
                    pushByte(nextByte);
                }
            }
        }
        return null;
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

    //register parser
    private RegisterMessage parseRegister(byte nextByte){
        while (zero == 0 && nextByte != '\0'){
            pushByte(nextByte);
        }
        String userName = popString();
        while (zero == 1 && nextByte != '\0'){
            pushByte(nextByte);
        }
        return new RegisterMessage(userName, popString());
    }

    private LoginMessage parseLogin(byte nextByte){
        while (zero == 0 && nextByte != '\0'){
            pushByte(nextByte);
        }
        String userName = popString();
        while (zero == 1 && nextByte != '\0'){
            pushByte(nextByte);
        }
        return new LoginMessage(userName, popString());
    }
}