package main.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;

import main.config.Config;
import main.headers.HeaderAnswer;
import main.headers.HeaderIndexAnswer;
import main.headers.HeaderIndexWord;
import main.headers.HeaderUserStats;
import main.headers.HeaderUser;
import main.headers.HeaderWord;
import main.tools.AskLog;

public final class Database {
    
    public static Connection getConnection() {
        Connection connection = null;
        
        try {
            Class.forName("com.mysql.jdbc.Driver").newInstance();
        }
        catch (InstantiationException e) {
            e.printStackTrace();
        }
        catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        
        try {
            connection = DriverManager.getConnection(Config.getDbUrlDeploy(), Config.getUsernameDeploy(), Config.getPasswordDeploy());
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
        
        return connection;
    }

    public static void writeUser(String username){
        Connection connection = getConnection();
        
        if(connection == null){
            AskLog.err("Database: connection is null");
        }
        else{
            DatabaseUsers.writeUser(connection, username);
        }
    }
    
    public static void writeUsers(HashSet<String> users) {
        Connection connection = getConnection();
        
        if(connection == null){
            AskLog.err("Database: connection is null");
        }
        else{
            DatabaseUsers.writeUsers(connection, users);
        }
    }
    
    public static ArrayList<HeaderUser> getUsersAll() {
        Connection connection = getConnection();
        
        if(connection == null){
            AskLog.err("Database: connection is null");
            return null;
        }
        else{
            return DatabaseUsers.getUsersAll(connection);
        }
    }

    public static void updateUserLastDate(HeaderUser user, long maxDate) {
        Connection connection = getConnection();
        
        if(connection == null){
            AskLog.err("Database: connection is null");
        }
        else{
            DatabaseUsers.updateDate(connection, user, maxDate);
        }
    }
    
    public static void updateUserRus(HeaderUser user, boolean rus) {
        Connection connection = getConnection();
        
        if(connection == null){
            AskLog.err("Database: connection is null");
        }
        else{
            DatabaseUsers.updateRus(connection, user, rus);
        }
    }
    
    public static int getAnswersSize(){
        Connection connection = getConnection();
        
        if(connection == null){
            AskLog.err("Database: connection is null");
            return 0;
        }
        else{
            return DatabaseAnswers.getAnswersSize(connection);
        }        
    }
    
    public static ArrayList<HeaderAnswer> getAnswers(final int page){
        Connection connection = getConnection();
        
        if(connection == null){
            AskLog.err("Database: connection is null");
            return null;
        }
        else{
            return DatabaseAnswers.getAnswers(connection, page);
        }
    }

    public static void writeAnswers(ArrayList<HeaderAnswer> answers) {
        Connection connection = getConnection();
        
        if(connection == null){
            AskLog.err("Database: connection is null");
        }
        else{
            DatabaseAnswers.writeAnswers(connection, answers);
        }
    }

    public static void writeWords(HashMap<String, HeaderWord> words) {
        Connection connection = getConnection();
        
        if(connection == null){
            AskLog.err("Database: connection is null");
        }
        else{
            DatabaseWords.writeWords(connection, words);
        }
    }

    public static void updateWords(HashMap<String, HeaderWord> words) {
        Connection connection = getConnection();
        
        if(connection == null){
            AskLog.err("Database: connection is null");
        }
        else{
            DatabaseWords.updateWords(connection, words);
        }
    }
    
    public static void writeUserRating(Collection<HeaderUserStats> collection) {
        Connection connection = getConnection();
        
        if(connection == null){
            AskLog.err("Database: connection is null");
        }
        else{
            DatabaseUserStats.writeStats(connection, collection);
        }
    }

    public static void writeUserStatsPages(ArrayList<String> list) {
        Connection connection = getConnection();
        
        if(connection == null){
            AskLog.err("Database: connection is null");
        }
        else{
            DatabaseUserStatsPages.writeStats(connection, list);
        }
    }
    
    public static void writeIndexAnswers(ArrayList<HeaderIndexAnswer> arr) {
        Connection connection = getConnection();
        
        if(connection == null){
            AskLog.err("Database: connection is null");
        }
        else{
            DatabaseIndexAnswers.writeArray(connection, arr);
        }
    }

    public static void writeIndexWords(ArrayList<HeaderIndexWord> arr) {
        Connection connection = getConnection();
        
        if(connection == null){
            AskLog.err("Database: connection is null");
        }
        else{
            DatabaseIndexWords.writeArray(connection, arr);
        }
    }

    public static ArrayList<HeaderAnswer> searchAnswers(String text) {
        Connection connection = getConnection();
        
        if(connection == null){
            AskLog.err("Database: connection is null");
            return null;
        }
        else{
            return DatabaseAnswers.searchAnswers(connection, text);
        }
    }
}