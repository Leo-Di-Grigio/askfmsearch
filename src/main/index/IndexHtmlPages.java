package main.index;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

import main.database.Database;
import main.tools.AskLog;
import main.tools.Timer;

final class IndexHtmlPages {

    private static final int PAGE_SIZE = 100;
    
    private static final String HTML_TABLE = "<table class=\"table\"><tr><td>#</td><td>Пользователь</td><td>Рейтинг</td><td>КСА</td><td>Ответы</td><td>Понравилось</td></tr>%table%</table>";
    private static final String HTML_ROW = "<tr><td>%value_index%</td><td><a href=\"https://ask.fm/%value_user_name%\" target=\"_blank\">@%value_user_name%</a></td><td>%value_ratio%</td><td>%value_celebrity_ratio%</td><td>%value_answers%</td><td>%value_likes%</td></tr>";
    
    private static final String KEY_TABLE = "%table%";
    private static final String KEY_VALUE_INDEX = "%value_index%";
    private static final String KEY_USER_NAME = "%value_user_name%";
    private static final String KEY_USER_RATIO = "%value_ratio%";
    private static final String KEY_CELEBRITY_RATIO = "%value_celebrity_ratio%";
    private static final String KEY_ANSWERS = "%value_answers%";
    private static final String KEY_LIKES = "%value_likes%";
    
    private static final String SQL_SELECT_SIZE = "SELECT count(*) FROM users_stats";
    private static final String SQL_SELECT = "SELECT user, answers, likes, rating, celebrity_ratio FROM users_stats ORDER BY rating DESC LIMIT ";
    
    static void build() {
        buildPages();
    }

    private static void buildPages() {
        Connection connection = Database.getConnection();
        String htmlTable = "";
        
        if(connection == null){
            AskLog.err("Index rating: no database connection");
        }
        else{
            try {
                Timer timer = new Timer();
                timer.start();
                AskLog.debug("Index rating: begin building results");
                
                // read
                Statement statement = connection.createStatement();
                
                // get users table size
                int usersCount = 0;
                {
                    ResultSet resultSet = statement.executeQuery(SQL_SELECT_SIZE);
                    if(resultSet.next()){
                        usersCount = resultSet.getInt(1);
                    }
                    resultSet.close();
                }
                
                // build pages
                ArrayList<String> list = new ArrayList<String>();
                {
                    int pagesTotal = usersCount/PAGE_SIZE;
                    if(usersCount % PAGE_SIZE > 0){
                        ++pagesTotal;
                    }
                    
                    for(int i = 0, userIndex = 0; i < pagesTotal; ++i){
                        AskLog.debug("page " + i + " \\ " + pagesTotal);
                        ResultSet resultSet = statement.executeQuery(SQL_SELECT + i*PAGE_SIZE + ", " + PAGE_SIZE);
                        
                        StringBuilder htmlTableBuilder = new StringBuilder();
                        
                        while(resultSet.next()){
                            ++userIndex;                       
                            htmlTableBuilder.append(HTML_ROW.replaceAll(KEY_VALUE_INDEX, "" + userIndex)
                                                            .replaceAll(KEY_USER_NAME, "" + resultSet.getString(1))
                                                            .replaceAll(KEY_USER_RATIO, "" + resultSet.getFloat(4))
                                                            .replaceAll(KEY_CELEBRITY_RATIO, "" + resultSet.getFloat(5))
                                                            .replaceAll(KEY_ANSWERS, "" + resultSet.getInt(2))
                                                            .replaceAll(KEY_LIKES, "" + resultSet.getInt(3)));
                        }
                        resultSet.close();
                        
                        htmlTable = HTML_TABLE.replaceAll(KEY_TABLE, htmlTableBuilder.toString());
                        list.add(htmlTable);
                    }
                    
                    statement.close();   
                }
                connection.close();
                
                // write
                Database.writeUserStatsPages(list);
                timer.stop();
                AskLog.debug("Index rating: end in " + timer.getTimeMs() + " ms");
            }
            catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}
