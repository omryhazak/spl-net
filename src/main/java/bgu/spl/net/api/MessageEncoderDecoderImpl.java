package bgu.spl.net.api;

import bgu.spl.net.api.bidi.Messages.*;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.LinkedList;

public class MessageEncoderDecoderImpl implements  MessageEncoderDecoder<Message> {


    private byte[] bytes = new byte[1024];
    private byte[] bytesEncoder = new byte[1024];
    private short opCode = -1;
    private int len = 0;
    private int lenEncoder=0;
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
            if(len==2){
                this.opCode = bytesToShort(Arrays.copyOfRange(this.bytes, 0, 2));
                popString();
            }
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
        this.bytesEncoder = new byte[1024];
        switch (message.getOpCode()){

            case(9):
                //notification message
                return notifEncoder(message);
            case(10):
                //ack message
                return ackEncoder(message);
            case(11):
                //error message
                return errorEncoder(message);

        }

        return null;
    }

    private void pushByte(byte nextByte) {
        if (len >= bytes.length) {
            bytes = Arrays.copyOf(bytes, len*2);
        }

        bytes[len++] = nextByte;
    }

    //pushByte for the encode bytes array
    private void pushByteEncode(byte nextByte){
        if(lenEncoder >= bytesEncoder.length){
            bytesEncoder = Arrays.copyOf(bytesEncoder, lenEncoder*2);
        }

        bytesEncoder[lenEncoder++] = nextByte;
    }

    //push a full array to the encoder bytes array, using the pushByteEncode
    private void pushArrayOfBytes(byte[] b){
        for (int i = 0; i< b.length; i++){
            pushByteEncode(b[i]);
        }
    }

    private String popString(){
        String result = new String(bytes, 0, len, StandardCharsets.UTF_8);
        len = 0;
        return result;
    }

    //register, login, Pm and stat parser
    private Message generalParser(Class<? extends Message> type, byte nextByte) {
        while (true) {
            //getting enough bytes for user name and then setting it
            if (zero == 0 && nextByte != '\0') {
                pushByte(nextByte);
                break;
            }
            if (zero == 0) {
                this.userNameForRegisterParser = popString();
                if (type == StatMessage.class) {
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
            if (zero == 1) {

                //reset the right field for the next decoding
                zero = 0;
                this.opCode = -1;

                //return the message according to type argument
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
            //and do not push the byte to the array
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
            //and empty the bytes array
            else if (counterForFollow == 1 && len == 2) {
                this.numOfUsers = (int) bytesToShort(Arrays.copyOfRange(this.bytes, 0, len));
                popString();
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

             //return the message with all arguments and reset the right fields for the next decoding
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

    //notification encoder
    private byte[] notifEncoder(Message message){

        //convert the notification opcode to bytes and assign them with pushbyte
        pushArrayOfBytes(shortToBytes(message.getOpCode()));

        //convert and assign the status of the notification
        short pmOrPublic;
        if(((NotificationMessage)message).getAnswerOpcode()==6) pmOrPublic=0;
        else pmOrPublic=1;
        pushArrayOfBytes(shortToBytes(pmOrPublic));

        //convert and assign the username that sent the message
        pushArrayOfBytes(((NotificationMessage)message).getSentBy().getBytes());
        pushByteEncode((byte) '\0');

        //convert and assign the content
        pushArrayOfBytes(((NotificationMessage)message).getContent().getBytes());
        pushByteEncode((byte) '\0');

        //resrting the right fields
        byte[] toReturn  = Arrays.copyOfRange(bytesEncoder, 0, lenEncoder);
        this.lenEncoder = 0;
        return toReturn;
    }

    //ack encoder
    private byte[] ackEncoder(Message message){
//if a general ack message is needed
        if(((AckMessage)message).getMessageOpCode()==1 || ((AckMessage)message).getMessageOpCode()==2 || ((AckMessage)message).getMessageOpCode()== 3
                || ((AckMessage)message).getMessageOpCode()== 5 || ((AckMessage)message).getMessageOpCode()== 6){

            //convert and assign the ack opcode and the message opcode
            pushArrayOfBytes(shortToBytes(message.getOpCode()));
            pushArrayOfBytes(shortToBytes(((AckMessage)message).getMessageOpCode()));

            //reset the right fields
            byte[] toReturn  = Arrays.copyOfRange(bytesEncoder, 0, lenEncoder);
            this.lenEncoder = 0;
            return toReturn;

            // if its correspond follow message
        }else if(((AckMessage)message).getMessageOpCode()==4 || ((AckMessage)message).getMessageOpCode()==7 ){

            //convert and assign the opcodes and the num of users
            pushArrayOfBytes(shortToBytes(message.getOpCode()));
            pushArrayOfBytes(shortToBytes(((AckMessage)message).getMessageOpCode()));
            pushArrayOfBytes(shortToBytes((short) ((AckMessage)message).getNumOfUsers()));

            //convert and assign the users + '\0'
            for(String s : ((AckMessage)message).getUserNameList()){
                pushArrayOfBytes(s.getBytes());
                pushByteEncode((byte)'\0');
            }
            //reset the right fields
            byte[] toReturn  = Arrays.copyOfRange(bytesEncoder, 0, lenEncoder);
            this.lenEncoder = 0;
            return toReturn;
        }
        else{

            //convert and assign the opcodes and all the other int
            pushArrayOfBytes(shortToBytes(message.getOpCode()));
            pushArrayOfBytes(shortToBytes(((AckMessage)message).getMessageOpCode()));
            pushArrayOfBytes(shortToBytes((short) ((AckMessage)message).getNumOfPosts()));
            pushArrayOfBytes(shortToBytes((short) ((AckMessage)message).getNumOfFollowers()));
            pushArrayOfBytes(shortToBytes((short) ((AckMessage)message).getNumOfFollowing()));

            //reset the right fields
            byte[] toReturn  = Arrays.copyOfRange(bytesEncoder, 0, lenEncoder);
            this.lenEncoder = 0;
            return toReturn;
        }
    }

    //eror encoder
    private byte[] errorEncoder(Message message){
        pushArrayOfBytes(shortToBytes(message.getOpCode()));
        pushArrayOfBytes(shortToBytes(((ErrorMessage)message).getMessageOpCode()));

        //reset the right fields
        byte[] toReturn  = Arrays.copyOfRange(bytesEncoder, 0, lenEncoder);
        this.lenEncoder = 0;
        return toReturn;
    }

    // short to byte (encoder)
    public byte[] shortToBytes(short num)
    {
        byte[] bytesArr = new byte[2];
        bytesArr[0] = (byte)((num >> 8) & 0xFF);
        bytesArr[1] = (byte)(num & 0xFF);
        return bytesArr;
    }

    //byte to short (decoder)
    public short bytesToShort(byte[] byteArr)
    {
        short result = (short)((byteArr[0] & 0xff) << 8);
        result += (short)(byteArr[1] & 0xff);
        return result;
    }
}