package main.database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

import main.headers.HeaderAnswer;
import main.tools.AskLog;
import main.tools.Timer;

final class DatabaseAnswers {
    
    private static final String SQL_SELECT_SIZE = "SELECT count(*) FROM answers_in";
    
    private static final String SQL_SELECT_ANSWERS = "SELECT href, id, user, text_question, text_answer, likes_count, answer_ratio, date FROM answers_in WHERE id BETWEEN %positionBegin% AND %positionEnd%";
    private static final String REPLACE_KEY_POSITION_BEGIN = "%positionBegin%";
    private static final String REPLACE_KEY_POSITION_END = "%positionEnd%";
    
    private static final int SQL_SELECT_ANSWERS_PAGE_SIZE = 100000;
    
    private static final String SQL_INSERT_ANSWERS = "INSERT IGNORE INTO answers_in (href, user, text_question, text_answer, likes_count, answer_ratio, date) VALUES ";
    private static final int SQL_INSERT_BATCH_LIMIT = 10000;

    private static final String SQL_SEARCH_ANSWERS = "SELECT href, id, user, text_question, text_answer, likes_count, date FROM answers_in WHERE MATCH(text_answer) AGAINST(?) LIMIT 10";
    
    static int getAnswersSize(Connection connection){
        try {
            Statement statement = connection.createStatement();
            
            int count = 0;
            
            ResultSet resultSet = statement.executeQuery(SQL_SELECT_SIZE);
            if(resultSet.next()){
                count = resultSet.getInt(1);
                return count;
            }
            
            resultSet.close();
            statement.close();
        }
        catch(SQLException e){
            e.printStackTrace();
        }
        
        return 0;
    }
    
    static ArrayList<HeaderAnswer> getAnswers(Connection connection, final int page) {
        if(connection == null || page < 0){
            return null;
        }
        
        try{
            Timer timer = new Timer();
            timer.start();
            
            ArrayList<HeaderAnswer> buffer = new ArrayList<HeaderAnswer>();
            final int PAGE_START_INDEX = page * SQL_SELECT_ANSWERS_PAGE_SIZE; 
        
            Statement statement = connection.createStatement();
            
            //final String sql = SQL_SELECT_ANSWERS.replaceAll(REPLACE_KEY_PAGE_COUNT, "" + SQL_SELECT_ANSWERS_PAGE_SIZE).replaceAll(REPLACE_KEY_PAGE_OFFSET, "" + PAGE_START_INDEX);
            final String sql = SQL_SELECT_ANSWERS.replaceAll(REPLACE_KEY_POSITION_BEGIN, "" + PAGE_START_INDEX)
                                                 .replaceAll(REPLACE_KEY_POSITION_END,   "" + (PAGE_START_INDEX + SQL_SELECT_ANSWERS_PAGE_SIZE));
            
            ResultSet resultSet = statement.executeQuery(sql);
            
            while(resultSet.next()){
                buffer.add(new HeaderAnswer(resultSet.getString(1), resultSet.getInt(2), resultSet.getString(3), resultSet.getString(4), resultSet.getString(5), resultSet.getInt(6), resultSet.getFloat(7), resultSet.getLong(8)));
            }
    
            resultSet.close();
            connection.close();
            
            timer.stop();
            AskLog.debug("(Database) Get answers id > " + page*SQL_SELECT_ANSWERS_PAGE_SIZE + " in " + timer.getTimeMs() + " ms");
            
            return buffer;
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
        
        return null;
    }
    
    public static void writeAnswers(Connection connection, ArrayList<HeaderAnswer> answers) {
        if(connection == null){
            AskLog.err("Connection is null");
            return;
        }
        else if(answers == null){
            AskLog.err("Answers is null");
            return;
        }
        else if(answers.size() == 0){
            return;
        }
            
        try {
            AskLog.debug("(Database) Writings answers. Count: " + answers.size());
            // cycle control values
            int count = Math.min(SQL_INSERT_BATCH_LIMIT, answers.size());
            int index = 0;
            int indexParams = 0;
            
            // begin writing
            PreparedStatement statement = DatabaseTools.getPreparedStatement(connection, SQL_INSERT_ANSWERS, count, HeaderAnswer.PARAMS);
            for(HeaderAnswer answer: answers){
                ++index;
                
                for(int i = 0; i < HeaderAnswer.PARAMS; ++i){
                    statement.setObject(++indexParams, answer.getParam(i));
                }
                
                if(index % SQL_INSERT_BATCH_LIMIT == 0 || index == answers.size()) {
                    AskLog.debug("(Database) Writing indexes: " + index);
                    indexParams = 0;
                    
                    statement.execute();
                    statement.close();
                    
                    count = Math.min(SQL_INSERT_BATCH_LIMIT, answers.size() - index);
                    statement = DatabaseTools.getPreparedStatement(connection, SQL_INSERT_ANSWERS, count, HeaderAnswer.PARAMS);
                }
            }
            
            connection.close();
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    public static ArrayList<HeaderAnswer> searchAnswers(Connection connection, String text){
        if(connection == null || text == null || text.length() == 0){
            return null;
        }
        else {
            ArrayList<HeaderAnswer> list = new ArrayList<HeaderAnswer>();
            
            try {
                PreparedStatement statement = connection.prepareStatement(SQL_SEARCH_ANSWERS);
                statement.setString(1, text);
                
                ResultSet resultSet = statement.executeQuery();
                
                while(resultSet.next()){
                    list.add(new HeaderAnswer(resultSet.getString(1), resultSet.getInt(2), resultSet.getString(3), resultSet.getString(4), resultSet.getString(5), resultSet.getInt(6), 0.0f, resultSet.getLong(7)));
                }
                
                resultSet.close();
                statement.close();
                
                return list;
            }
            catch (SQLException e) {
                e.printStackTrace();
            }
        }
        
        return null;
    }
}