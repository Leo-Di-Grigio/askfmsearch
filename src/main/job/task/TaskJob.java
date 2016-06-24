package main.job.task;

import main.tools.AskLog;

final class TaskJob extends Task {

    public TaskJob(final String jobTitle) {
        
    }

    @Override
    protected void execute() {
        AskLog.err(this.getClass().getName() + " not released");
    }
}
