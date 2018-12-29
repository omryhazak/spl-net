package bgu.spl.net.srv;

import bgu.spl.net.api.MessageEncoderDecoder;
import bgu.spl.net.api.bidi.BidiMessagingProtocol;
import bgu.spl.net.api.bidi.Messages.Message;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.net.Socket;


public class BlockingConnectionHandler<Message> implements Runnable, ConnectionHandler<Message> {

    private final BidiMessagingProtocol<Message> protocol;
    private final MessageEncoderDecoder<Message> encdec;
    private final Socket sock;
    private BufferedInputStream in;
    private BufferedOutputStream out;
    private volatile boolean connected = true;

    public BlockingConnectionHandler(Socket sock, MessageEncoderDecoder<Message> reader, BidiMessagingProtocol<Message> protocol) {
        this.sock = sock;
        this.encdec = reader;
        this.protocol = protocol;
    }

    @Override
    public void run() {
        try (Socket sock = this.sock) { //just for automatic closing
            int read;

            in = new BufferedInputStream(sock.getInputStream());

            //******************should be here somehow???????*********************
            protocol.start(connectionId,connection);

            while (!protocol.shouldTerminate() && connected && (read = in.read()) >= 0) {
                Message nextMessage = encdec.decodeNextByte((byte) read);
                if (nextMessage != null) {

                    protocol.process(nextMessage);
                }
            }

        } catch (IOException ex) {
            ex.printStackTrace();
        }

    }

    @Override
    public void close() throws IOException {
        connected = false;
        sock.close();
    }

    @Override
    public void send(Message msg) {
        //an ack message needs to be sent here
        //also convert message to string
//        try {
//            out = new BufferedOutputStream(this.sock.getOutputStream());
//            out.write(encdec.encode(msg));
//            out.flush();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }

    }
}
