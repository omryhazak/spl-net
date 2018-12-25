package bgu.spl.net.api;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;

public class MessageEncoderDecoderImpl implements  MessageEncoderDecoder<String> {


    private byte[] bytes = new byte[1024];
    private int len = 0;

    public String decodeNextByte(byte nextByte) {

        return null;
    }


    public byte[] encode(String message){
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
}
