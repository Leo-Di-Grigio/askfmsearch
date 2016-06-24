package main.database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

import main.tools.AskLog;

final class DatabaseUserStatsPages {
    
    private static final String SQL_TABLE_TRUNCATE = "TRUNCATE TABLE user_stats_html";
    
    private static final String SQL_INSERT_PAGES = "INSERT INTO user_stats_html (page, html) VALUES";
    private static final int PARAMS = 2;

    private static final int SQL_INSERT_BATCH_LIMIT = 25;
    
    static void truncate(Connection connection){
        if(connection == null){
            return;
        }
        
        try {
            Statement statement = connection.createStatement();
            statement.executeQuery(SQL_TABLE_TRUNCATE);
            statement.close();
            connection.close();
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    static void writeStats(Connection connection, ArrayList<String> list) {
        if(connection == null || list == null || list.size() == 0){
            return;
        }
        
        try {
            // cycle control values
            int count = Math.min(SQL_INSERT_BATCH_LIMIT, list.size());
            int index = 0;
            int indexParams = 0;
            
            // begin writing
            PreparedStatement statement = DatabaseTools.getPreparedStatement(connection, SQL_INSERT_PAGES, count, PARAMS);
            for(String page: list){
                ++index;
                
                statement.setInt(++indexParams, index);
                statement.setString(++indexParams, page);
                
                if(index % SQL_INSERT_BATCH_LIMIT == 0 || index == list.size()) {
                    AskLog.debug("(Database) Writing indexes: " + index);
                    indexParams = 0;
                    
                    statement.execute();
                    statement.close();
                    
                    count = Math.min(SQL_INSERT_BATCH_LIMIT, list.size() - index);
                    statement = DatabaseTools.getPreparedStatement(connection, SQL_INSERT_PAGES, count, PARAMS);
                }
            }
            
            connection.close();
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
