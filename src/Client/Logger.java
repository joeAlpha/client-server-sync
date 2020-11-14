package Client;

import java.io.*;

public class Logger {
    File log;

    public Logger() {
        log = new File("log.txt");
    }

    public void writeEvent(String event) {
        try (FileWriter fw = new FileWriter(log, true);
             BufferedWriter bw = new BufferedWriter(fw);
             PrintWriter printerOut = new PrintWriter(bw)) {
            printerOut.println(event);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
