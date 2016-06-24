package main.job.task;

import main.interfaces.UpdateListener;
import main.tools.AskLog;

public abstract class Task {

    private String command;

    public final void setCommand(String command) {
        this.command = command;
    }
    
    public void execute(UpdateListener listener) {
        AskLog.log("Do task > " + command);
        this.execute();
        
        // on ending;
        listener.update();
    }
    
    abstract protected void execute();
}
