package main.job.task;

import main.index.Index;

final class TaskIndexFoundUsers extends TaskIndex {

    public TaskIndexFoundUsers(String [] data) {
        
    }

    @Override
    protected void execute() {
        Index.findUsers();
    }
}