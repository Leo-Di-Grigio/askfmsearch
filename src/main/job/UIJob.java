package main.job;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Scanner;

import main.database.Database;
import main.headers.HeaderAnswer;
import main.tools.AskLog;

public class UIJob {

    // constants
    private static final String LINE_EXIT = "exit";
    private static final String LINE_HELP = "help";
    private static final String LINE_SHOW_JOBS = "jobs";
    private static final String LINE_DO_JOB = "do";
    private static final String LINE_SHOW_JOB = "show";
    private static final String LINE_SEARCH = "search";
    
    // data path
    private static final String JOBS_FOLDER_PATH = "jobs";
    
    //
    private BufferedReader input;
    private boolean notExit = true;
    
    public UIJob() {
        input = new BufferedReader(new InputStreamReader(System.in));
    }
    
    public void run(){
        printHello();
        
        
        String line = "";
        while(notExit){
            System.out.print(">");
            
            try {
                line = input.readLine();
                line = line.trim();
                line = line.toLowerCase();
                processLine(line);
            }
            catch (IOException e) {
                AskLog.err("Bad input");
            }
        }
    }
    
    private void processLine(final String line) {
        switch (line) {
            
            case LINE_EXIT:
                notExit = false;
                break;
                
            case LINE_HELP:
                printHelp();
                break;
                
            case LINE_SHOW_JOBS:
                printJobsList();
                break;
                
            case LINE_SEARCH:
                search();
                break;

            default:
                processElse(line);
                break;
        }
    }
    
    // test
    private void search() {
        System.out.println("test 123");
        
        ArrayList<HeaderAnswer> list = Database.searchAnswers("пиздос чувак");
        
        if(list != null){
            System.out.println("query size: " + list.size());
            
            for(HeaderAnswer answer: list){
                System.out.println("" + answer.answerId + " " + answer.href + " " + answer.likes);
            }
        }
        else{
            System.out.println("query is empty");
        }
    }

    private void processElse(final String line) {
        String [] lineKeys = line.split(" ");
        
        if(lineKeys.length > 0){
            switch (lineKeys[0]) {
                
                case LINE_DO_JOB:
                    if(lineKeys.length == 2){
                        doJob(lineKeys[1]);
                    }
                    break;
                    
                case LINE_SHOW_JOB:
                    if(lineKeys.length == 2){
                        showJob(lineKeys[1]);
                    }
                    break;

                default:
                    break;
            }
        }
    }

    private void printHello(){
        System.out.println("=== AskSearch Engine ===");
    }
    
    private void printHelp(){
        System.out.println("=== Help ===");
        System.out.println(LINE_EXIT + " - to end working");
        System.out.println(LINE_HELP + " - show comamnds list");
        System.out.println(LINE_SHOW_JOBS + " - show jobs list");
        System.out.println(LINE_DO_JOB + " [job name] - execute job");
        System.out.println(LINE_SHOW_JOB + " [job_name] - to print job scenario");
    }
    
    private void printJobsList() {
        String [] jobsList = buildJobsList();
        
        System.out.println("=== Jobs ===");
        for(int i = 0; i < jobsList.length; ++i){
            System.out.println(jobsList[i]);
        }
    }

    private String [] buildJobsList() {
        File folder = new File(JOBS_FOLDER_PATH);
        File [] files = folder.listFiles();
        
        String [] jobsNames = new String[files.length];
        for(int i = 0; i < jobsNames.length; ++i){
            jobsNames[i] = files[i].getName();
        }
        
        return jobsNames;
    }

    private void doJob(final String path) {
        ArrayList<String> scenario = readJobScenario(path);
        
        if(scenario != null){
            executeJob(scenario);
            notExit = false;
        }
    }

    private void showJob(final String path) {
        ArrayList<String> scenario = readJobScenario(path);
        
        if(scenario != null){
            for(String line: scenario){
                System.out.println("> " + line);
            }
        }
    }

    private void executeJob(ArrayList<String> jobScenario) {
        new JobManager().start(jobScenario);
    }
    
    private ArrayList<String> readJobScenario(String path) {
        File file = new File(JOBS_FOLDER_PATH + "/" + path);
        
        if(file.exists()){
            Scanner in;
            try {
                in = new Scanner(file);
                
                ArrayList<String> scenario = new ArrayList<String>();
                while(in.hasNextLine()) {
                    scenario.add(in.nextLine());
                }
                
                in.close();
                
                if(scenario.size() > 0){
                    return scenario;
                }
                else{
                    AskLog.err("Scenario file is empty");
                    return null;
                }
            }
            catch (FileNotFoundException e) {
                e.printStackTrace();
                return null;
            }
        }
        else{
            AskLog.err("Scenario file not found");
            return null;
        }
    }
}