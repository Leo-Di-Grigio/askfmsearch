package main.job.task;

import main.index.Index;

final class TaskIndexSearchTable extends TaskIndex {

    public TaskIndexSearchTable(String [] data) {
        
    }

    @Override
    protected void execute() {
        Index.buildSearchIndex();
    }
}
