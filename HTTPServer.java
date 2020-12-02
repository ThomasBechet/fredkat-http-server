import java.net.*;
import java.io.*;
import java.util.*;
import java.util.concurrent.*;

public class HTTPServer {
    public static final int CONNECTION_PORT = 12345;
    public static final String HTTP_VERSION = "1.1";

    public static void main(String[] args) {
        try {
            ServerSocket listener = new ServerSocket(CONNECTION_PORT);
            ExecutorService pool = Executors.newFixedThreadPool(20);

            while (true) {
                Socket newTask = listener.accept();
                pool.execute(new HTTPServerProcess(newTask));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
} 