package Client;

import java.io.*;

public class Logger {
    File log;

    public Logger() {
        log = new File("src/Client/log/log.txt");
    }

    public synchronized void writeEvent(String event) {
        try (FileWriter fw = new FileWriter(log, true);
             BufferedWriter bw = new BufferedWriter(fw);
             PrintWriter printerOut = new PrintWriter(bw)) {
            printerOut.println(event);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
