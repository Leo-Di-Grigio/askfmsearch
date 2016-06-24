package main.job.task;

import main.index.Index;

final class TaskIndexUsersStats extends TaskIndex {

    public TaskIndexUsersStats(String [] data) {
        
    }

    @Override
    protected void execute() {
        Index.buildRating();
    }
}