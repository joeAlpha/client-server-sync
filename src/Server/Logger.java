package Server;

import java.io.*;

public class Logger {
    File log;

    public Logger() {
        log = new File("src/Server/log/serverLog.txt");
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
