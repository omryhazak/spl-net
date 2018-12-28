package bgu.spl.net.api.bidi.Messages;

import bgu.spl.net.api.bidi.AllUsers;

import java.util.LinkedList;

public class PostMessage extends Message {

    private String content;
    private LinkedList<String> usersNameToSend;
    private int opCode =  5;

    public PostMessage(String message) {

        this.usersNameToSend = new LinkedList<>();

        String toParse = message;
        toParse = toParse.substring(2);
        this.content = toParse.substring(0, toParse.length()-1);
        if(toParse.indexOf('@')==-1){
            this.usersNameToSend = null;
        }
        else{
            StringBuffer buff = new StringBuffer(toParse);
            String tmp = toParse;
            while (buff.indexOf("@") != -1){
                String toAdd ;
                int i =  buff.indexOf("@")+1;
                int j = buff.indexOf("@")+1;
                while (tmp.charAt(j) != ' '){
                    j++;
                }
                toAdd = tmp.substring(i,j);
                this.usersNameToSend.add(toAdd);
            }


        }

    }

    public String getContent() {
        return content;
    }

    public LinkedList<String> getUsersNameToSend() {
        return usersNameToSend;
    }

    @Override
    public Boolean process(int connectId, AllUsers allUsers) {

    }
}
