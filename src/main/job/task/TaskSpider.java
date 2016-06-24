package main.job.task;

import java.util.concurrent.CountDownLatch;

import main.tools.AskLog;

abstract class TaskSpider extends Task {

    private static final String PARAM_THREADS = "t=";
    
    private final int threadsCount;
    
    public TaskSpider(String [] data) {
        this.threadsCount = getThreads(data);
    }
    
    private int getThreads(String [] data) {
        for(int i = 2; i < data.length; ++i){
            if(data[i].startsWith(PARAM_THREADS)){
                try{
                    String value = data[i].replaceAll(PARAM_THREADS, "");
                    int result = Integer.parseInt(value);
                    
                    if(result > 0){
                        return result;
                    }
                    else{
                        return 1;
                    }
                }
                catch(NumberFormatException e){
                    return 1;
                }
            }
        }
        
        return 1;
    }

    @Override
    protected final void execute() {
        CountDownLatch latch = new CountDownLatch(this.threadsCount);
        
        this.execute(latch, threadsCount);
        this.end(latch);
    }

    private void end(CountDownLatch latch) {
        try {
            latch.await();
            Thread.sleep(1000);
        }
        catch (InterruptedException e) {
            e.printStackTrace();
        }
        
        AskLog.debug("Spider task end");
    }

    protected void unlockLatch(CountDownLatch latch) {
        for(int i = 0; i < threadsCount; ++i){
            latch.countDown();
        }
    }
    
    abstract protected void execute(CountDownLatch latch, int threadsCount);
}