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
    private int numOfUsers;
    private boolean toFollow;

    public Message decodeNextByte(byte nextByte) {

        //getting enough bytes to get the opCode
        while (opCode==-1) {
            pushByte(nextByte);
            if(len==2)
                this.opCode = stringToShort(popString());
        }

        //register message
        if(opCode==1){
            RegisterAndLoginParser(RegisterMessage.class ,nextByte);
        }

        //login message
        else if(opCode==2){
            RegisterAndLoginParser(LoginMessage.class ,nextByte);
        }

        //logout message
        else if(opCode==3){
            return new LogoutMessage();
        }

        //follow message
        else if(opCode==4){
            if(zero==0) {
                if ((short)nextByte == 1) {
                    this.toFollow = true;
                    zero++;
                } else {
                    this.toFollow = false;
                    zero++;
                }
                return null;
            }
            if(zero==1){
                while(len<2){
                    pushByte(nextByte);
                    break;
                }
            }if(len==2){
                this.numOfUsers = Integer.parseInt(popString());
                zero++;
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

    //register and login parser
    private Message RegisterAndLoginParser(Class<? extends Message> type, byte nextByte){
        while (zero == 0 && nextByte != '\0'){
            pushByte(nextByte);
        }
        String userName = popString();
        while (zero == 1 && nextByte != '\0'){
            pushByte(nextByte);
        }
        if(type == RegisterMessage.class)
            return new RegisterMessage(userName, popString());
        else return new LoginMessage(userName, popString());
    }


}