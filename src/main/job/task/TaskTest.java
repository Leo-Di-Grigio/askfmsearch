package main.job.task;

import main.tools.AskLog;

class TaskTest extends Task {

    @Override
    protected void execute() {
        AskLog.log("testing...");
    }
}
