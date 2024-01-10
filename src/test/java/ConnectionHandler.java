import lombok.Data;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public @Data class ConnectionHandler implements Runnable {
    private Socket socket;

    private List<String> receivedPackets = new CopyOnWriteArrayList<>();

    public ConnectionHandler(Socket socket) {
        this.socket = socket;
    }

    @Override
    public void run() {
        try {
            while (true) {
                DataInputStream input = new DataInputStream(socket.getInputStream());
                int length = input.readInt();
                byte[] message = new byte[length];
                input.readFully(message, 0, message.length);
                String lastReceived = new String(message);
                System.out.println(lastReceived);
                if (!lastReceived.contains("Enter")) receivedPackets.add(lastReceived);
                else {
                    System.out.println(receivedPackets.get(receivedPackets.indexOf(lastReceived) - 1));
                    receivedPackets.clear();
                    break;
                }
            }
        } catch (IOException ignored) {
        }
    }
}
