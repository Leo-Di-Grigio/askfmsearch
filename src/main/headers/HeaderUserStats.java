package main.headers;

import main.interfaces.Parametric;

public final class HeaderUserStats implements Parametric {

    private final String user;
    private int totalAnswers;
    private int totalLikes;
    private float totalRating;
    private float celebrityRatio;
    
    public HeaderUserStats(String user) {
        this.user = user;
    }

    public void addStats(int likesCount, float rating) {
        ++this.totalAnswers;
        this.totalLikes += likesCount;
        this.totalRating += rating;
    }
    
    public static final int PARAMS = 5;
    
    @Override
    public Object getParam(int index) {
        switch (index) {
            case 0: return user;
            case 1: return totalAnswers;
            case 2: return totalLikes;
            case 3: return totalRating;
            case 4: return celebrityRatio;
        }
        
        return null;
    }

    public void build() {
        if(this.totalAnswers > 0 && this.totalLikes > 0){
            this.celebrityRatio = (float)this.totalLikes/(float)this.totalAnswers;
        }
    }
}
