package main.headers;

import main.interfaces.Parametric;

public final class HeaderWord implements Parametric {
    
    public String text;
    public String textClear;
    public String numsClear;
    public long countTotal;
    public int countQuestions;
    public int countAnswers;

    public static final int PARAMS = 6;
    @Override
    public Object getParam(int index) {
        
        switch (index) {
            case 0: return text;
            case 1: return textClear;
            case 2: return numsClear;
            case 3: return countTotal;
            case 4: return countQuestions;
            case 5: return countAnswers;
        }
        
        return null;
    }
}
