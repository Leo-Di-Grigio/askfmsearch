package main.database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;

import main.headers.HeaderIndexWord;
import main.tools.AskLog;

final class DatabaseIndexWords {
    
    private static final String SQL_INSERT_ARRAY = "INSERT INTO index_words_copy (hash, word, index_size_question, index_size_answer, index_question, index_answer) VALUES ";
    
    private static final String SQL_UPDATE_ON_DUPLICATE = " ON DUPLICATE KEY UPDATE "
                                                        + "index_size_question = index_size_question + VALUES(index_size_question), "
                                                        + "index_size_answer = index_size_answer + VALUES(index_size_answer), "
                                                        + "index_question = CONCAT(index_question, VALUES(index_question)), "
                                                        + "index_answer = CONCAT(index_answer, VALUES(index_answer))";
    
    private static final int SQL_INSERT_UPDATE_BATCH_LIMIT = 1000;
    
    public static void writeArray(Connection connection, ArrayList<HeaderIndexWord> arr) {
        if(connection == null || arr == null || arr.size() == 0){
            return;
        }
        
        try {
            // cycle control
            int count = Math.min(SQL_INSERT_UPDATE_BATCH_LIMIT, arr.size());
            int index = 0;
            int indexParams = 0;
            
            // begin writing
            PreparedStatement statement = DatabaseTools.getPreparedStatementOnDuplicate(connection, SQL_INSERT_ARRAY, count, HeaderIndexWord.PARAMS, SQL_UPDATE_ON_DUPLICATE);
            for(HeaderIndexWord word: arr){
                ++index;
                
                for(int i = 0; i < HeaderIndexWord.PARAMS; ++i){
                    statement.setObject(++indexParams, word.getParam(i));
                }
                
                if(index % SQL_INSERT_UPDATE_BATCH_LIMIT == 0 || index == arr.size()) {
                    AskLog.debug("(Database) Writing words index: " + index);
                    indexParams = 0;
                
                    statement.execute();
                    statement.close();
                    
                    count = Math.min(SQL_INSERT_UPDATE_BATCH_LIMIT, arr.size() - index);
                    statement = DatabaseTools.getPreparedStatementOnDuplicate(connection, SQL_INSERT_ARRAY, count, HeaderIndexWord.PARAMS, SQL_UPDATE_ON_DUPLICATE);
                }
            }

            connection.close();
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
