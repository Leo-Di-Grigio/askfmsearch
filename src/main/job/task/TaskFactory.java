package main.job.task;

import java.util.ArrayList;
import java.util.concurrent.LinkedBlockingQueue;

public final class TaskFactory {
    
    // keys spider
    private static final String TASK_SPIDER = "spider";
    private static final String TASK_SPIDER_ANSWERS = "answers";
    
    // keys index
    private static final String TASK_INDEX = "index";
    private static final String TASK_INDEX_SEARCH_TABLE = "search_index";
    private static final String TASK_INDEX_USERS_STATS = "stats";
    private static final String TASK_INDEX_HTML_PAGES = "html_pages";
    private static final String TASK_INDEX_FOUND_USERS = "users";
    
    // keys job
    private static final String TASK_JOB = "job";

    // keys test
    private static final String TASK_TEST = "test";
    
    public static LinkedBlockingQueue<Task> compile(ArrayList<String> jobScenario) {
        LinkedBlockingQueue<Task> queue = new LinkedBlockingQueue<Task>();
        
        for(String command: jobScenario){
            Task task = compileTask(command);
            
            if(task != null){
                queue.add(task);
            }
        }
        
        return queue;
    }
    
    private static Task compileTask(final String command) {
        String [] data = command.split(" ");
        
        if(data.length > 0){
            Task task = null;
            
            switch (data[0]) {
                
                case TASK_SPIDER:
                    task = buildSpider(data);
                    break;
                    
                case TASK_INDEX:
                    task = buildIndex(data);
                    break;

                case TASK_JOB:
                    task = buildJob(data);
                    break;
                    
                case TASK_TEST:
                    task = buildTest(data);
                    break;
                    
                default:
                    break;
            }
            
            if(task != null){
                task.setCommand(command);
                return task;
            }
            else{
                return null;
            }
        }
        else{
            return null;
        }
    }

    private static TaskSpider buildSpider(String[] data) {
        if(data.length > 1){
            switch (data[1]) {
                
                case TASK_SPIDER_ANSWERS:
                    return new TaskSpiderAnswers(data);

                default:
                    return null;
            }
        }
        else {
            return null;
        }
    }

    private static TaskIndex buildIndex(String[] data) {
        if(data.length > 1){
            switch (data[1]) {
                
                case TASK_INDEX_SEARCH_TABLE:
                    return new TaskIndexSearchTable(data);
                    
                case TASK_INDEX_USERS_STATS:
                    return new TaskIndexUsersStats(data);
                    
                case TASK_INDEX_HTML_PAGES:
                    return new TaskIndexHtmlPages(data);
                    
                case TASK_INDEX_FOUND_USERS:
                    return new TaskIndexFoundUsers(data);
                
                default:
                    return null;
            }
        }
        else{
            return null;
        }
    }

    private static TaskJob buildJob(String[] data) {
        if(data.length > 1){
            return new TaskJob(data[1]);
        }
        else{
            return null;
        }
    }
    
    private static TaskTest buildTest(String[] data) {
        return new TaskTest();
    }
}