import java.util.*;
import java.io.*;

/**
 * Class responsible for building the HTTP Client request
 */
public class HTTPRequestObject {

    /**
     * Available http request type
     */
    public static enum Type {
        GET,
        POST,
        PUT,
        DELETE,
        TRACE,
        UPDATE,
        UNDEFINED
    }

    private Type type; // Request type
    private String queryString; // Request query string
    private String version; // Request version
    private Map<String, String> headers; // Request headers

    public HTTPRequestObject(BufferedReader reader) throws IOException {
        this.headers = new HashMap<String, String>();
        
        String line;
        
        // Split first line tokens
        String[] requestTokens = reader.readLine().split(" ");
        
        // Read the request type
        String type = requestTokens[0].trim();
        if (type.equals("GET")) {
            this.type = Type.GET;
        } else if (type.equals("POST")) {
            this.type = Type.POST;
        } else {
            this.type = Type.UNDEFINED;
        }
        // Read the query string
        this.queryString = requestTokens[1].trim();
        // Read the request version
        this.version = requestTokens[2].trim();

        // Read headers (put in the map)
        List<String> headers = new ArrayList<String>();
        while ((line = reader.readLine()).length() > 0) {
            String[] tokens = line.trim().split(":");
            this.headers.put(tokens[0], tokens[1].trim());
        }
    }

    public Type getType() {
        return this.type;
    }
    public String getQueryString() {
        return this.queryString;
    }
    public String getHeader(String name) {
        return this.headers.get(name);
    }
}