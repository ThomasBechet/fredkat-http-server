import java.net.*;
import java.io.*;
import java.util.*;
import java.util.concurrent.*;

/**
 * HTTP Server creation
 */
public class HTTPServer {
    // Contact server port
    public static final int CONNECTION_PORT = 12345;
    // HTTP version
    public static final String HTTP_VERSION = "1.1";

    public static void main(String[] args) {
        try {
            // Listen on connection port
            ServerSocket listener = new ServerSocket(CONNECTION_PORT);
            // Create a pool of service to handle multiple connections
            ExecutorService pool = Executors.newFixedThreadPool(20);

            // For each connection
            while (true) {
                // Get the socket
                Socket newTask = listener.accept();
                // Start a new process from the socket
                pool.execute(new HTTPServerProcess(newTask));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
} 