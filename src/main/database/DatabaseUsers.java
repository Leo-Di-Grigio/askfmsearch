package main.database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashSet;

import main.headers.HeaderUser;
import main.tools.AskLog;

final class DatabaseUsers {
    
    private static final String SQL_INSERT = "INSERT IGNORE INTO users (name) VALUES(?)";
    private static final String SQL_INSERT_ARRAY = "INSERT IGNORE INTO users (name) VALUES ";
    
    private static final String SQL_SELECT_ALL_USERNAMES = "SELECT name, last_date, rus FROM users";
    
    private static final String SQL_UPDATE_LAST_DATE = "UPDATE users SET last_date = ? WHERE name = ? LIMIT 1";
    private static final String SQL_UPDATE_LOCAL_RUS = "UPDATE users SET rus = ? WHERE name = ? LIMIT 1";
    
    private static final int SQL_INSERT_BATCH_LIMIT = 100000;
    
    static void writeUser(Connection connection, String username){
        if(connection == null){
            return;
        }
        
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(SQL_INSERT);            
            preparedStatement.setString(1, username);
            
            preparedStatement.execute();
            preparedStatement.close();
            
            connection.close();
            AskLog.log("(Database) added user \"" + username + "\"");
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    static void writeUsers(Connection connection, HashSet<String> users) {
        if(connection == null || users == null || users.size() == 0){
            return;
        }
        
        try {
            // cycle control values
            int count = Math.min(SQL_INSERT_BATCH_LIMIT, users.size());
            int index = 0;
            int indexParams = 0;
            
            // begin writing
            PreparedStatement statement = DatabaseTools.getPreparedStatement(connection, SQL_INSERT_ARRAY, count, 1);
            
            for(String name: users){
                ++index;
                statement.setString(++indexParams, name);
                
                if(index % SQL_INSERT_BATCH_LIMIT == 0 || index == users.size()) {
                    AskLog.debug("(Database) Writing users: " + index);
                    indexParams = 0;
                
                    statement.execute();
                    statement.close();
                    
                    count = Math.min(SQL_INSERT_BATCH_LIMIT, users.size() - index);
                    statement = DatabaseTools.getPreparedStatement(connection, SQL_INSERT_ARRAY, count, 1);
                }
            }

            connection.close();
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    static ArrayList<HeaderUser> getUsersAll(Connection connection) {
        if(connection == null){
            return null;
        }
        
        try {
            ArrayList<HeaderUser> users = new ArrayList<HeaderUser>();
            
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(SQL_SELECT_ALL_USERNAMES);
            
            while(resultSet.next()){
                users.add(new HeaderUser(resultSet.getString(1), resultSet.getLong(2), resultSet.getBoolean(3)));
            }
            
            resultSet.close();
            statement.close();
            connection.close();
            
            AskLog.debug("Loaded " + users.size() + " users");
            return users;
        }
        catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }
    
    static void updateDate(Connection connection, HeaderUser user, long maxDate){
        if(connection == null || user == null){
            return;
        }
        
        try {
            PreparedStatement statement = connection.prepareStatement(SQL_UPDATE_LAST_DATE);            
            statement.setLong(1, maxDate);
            statement.setString(2, user.name);
            
            statement.executeUpdate();
            statement.close();
            
            connection.close();
            AskLog.debug("User \"" + user.name + "\" last_date is updated");
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    static void updateRus(Connection connection, HeaderUser user, boolean rus){
        if(connection == null || user == null){
            return;
        }
        
        try {
            PreparedStatement statement = connection.prepareStatement(SQL_UPDATE_LOCAL_RUS);            
            statement.setBoolean(1, rus);
            statement.setString(2, user.name);
            
            statement.executeUpdate();
            statement.close();
            
            connection.close();
            AskLog.debug("User \"" + user.name + "\" last_date is updated");
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
