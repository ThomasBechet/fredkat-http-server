import java.util.*;
import java.net.*;
import java.io.*;

public class Utility {
    /**
     * Utility function charge of loading bytes from a file
     */
    public static byte[] readFile(String path) throws IOException {
        File file = new File(path);
        FileInputStream fis = new FileInputStream(file);
        byte[] data = new byte[(int) file.length()];
        fis.read(data);
        fis.close();
        return data;
    }
}