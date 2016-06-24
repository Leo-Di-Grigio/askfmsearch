package main.job.task;

import java.text.Collator;
import java.util.ArrayList;
import java.util.Locale;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.LinkedBlockingQueue;

import main.database.Database;
import main.headers.HeaderUser;
import main.parsers.ParserAnswers;
import main.tools.AskLog;

final class TaskSpiderAnswers extends TaskSpider {

    private static final String PARAM_START_KEY = "start_key=";
    
    private final String startKey;
    
    public TaskSpiderAnswers(String [] data) {
        super(data);
        
        this.startKey = getStartKey(data);
    }
    
    private String getStartKey(String [] data) {
        for(int i = 2; i < data.length; ++i){
            if(data[i].startsWith(PARAM_START_KEY)){
                return data[i].replaceAll(PARAM_START_KEY, "");
            }
        }
        
        return null;
    }

    @Override
    protected void execute(final CountDownLatch latch, final int threadsCount) {
        // data
        LinkedBlockingQueue<HeaderUser> users = loadUsersList(this.startKey);
        
        if(users != null){
            AskLog.debug("Spider answers on t=" + threadsCount);
            AskLog.debug("Users count: " + users.size());
            processUsersList(latch, threadsCount, users);
        }
        else{
            AskLog.log("Users not found");
            super.unlockLatch(latch);
        }
    }

    private LinkedBlockingQueue<HeaderUser> loadUsersList(final String key) {
        LinkedBlockingQueue<HeaderUser> users = null;
        
        if(key != null){
            users = new LinkedBlockingQueue<HeaderUser>();
            Collator collator = Collator.getInstance(new Locale("en", "US"));
            
            ArrayList<HeaderUser> list = Database.getUsersAll();
            for(HeaderUser user: list){
                if(collator.compare(user.name, key) >= 0){
                    users.add(user);
                }
            }
        }
        else{
            users = new LinkedBlockingQueue<HeaderUser>(Database.getUsersAll()); 
        }
        
        // return queue
        if(users.size() > 0){
            return users;
        }
        else{
            return null;
        }
    }
    
    private void processUsersList(final CountDownLatch latch, final int threadsCount, LinkedBlockingQueue<HeaderUser> users) {
        System.out.println("t=" + threadsCount);
        for(int i = 0; i < threadsCount; ++i){
            new Thread(new ParserAnswers(latch, i, users)).start();
        }
    }
}