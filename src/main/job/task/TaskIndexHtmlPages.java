package main.job.task;

import main.index.Index;

final class TaskIndexHtmlPages extends TaskIndex {

    public TaskIndexHtmlPages(String [] data) {
        
    }

    @Override
    protected void execute() {
        Index.buildHtmlPages();
    }
}
