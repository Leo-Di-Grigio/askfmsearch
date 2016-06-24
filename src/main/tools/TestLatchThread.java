package main.tools;

import java.util.concurrent.CountDownLatch;

public class TestLatchThread implements Runnable {

    private int id;
        
    private CountDownLatch latch;

    public TestLatchThread(CountDownLatch latch, int id) {
        this.latch = latch;
        this.id = id;
    }
    
    @Override
    public void run() {
        AskLog.debug("t=" + id + " start");
        
        // do
        doWork();
        
        latch.countDown();
        AskLog.debug("t= " + id + " end");
    }

    private void doWork() {
        try {
            Thread.sleep(1000);
        }
        catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
