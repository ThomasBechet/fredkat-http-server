import java.util.*;
import java.io.*;

public class HTTPRequestObject {
    public static enum Type {
        GET,
        POST,
        PUT,
        DELETE,
        TRACE,
        UPDATE,
        UNDEFINED
    }

    private Type type;
    private String queryString;
    private String version;
    private Map<String, String> headers;

    public HTTPRequestObject(BufferedReader reader) throws IOException {
        this.headers = new HashMap<String, String>();
        
        String line;
        
        // Request
        String[] requestTokens = reader.readLine().split(" ");
        String type = requestTokens[0].trim();
        if (type.equals("GET")) {
            this.type = Type.GET;
        } else if (type.equals("POST")) {
            this.type = Type.POST;
        } else {
            this.type = Type.UNDEFINED;
        }
        this.queryString = requestTokens[1].trim();
        this.version = requestTokens[2].trim();

        // Headers
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