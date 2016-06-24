package main.job;

import java.util.ArrayList;
import java.util.concurrent.LinkedBlockingQueue;

import main.interfaces.UpdateListener;
import main.job.task.Task;
import main.job.task.TaskFactory;
import main.tools.AskLog;

class JobManager implements UpdateListener {
    
    private LinkedBlockingQueue<Task> queue;

    public void start(ArrayList<String> jobScenario) {
        queue = TaskFactory.compile(jobScenario);
        
        if(queue != null && queue.size() > 0){
            this.update();
        }
        else{
            AskLog.err("Job is empty");
        }
    }

    @Override
    public void update() {
        Task task = queue.poll();
        
        if(task != null){
            task.execute(this);
        }
        else{
            AskLog.log("Job is done");
        }
    }
}