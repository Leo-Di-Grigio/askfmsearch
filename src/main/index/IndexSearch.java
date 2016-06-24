package main.index;

import java.util.ArrayList;
import java.util.HashMap;

import main.database.Database;
import main.headers.HeaderAnswer;
import main.headers.HeaderIndexAnswer;
import main.headers.HeaderIndexWord;
import main.tools.Timer;
import main.tools.Tools;

final class IndexSearch {

    static void build(){
        ArrayList<HeaderAnswer> answers = null;
        ArrayList<HeaderIndexAnswer> indexAnswers = new ArrayList<HeaderIndexAnswer>();
        ArrayList<HeaderIndexWord> indexWords = new ArrayList<HeaderIndexWord>();
        
        // get from base and sort
        Timer timer = new Timer();
        for(int page = 0; true; ++page){
            answers = Database.getAnswers(page);
            
            if(answers != null && answers.size() > 0){
                processAnswers(answers, indexAnswers, indexWords);
                
                timer.start();
                Database.writeIndexAnswers(indexAnswers);
                timer.stop();
                System.out.println("Answers writed in " + timer.getTimeMs() + " ms");
                
                timer.start();
                Database.writeIndexWords(indexWords);
                timer.stop();
                System.out.println("Words writed in " + timer.getTimeMs() + " ms");
                
                indexAnswers.clear();
                indexWords.clear();
                answers = null;
            }
            else{
                break;
            }
        } 
    }

    private static void processAnswers(ArrayList<HeaderAnswer> answers, ArrayList<HeaderIndexAnswer> indexAnswers, ArrayList<HeaderIndexWord> indexWords) {
        HashMap<String, ArrayList<Integer>> wordsQuestion = new HashMap<String, ArrayList<Integer>>();
        HashMap<String, ArrayList<Integer>> wordsAnswer = new HashMap<String, ArrayList<Integer>>();
        
        for(HeaderAnswer answer: answers){
            HashMap<String, ArrayList<Integer>> indexAnswer = buildIndex(answer.textAnswer);

            if(indexAnswer.size() > 0){
                HashMap<String, ArrayList<Integer>> indexQuestion = buildIndex(answer.textQuestion);
                
                // answer
                indexAnswers.add(new HeaderIndexAnswer(answer.answerId, indexQuestion.size(), indexAnswer.size(), Tools.buildInvertedIndex(indexQuestion), Tools.buildInvertedIndex(indexAnswer)));
                
                writeWords(wordsQuestion, indexQuestion, answer.answerId);
                writeWords(wordsAnswer, indexAnswer, answer.answerId);
            }            
        }
        
        buildIndexWords(indexWords, wordsQuestion, wordsAnswer);
    }

    private static void writeWords(HashMap<String, ArrayList<Integer>> words, HashMap<String, ArrayList<Integer>> index, final int answerId) {
        if(index.size() > 0){
            for(String word: index.keySet()){
                ArrayList<Integer> answersId = words.get(word);
                
                if(answersId == null){
                    answersId = new ArrayList<Integer>();
                    words.put(word, answersId);
                }
                
                answersId.add(answerId);
            }   
        }
    }
    
    private static void buildIndexWords(ArrayList<HeaderIndexWord> indexWords, HashMap<String, ArrayList<Integer>> wordsQuestion, HashMap<String, ArrayList<Integer>> wordsAnswer) {
        HashMap<String, HeaderIndexWord> words = new HashMap<String, HeaderIndexWord>();
        
        for(String word: wordsQuestion.keySet()){
            HeaderIndexWord wordHeader = words.get(word);
            
            if(wordHeader == null){
                wordHeader = new HeaderIndexWord(word);
                words.put(word, wordHeader);
            }
            
            ArrayList<Integer> answersId = wordsQuestion.get(word);
            
            if(answersId != null){
                wordHeader.addIndexesQuestion(answersId);
            }
        }
        
        for(String word: wordsAnswer.keySet()){
            HeaderIndexWord wordHeader = words.get(word);
            
            if(wordHeader == null){
                wordHeader = new HeaderIndexWord(word);
                words.put(word, wordHeader);
            }
            
            ArrayList<Integer> answersId = wordsAnswer.get(word);
            
            if(answersId != null){
                wordHeader.addIndexesAnswers(answersId);
            }
        }
        
        for(HeaderIndexWord headerWord: words.values()){
            headerWord.build();
            indexWords.add(headerWord);
        }
    }

    private static HashMap<String, ArrayList<Integer>> buildIndex(String text){
        HashMap<String, ArrayList<Integer>> index = new HashMap<String, ArrayList<Integer>>();
        
        String [] words = text.split(" ");
        
        int wordPosition = 0;
        for(final String word: words){
            String normalWord = Tools.normalizeText(word);
            
            if(!normalWord.equals("")){
                ArrayList<Integer> positions = index.get(normalWord);
                
                if(positions == null){
                    positions = new ArrayList<Integer>();
                    index.put(normalWord, positions);
                }
                
                positions.add(wordPosition);
                ++wordPosition;
            }
        }
        
        return index;
    }
}