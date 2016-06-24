package main.headers;

public class HeaderIndexAnswer extends Header {

    public final int answersId;
    public final int indexSizeQuestion;
    public final int indexSizeAnswer;
    public final String indexQuestion;
    public final String indexAnswer;

    public HeaderIndexAnswer(int answersId, int indexSizeQuestion, int indexSizeAnswer, String indexQuestion, String indexAnswer) {
        this.answersId = answersId;
        this.indexSizeQuestion = indexSizeQuestion;
        this.indexSizeAnswer = indexSizeAnswer;
        this.indexQuestion = indexQuestion;
        this.indexAnswer = indexAnswer;
    }
    
    public static final int PARAMS = 5;
    
    @Override
    public Object getParam(int id) {
        switch (id) {
            case 0: return answersId;
            case 1: return indexSizeQuestion;
            case 2: return indexSizeAnswer;
            case 3: return indexQuestion;
            case 4: return indexAnswer;

            default:
                break;
        } 
        return null;
    }
}