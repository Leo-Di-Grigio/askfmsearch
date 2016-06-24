package main.config;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

public final class Config {

    private static final String FILE_PATH = "config.cfg";
    private static final String DB_URL_PREFIX = "jdbc:mysql://"; // "jdbc:mysql://localhost/askfm_test"

    private static final String LINE_DB_URL_DEPLOY = "db-deploy:";
    private static final String LINE_USERNAME_DEPLOY = "user-deploy:";
    private static final String LINE_PASSWORD_DEPLOY = "pass-deploy:";
    
    private static final String LINE_DB_URL_TEST = "db-test:";
    private static final String LINE_USERNAME_TEST = "user-test:";
    private static final String LINE_PASSWORD_TEST = "pass-test:";
    
    // deploy
    private static String dbUrlDeploy;
    private static String usernameDeploy;
    private static String passwordDeploy;
    
    // test
    private static String dbUrlTest;
    private static String usernameTest;
    private static String passwordTest;
    
    
    static {
        read();
    }
    
    private static boolean read(){
        File file = new File(FILE_PATH);
        
        if(file.exists()){
            Scanner in;
            try {
                in = new Scanner(file);
                while(in.hasNextLine()) {
                    parseConfigLine(in.nextLine());
                }
                
                in.close();
                return true;
            }
            catch (FileNotFoundException e) {
                e.printStackTrace();
                return false;
            }
        }
        else{
            return false;
        }
    }
    
    private static void parseConfigLine(String nextLine) {
        String [] arr = nextLine.split(" ");
        
        if(arr.length == 2){
            
            switch (arr[0]) {
                case LINE_DB_URL_DEPLOY:
                    Config.dbUrlDeploy = DB_URL_PREFIX + arr[1]; 
                    break;

                case LINE_USERNAME_DEPLOY:
                    Config.usernameDeploy = arr[1];
                    break;
                    
                case LINE_PASSWORD_DEPLOY:
                    Config.passwordDeploy = arr[1];
                    break;

                case LINE_DB_URL_TEST:
                    Config.dbUrlTest = DB_URL_PREFIX + arr[1]; 
                    break;

                case LINE_USERNAME_TEST:
                    Config.usernameTest = arr[1];
                    break;
                    
                case LINE_PASSWORD_TEST:
                    Config.passwordTest = arr[1];
                    break;
                    
                default:
                    break;
            }
        }
    }
    
    public static String getDbUrlDeploy(){
        return dbUrlDeploy;
    }
    
    public static String getUsernameDeploy(){
        return usernameDeploy;
    }
    
    public static String getPasswordDeploy(){
        return passwordDeploy;
    }
    
    public static String getDbUrlTest(){
        return dbUrlTest;
    }
    
    public static String getUsernameTest(){
        return usernameTest;
    }
    
    public static String getPasswordTest(){
        return passwordTest;
    }
}
