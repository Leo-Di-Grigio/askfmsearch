package main.database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import main.headers.HeaderWord;
import main.tools.AskLog;

final class DatabaseWords {
    
    private static final String SQL_INSERT_WORDS = "INSERT INTO words (text, text_clear, nums_clear, count_total, count_questions, count_answers) VALUES ";
    private static final int SQL_INSERT_BATCH_LIMIT = 10000;
    
    private static final String SQL_UPDATE_ON_DUPLICATE = " ON DUPLICATE KEY UPDATE count_total = count_total + VALUES(count_total), count_questions = count_questions + VALUES(count_questions), count_answers = count_answers + VALUES(count_answers)";
    private static final int SQL_INSERT_UPDATE_BATCH_LIMIT = 50000;
    
    public static void writeWords(Connection connection, Map<String, HeaderWord> words){
        if(connection == null || words == null || words.size() == 0){
            return;
        }
        
        try {
            // cycle control values
            int count = Math.min(SQL_INSERT_BATCH_LIMIT, words.size());
            int index = 0;
            int indexParams = 0;
            
            // begin writing
            PreparedStatement statement = DatabaseTools.getPreparedStatement(connection, SQL_INSERT_WORDS, count, HeaderWord.PARAMS);
            for(HeaderWord word: words.values()){
                ++index;
                
                for(int i = 0; i < HeaderWord.PARAMS; ++i){
                    statement.setObject(++indexParams, word.getParam(i));
                }
                
                if(index % SQL_INSERT_BATCH_LIMIT == 0 || index == words.size()) {
                    AskLog.debug("(Database) Writing words: " + index);
                    indexParams = 0;
                
                    statement.execute();
                    statement.close();
                    
                    count = Math.min(SQL_INSERT_BATCH_LIMIT, words.size() - index);
                    statement = DatabaseTools.getPreparedStatement(connection, SQL_INSERT_WORDS, count, HeaderWord.PARAMS);
                }
            }
            
            connection.close();
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    public static void updateWords(Connection connection, HashMap<String, HeaderWord> words){
        if(connection == null || words == null || words.size() == 0){
            return;
        }
        
        try {
            // cycle control values
            int count = Math.min(SQL_INSERT_UPDATE_BATCH_LIMIT, words.size());
            int index = 0;
            int indexParams = 0;
            
            // begin writing
            PreparedStatement statement = DatabaseTools.getPreparedStatementOnDuplicate(connection, SQL_INSERT_WORDS, count, HeaderWord.PARAMS, SQL_UPDATE_ON_DUPLICATE);
            for(HeaderWord word: words.values()){
                ++index;
                
                for(int i = 0; i < HeaderWord.PARAMS; ++i){
                    statement.setObject(++indexParams, word.getParam(i));
                }
                
                if(index % SQL_INSERT_UPDATE_BATCH_LIMIT == 0 || index == words.size()) {
                    AskLog.debug("(Database) Writing words: " + index);
                    indexParams = 0;
                
                    statement.execute();
                    statement.close();
                    
                    count = Math.min(SQL_INSERT_UPDATE_BATCH_LIMIT, words.size() - index);
                    statement = DatabaseTools.getPreparedStatementOnDuplicate(connection, SQL_INSERT_WORDS, count, HeaderWord.PARAMS, SQL_UPDATE_ON_DUPLICATE);
                }
            }
            
            connection.close();
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
    }
}