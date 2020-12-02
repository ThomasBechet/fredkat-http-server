import java.util.*;
import java.io.*;
import java.text.SimpleDateFormat;
import java.nio.file.Files;

public class HTTPResponseObject {

    public static enum Status {
        OK,
        NOT_FOUND,
        BAD_REQUEST,
        METHOD_NOT_ALLOWED,
        INTERNAL_SERVER_ERROR
    }

    private static Map<Status, String> messages = Map.of(
        Status.OK, "OK",
        Status.NOT_FOUND, "NOT FOUND",
        Status.BAD_REQUEST, "BAD REQUEST",
        Status.METHOD_NOT_ALLOWED, "METHOD NOT ALLOWED",
        Status.INTERNAL_SERVER_ERROR, "INTERNAL_SERVER_ERROR"
    );
    private static Map<Status, Integer> codes = Map.of(
        Status.OK, 200,
        Status.NOT_FOUND, 404,
        Status.BAD_REQUEST, 400,
        Status.METHOD_NOT_ALLOWED, 405,
        Status.INTERNAL_SERVER_ERROR, 500
    );

    private Status status;
    private String serverName;
    private String contentType;
    private byte[] body;
    private byte[] response;

    private void generateResponse() {
        String headerResponse;
        headerResponse = "HTTP/" + HTTPServer.HTTP_VERSION + " " + codes.get(this.status) + " " + messages.get(this.status) + "\n";
        headerResponse += "Date:" + new SimpleDateFormat("E, d MMM y HH:mm:ss z").format(new Date()) + "\n";
        headerResponse += "Server:" + this.serverName + "\n";
        headerResponse += "Connection:close" + "\n";
        headerResponse += "Content-Length:" + this.body.length + "\n";
        if (this.contentType != null) {
            headerResponse += "Content-Type:" + this.contentType + "\n";
        } else {
            headerResponse += "Content-Type:*/*" + "\n"; // On autorise tous les types de retour
        }
        headerResponse += "\n";

        if (this.body != null) {
            // Build final request with the body included
            String r = "\n\n";
            byte[] b = headerResponse.getBytes();
            this.response = new byte[b.length + this.body.length + r.length()];
            System.arraycopy(b, 0, this.response, 0, b.length);
            System.arraycopy(this.body, 0, this.response, b.length, this.body.length);
            System.arraycopy(r.getBytes(), 0, this.response, b.length + this.body.length, r.length());
        } else {
            // Build the final request without the body
            String r = "\n\n";
            byte[] b = headerResponse.getBytes();
            this.response = new byte[b.length + r.length()];
            System.arraycopy(b, 0, this.response, 0, b.length);
            System.arraycopy(r.getBytes(), 0, this.response, b.length, r.length());
        }
    }
    
    public HTTPResponseObject(String path, String serverName) throws IOException {
        this.body = Utility.readFile(path);
        this.status = Status.OK;
        this.contentType = Files.probeContentType(new File(path).toPath());
        this.serverName = serverName;
        this.generateResponse();
    }
    public HTTPResponseObject(Status status, String serverName) {
        this.body = null;
        this.contentType = null;
        this.status = status;
        this.serverName = serverName;
        this.generateResponse();
    }
    public HTTPResponseObject(Status status, String path, String serverName) throws IOException {
        this.body = Utility.readFile(path);
        this.contentType = Files.probeContentType(new File(path).toPath());
        this.status = status;
        this.serverName = serverName;
        this.generateResponse();
    }

    public byte[] getResponse() {
        return this.response;
    }
}