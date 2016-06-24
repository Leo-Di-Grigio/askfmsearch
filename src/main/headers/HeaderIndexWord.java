package main.headers;

import java.util.ArrayList;

public class HeaderIndexWord extends Header {

    private final String word;
    private final int hash;
    private String indexQuestions;
    private String indexAnswers;
    
    private ArrayList<Integer> indexQuestionsArr;
    private ArrayList<Integer> indexAnswersArr;
    
    public HeaderIndexWord(String word) {
        this.word = word;
        this.hash = word.hashCode();
        
        this.indexQuestionsArr = new ArrayList<Integer>();
        this.indexAnswersArr = new ArrayList<Integer>();
    }
    
    public static final int PARAMS = 6;
    
    @Override
    public Object getParam(final int paramId) {
        switch (paramId) {
            case 0: return hash;
            case 1: return word;
            case 2: return indexQuestionsArr.size();
            case 3: return indexAnswersArr.size();
            case 4: return indexQuestions;
            case 5: return indexAnswers;
        }
        
        return null;
    }

    public void addIndexesQuestion(ArrayList<Integer> answersId) {
        indexQuestionsArr.addAll(answersId);
    }

    public void addIndexesAnswers(ArrayList<Integer> answersId) {
        indexAnswersArr.addAll(answersId);
    }

    public void build() {
        this.indexQuestions = buildIndex(indexQuestionsArr);
        this.indexAnswers = buildIndex(indexAnswersArr);
    }

    private String buildIndex(ArrayList<Integer> indexes) {
        if(indexes.size() > 0){
            StringBuilder builder = new StringBuilder();
            
            for(Integer answerId: indexes){
                builder.append(answerId).append(',');
            }
            
            return builder.toString();    
        }
        else{
            return "";
        }
    } 
}
