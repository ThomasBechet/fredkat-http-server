import java.net.*;
import java.io.*;
import java.nio.file.*;
import java.util.*;
import java.util.stream.*;

public class HTTPServerProcess implements Runnable {
    private Socket socket;

    // Error html files URI (same uri for both servers)
    private static String HTML_ERROR_400_URI = "400.html";
    private static String HTML_ERROR_404_URI = "404.html";
    private static String HTML_ERROR_500_URI = "500.html";

    // Map between a host and its server root directory
    private static final Map<String, String> serverRoots = Map.of(
        "frederik.localhost", "frederikServer",
        "kristine.localhost", "kristineServer"
    );

    private static String buildURI(String query) throws UnsupportedEncodingException {
        // Decode query
        String decodedQuery = URLDecoder.decode(query, "UTF-8"); // Decide %xx characters
        decodedQuery = decodedQuery.split("\\?")[0]; // Remove parameters

        // Redirect on index.html if necessary
        File f = new File(decodedQuery);
        if (f.isDirectory()) {
            return "index.html";
        } else {
            return f.getPath();
        }
    }

    private static String buildPath(String uri, String host) {
        // Get the current directory
        Path currentRelativePath = Paths.get("");
        String currentPath = currentRelativePath.toAbsolutePath().toString();
    
        // Get the server directory
        String root = serverRoots.get(host);

        // Create the final path
        return currentPath + "/" + root + "/" + uri;
    }
    
    public HTTPServerProcess(Socket socket) {
        this.socket = socket;
    }

    @Override
    public void run() {
        try {
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            DataOutputStream out = new DataOutputStream(socket.getOutputStream());
        
            // Recover request
            HTTPRequestObject request = null;
            try {
                request = new HTTPRequestObject(in);
            } catch(IOException e) {
                System.out.println("Failed to build request object.");
                return;
            }

            // Get request info
            String host = request.getHeader("Host");
            String uri = buildURI(request.getQueryString());
            String path = buildPath(uri, host);

            System.out.println(host + " : " + path);

            // Check request method (only GET supported)
            if (request.getType() == HTTPRequestObject.Type.GET) {

                try {

                    try {

                        // TEST ONLY : simulate internal server error
                        if (new File(path).getName().equals("internal-server-error")) {
                            throw new InterruptedException("Internal Server Error.");
                        } else {
                            // OK
                            HTTPResponseObject response = new HTTPResponseObject(path, host);
                            out.write(response.getResponse());
                        }

                    } catch(InterruptedException exception) {

                        // Internal server error
                        try {
                            HTTPResponseObject response = new HTTPResponseObject(
                                HTTPResponseObject.Status.INTERNAL_SERVER_ERROR,
                                buildPath(HTML_ERROR_500_URI, host),
                                host);
                            out.write(response.getResponse());
                        } catch(IOException e2) {
                            System.out.println("Failed to read 500 html file.");
                        }

                    }

                } catch(IOException e) {

                    //File Not found
                    try {
                        HTTPResponseObject response = new HTTPResponseObject(
                            HTTPResponseObject.Status.NOT_FOUND, 
                            buildPath(HTML_ERROR_404_URI, host),
                            host);
                        out.write(response.getResponse());
                    } catch(IOException e2) {
                        System.out.println("Failed to read 404 html file.");
                    }

                }

            } else {

                // Bad request
                try {
                    HTTPResponseObject response = new HTTPResponseObject(
                        HTTPResponseObject.Status.BAD_REQUEST,
                        buildPath(HTML_ERROR_400_URI, host),
                        host);
                    out.write(response.getResponse());
                } catch(IOException e2) {
                    System.out.println("Failed to read 400 html file.");
                }

            }
        
            out.flush();
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}