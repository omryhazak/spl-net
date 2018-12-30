package bgu.spl.net.api;

import bgu.spl.net.api.bidi.Messages.*;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.LinkedList;

public class MessageEncoderDecoderImpl implements  MessageEncoderDecoder<Message> {


    private byte[] bytes = new byte[1024];
    private short opCode = -1;
    private int len = 0;
    private int zero=0;
    private int numOfUsers;
    private int counterForFollow=0;
    private boolean toFollow;
    private boolean followLoop =true;
    private String userNameForRegisterParser = "";
    private LinkedList<String> usersToFollow = new LinkedList<>();
    private LinkedList<String> usersToSend = new LinkedList<>();


    public Message decodeNextByte(byte nextByte) {

        //getting enough bytes to get the opCode
        while (opCode==-1) {
            pushByte(nextByte);
            if(len==2)
                this.opCode = stringToShort(popString());
        }

        //register message
        if(opCode == 1){
            return generalParser(RegisterMessage.class ,nextByte);
        }

        //login message
        else if(opCode == 2){
            return generalParser(LoginMessage.class ,nextByte);
        }

        //logout message
        else if(opCode == 3){
            return new LogoutMessage();
        }

        //follow message
        else if(opCode == 4){
            return FollowParser(nextByte);
        }

        //post message
        else if(opCode == 5){
            return postParser(nextByte);
        }

        //PM message
        else if (opCode == 6){
            return generalParser(PmMessage.class, nextByte);
        }

        //userList message
        else if (opCode == 7){
            return new UserlistMessage();
        }

        //stat message
        else if(opCode == 8){
            return generalParser(StatMessage.class, nextByte);
        }
        return null;
    }


    public byte[] encode(Message message){
        switch (message.getOpCode()){
            case(9):
                short op = 9;
                String toBytes = Short.toString(op);

        }

        return null;
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

    //register, login, Pm and stat parser
    private Message generalParser(Class<? extends Message> type, byte nextByte) {
        while (true) {
            //getting enough bytes for user name and then setting it
            if (zero == 0 && nextByte != '\0') {
                pushByte(nextByte);
                break;
            }
            if(zero == 0){
                this.userNameForRegisterParser = popString();
                if(type == StatMessage.class){
                    this.zero = 0;
                    return new StatMessage(this.userNameForRegisterParser);
                }
                zero++;
                break;
            }

            //getting enough bytes for password and then setting it
            if (zero == 1 && nextByte != '\0') {
                pushByte(nextByte);
                break;
            }
            if(zero ==1){
                zero = 0;
                if (type == RegisterMessage.class)
                    return new RegisterMessage(this.userNameForRegisterParser, popString());
                else if (type == LoginMessage.class) {
                    return new LoginMessage(this.userNameForRegisterParser, popString());
                } else return new PmMessage(this.userNameForRegisterParser, popString());

            }
        }
        return null;
    }

    //follow message parser
    private Message FollowParser(byte nextByte){
        while(followLoop) /* the while helps us break the decodeNextByte when needed*/ {

            //setting the boolean toFollow
            if (counterForFollow == 0) {
                if ((short) nextByte == 1) {
                    this.toFollow = true;
                    counterForFollow++;
                } else {
                    this.toFollow = false;
                    counterForFollow++;
                }
                break;
            }

            //getting enough bytes to get the num of users to un/follow
            if (counterForFollow == 1 && len < 2) {
                pushByte(nextByte);
                break;
            }

            //setting the num of users
            else if (counterForFollow == 1 && len == 2) {
                this.numOfUsers = Integer.parseInt(popString());
                counterForFollow++;
                break;
            }

            //getting the names of the users to follow
            if(counterForFollow==2 && zero < numOfUsers){
                if(nextByte != '\0'){
                    pushByte(nextByte);
                }
                else{
                    usersToFollow.add(popString());
                    zero++;
                }
                break;

             //return the message with all arguments and reset the right fields
            }else if(zero == numOfUsers){
                FollowMessage f = new FollowMessage(this.toFollow, this.numOfUsers, this.usersToFollow);
                this.usersToFollow = new LinkedList<>();
                this.numOfUsers = 0;
                this.zero = 0;
                this.counterForFollow = 0;
                return f;
            }
        }
        return null;
    }

    //post message parser
    private Message postParser(byte nextByte) {

        //get all the content
        if (nextByte != '\0') {
            pushByte(nextByte);

        // if we have all content, extract tagging
        } else {
            String toParse = popString();
            StringBuffer buff = new StringBuffer(toParse);
            String tmp = toParse;
            while (buff.indexOf("@") != -1) {
                String toAdd;
                int i = buff.indexOf("@") + 1;
                int j = buff.indexOf("@") + 1;
                while (tmp.charAt(j) != ' ') {
                    j++;
                }
                toAdd = tmp.substring(i, j);
                this.usersToSend.add(toAdd);
            }
            PostMessage p = new PostMessage(toParse, this.usersToSend);
            this.usersToSend = new LinkedList<>();
            return p;
        }
        return null;
    }

}