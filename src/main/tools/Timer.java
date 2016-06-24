package main.tools;

public class Timer {

    private static final long MS_IN_SEC = 1000;
    
    private long startTime;
    private long timeSpent;

    public Timer() {
        
    }
    
    public void start(){
        startTime = System.currentTimeMillis();
    }
    
    public void stop(){
        timeSpent = System.currentTimeMillis() - startTime;
    }
    
    public long getTimeMs(){
        return timeSpent;
    }
    
    public long getTimeSec(){
        return timeSpent/MS_IN_SEC;
    }
}
