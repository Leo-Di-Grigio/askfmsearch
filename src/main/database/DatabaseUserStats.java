package main.database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Collection;

import main.headers.HeaderUserStats;
import main.tools.AskLog;

final class DatabaseUserStats {

    private static final String SQL_INSERT_STATS = "INSERT INTO users_stats (user, answers, likes, rating, celebrity_ratio) VALUES ";
    private static final int SQL_INSERT_BATCH_LIMIT = 50000;

    public static void writeStats(Connection connection, Collection<HeaderUserStats> collection) {
        if(connection == null || collection == null || collection.size() == 0){
            return;
        }
        
        try {
            // cycle control values
            int count = Math.min(SQL_INSERT_BATCH_LIMIT, collection.size());
            int index = 0;
            int indexParams = 0;
            
            // begin writing
            PreparedStatement statement = DatabaseTools.getPreparedStatement(connection, SQL_INSERT_STATS, count, HeaderUserStats.PARAMS);
            for(HeaderUserStats header: collection){
                ++index;
                
                for(int i = 0; i < HeaderUserStats.PARAMS; ++i){
                    statement.setObject(++indexParams, header.getParam(i));
                }
                
                if(index % SQL_INSERT_BATCH_LIMIT == 0 || index == collection.size()) {
                    AskLog.debug("(Database) Writing indexes: " + index);
                    indexParams = 0;
                    
                    statement.execute();
                    statement.close();
                    
                    count = Math.min(SQL_INSERT_BATCH_LIMIT, collection.size() - index);
                    statement = DatabaseTools.getPreparedStatement(connection, SQL_INSERT_STATS, count, HeaderUserStats.PARAMS);
                }
            }
            
            connection.close();
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
