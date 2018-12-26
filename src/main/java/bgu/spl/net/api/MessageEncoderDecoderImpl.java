package bgu.spl.net.api;

import bgu.spl.net.api.bidi.Messages.Message;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;

public class MessageEncoderDecoderImpl implements  MessageEncoderDecoder<Message> {


    private byte[] bytes = new byte[1024];
    private int len = 0;
    private int opCode = -1;

    public Message decodeNextByte(byte nextByte) {
        if(len != 2) pushByte(nextByte);
        if(len == 2){
            short opCode = stringToShort(new String(bytes, 0, len, StandardCharsets.UTF_8));
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
        return Short.parseShort(s, 2);
    }

}
