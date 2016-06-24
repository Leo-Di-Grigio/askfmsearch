package main.tools;

public final class AskLog {

    public static void log(String data){
        System.out.print(new StringBuilder("LOG:").append(data).append("\n").toString());
    }
    
    public static void err(String data){
        System.err.print(new StringBuilder("ERR:").append(data).append("\n").toString());
    }
    
    public static void debug(String data){
        System.out.print(new StringBuilder("DEBUG:").append(data).append("\n").toString());
    }
}
