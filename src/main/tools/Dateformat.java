package main.tools;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class Dateformat {
    
    public static long main(String str) {
        DateFormat format = new SimpleDateFormat("MMM dd, yyyy HH:mm:ss z", Locale.ENGLISH);
        
        try {
            Date date = format.parse(str);
            return date.getTime();
        }
        catch (ParseException e) {
            AskLog.err("ParseError: " + str);
        }
        
        return 0;
    }
}