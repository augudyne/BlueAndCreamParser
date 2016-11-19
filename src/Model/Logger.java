package Model;

import java.io.*;

/**
 * Created by Austin on 2016-11-17.
 */
public class Logger {
    private static Logger instance;
    private OutputStream os;
    private OutputStreamWriter osw;

    private Logger(){
        try {
            os = new FileOutputStream("data/errorLogger.txt", true);
            osw = new OutputStreamWriter(os);
        }catch (FileNotFoundException fnf){
            System.out.println("File Not Found for Error Logger");
        }
    }

    public static Logger getInstance(){
        if(instance == null){
            return new Logger();
        } else
            return instance;
    }

    public void write(String s){
        try{
            osw.write(s + '\n');
            osw.flush();
        } catch (IOException e1){
            System.out.println("IO Exception in Logger Instance");
        }

    }
}
