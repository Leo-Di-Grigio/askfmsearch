package main.index;

import java.util.ArrayList;
import java.util.HashSet;

import main.database.Database;
import main.headers.HeaderAnswer;
import main.tools.AskLog;
import main.tools.Timer;

final class IndexFindUsers {
    
    static void build() {
        HashSet<String> usernames = new HashSet<String>();
        
        AskLog.debug("Find users begin");
        Timer timer = new Timer();
        timer.start();
        
        buildWordsIndex(usernames);
        
        timer.stop();
        AskLog.debug("Founded users written in " + timer.getTimeMs() + " ms");
    }

    private static void buildWordsIndex(HashSet<String> usernames){
        ArrayList<HeaderAnswer> answers = null;

        // get from base and sort
        for(int page = 0; true; ++page){
            answers = Database.getAnswers(page);
            
            if(answers != null && answers.size() > 0){
                wordsProcessPage(usernames, answers);
            }
            else{
                break;
            }
        }
    }
    
    private static void wordsProcessPage(HashSet<String> usernames, ArrayList<HeaderAnswer> list) {
        for(HeaderAnswer answer: list){
            processText(usernames, answer.textQuestion);
            processText(usernames, answer.textAnswer);
        }
        
        if(usernames.size() > 0){
            writeUsers(usernames);
            usernames.clear();
        }
    }

    private static void processText(HashSet<String> usernames, String text) {
        String [] splitedText = text.split(" ");
        
        for(String word: splitedText){
            if(IndexEngineTextCleaner.chechUsername(word)){
                usernames.add(word.replace("@", ""));
            }
        }
    }
    
    private static void writeUsers(HashSet<String> usernames) {
        if(usernames != null && usernames.size() > 0){
            Database.writeUsers(usernames);
        }
    }
}