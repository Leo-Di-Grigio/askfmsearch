package main.index;

import java.util.ArrayList;
import java.util.HashMap;

import main.database.Database;
import main.headers.HeaderAnswer;
import main.headers.HeaderUserStats;
import main.tools.AskLog;
import main.tools.Timer;

final class IndexStats {
    
    static void build(){
        AskLog.debug("Index rating: begin building results");
        Timer timer = new Timer();
        timer.start();
                     
        HashMap<String, HeaderUserStats> userHeaders = new HashMap<String, HeaderUserStats>();
            
        processStatistic(userHeaders);
        writeUsersStats(userHeaders);
            
        timer.stop();
        AskLog.debug("Index rating: end in " + timer.getTimeMs() + " ms");
    }

    private static void processStatistic(HashMap<String, HeaderUserStats> userHeaders) {
        ArrayList<HeaderAnswer> answers = null;
        
        for(int page = 0; true; ++page){
            answers = Database.getAnswers(page);
            
            if(answers != null && answers.size() > 0){
                for(HeaderAnswer answer: answers){
                    // get user header
                    HeaderUserStats statsHeader = userHeaders.get(answer.user);
                    
                    if(statsHeader == null){
                        statsHeader = new HeaderUserStats(answer.user);
                        userHeaders.put(answer.user, statsHeader);
                    }
                    
                    // update user data
                    statsHeader.addStats(answer.likes, answer.rating);
                }

                answers = null;
            }
            else{
                break;
            }
        }
    }
    
    private static void writeUsersStats(HashMap<String, HeaderUserStats> userHeaders) {
        if(userHeaders.size() > 0){
            AskLog.debug("Index rating: writing stats");
            for(HeaderUserStats user: userHeaders.values()){
                user.build();
            }
            Database.writeUserRating(userHeaders.values());
        }
    }
}