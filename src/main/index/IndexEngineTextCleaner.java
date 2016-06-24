package main.index;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

class IndexEngineTextCleaner {
    
    private static final String USERNAME_PATTERN = "^[a-zA-Z0-9_]{3,40}$";
    
    static String clearNumbers(String word){
        return word.replaceAll("[^0-9]", "");
    }

    public static boolean chechUsername(String text) {
        if(text.startsWith("@")){
            text = text.replace("@", "");
            return validateUsername(text);
        }
        else {
            return false;
        }
    }
    
    private static boolean validateUsername(String username) {
        Pattern pattern = Pattern.compile(USERNAME_PATTERN);
        Matcher matcher = pattern.matcher(username);
        return matcher.matches();
    }
}