package main.index;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

import main.database.Database;
import main.headers.HeaderAnswer;
import main.headers.HeaderWord;
import main.tools.AskLog;
import main.tools.Timer;
import main.tools.Tools;

final class IndexWords {
    
    static void build() {
        HashSet<String> usernames = new HashSet<String>();
        
        Timer timer = new Timer();
        timer.start();
        
        buildWordsIndex(usernames);
        writeUsers(usernames);
        
        timer.stop();
        AskLog.debug("Index words written in " + timer.getTimeMs() + " ms");
    }
    
    private static void writeUsers(HashSet<String> usernames) {
        if(usernames != null && usernames.size() > 0){
            AskLog.debug("Index words: begin writing founded users");
            Database.writeUsers(usernames);
        }
    }

    private static void buildWordsIndex(HashSet<String> usernames){
        HashMap<String, HeaderWord> words = new HashMap<String, HeaderWord>();
        ArrayList<HeaderAnswer> answers = null;

        // get from base and sort
        for(int page = 0; true; ++page){
            answers = Database.getAnswers(page);
            
            if(answers != null && answers.size() > 0){
                wordsProcessPage(words, usernames, answers);
                Database.updateWords(words);
                
                words.clear();
                answers = null;
            }
            else{
                break;
            }
        }
    }

    private static void wordsProcessPage(HashMap<String, HeaderWord> wordsIndex, HashSet<String> usernames, ArrayList<HeaderAnswer> list) {
        for(HeaderAnswer answer: list){
            processText(wordsIndex, usernames, answer.textQuestion, true);
            processText(wordsIndex, usernames, answer.textAnswer, false);
        }
    }

    private static void processText(HashMap<String, HeaderWord> wordsIndex, HashSet<String> usernames, String text, boolean isQuestion) {
        String [] splitedText = text.split(" ");
        
        for(String word: splitedText){
            HeaderWord headerWord = wordsIndex.get(word);
            
            if(headerWord == null){
                headerWord = new HeaderWord();
                
                headerWord.text = word;
                headerWord.textClear = Tools.normalizeText(headerWord.text);
                headerWord.numsClear = IndexEngineTextCleaner.clearNumbers(headerWord.text);
                
                if(IndexEngineTextCleaner.chechUsername(headerWord.text)){
                    usernames.add(headerWord.text.replace("@", ""));
                }
                
                wordsIndex.put(word, headerWord);
            }
            
            ++headerWord.countTotal;
            
            if(isQuestion){
                ++headerWord.countQuestions;
            }
            else{
                ++headerWord.countAnswers;
            }
        }
    }
}
