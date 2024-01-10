import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.Socket;

public class Main {
    public static void main(String[] args) throws Exception {
        ConnectionHandler handler = new ConnectionHandler(new Socket("0.0.0.0", 1337));
        handler.run();
    }
}
