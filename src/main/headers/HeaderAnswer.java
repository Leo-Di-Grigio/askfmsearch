package main.headers;

public final class HeaderAnswer extends Header {
    
    public final String href;
    public final int answerId;
    public final String user;
    public final String textQuestion;
    public final String textAnswer;
    public final int likes;
    public final float rating;
    public final long date;
    
    private static final int TEXT_SIZE_MAX = 3000;

    public HeaderAnswer(String href, int answerId, String user, String textQuestion, String textAnswer, int likes, float rating, long date) {
        this.href = href;
        this.answerId = answerId;
        this.user = user;
        this.textQuestion = textQuestion;
        this.textAnswer = textAnswer;
        this.likes = likes;
        this.rating = calcRating(likes, textAnswer);
        this.date = date;
    }

    public HeaderAnswer(String href, int answerId, String user, String textQuestion, String textAnswer, int likes, long date) {
        this(href, answerId, user, textQuestion, textAnswer, likes, calcRating(likes, textAnswer), date);
    }
    
    private static float calcRating(int likes, String textAnswer) {
        if(likes > 1){
            if(textAnswer.length() > 0){
                final int textLen = textAnswer.length();
                final float rating = (float)textLen/TEXT_SIZE_MAX*(float)Math.log(likes);
                return rating;
            }
            else{
                return (float)Math.log(Math.sqrt(Math.log(likes)));
            }
        }
        else{
            return 0.0f;
        }
    }

    public static final int PARAMS = 7;
    
    @Override
    public Object getParam(int index) {
        switch (index) {
            case 0: return href;
            case 1: return user;
            case 2: return textQuestion;
            case 3: return textAnswer;
            case 4: return likes;
            case 5: return rating;
            case 6: return date;
        }
        
        return null;
    }
}
