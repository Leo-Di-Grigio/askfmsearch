package main.ui;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import main.index.Index;
import main.tools.AskLog;

public final class UIConsole {

    // Misc
    private static final String LINE_EXIT = "exit";
    private static final String LINE_HELP = "help";
    
    // Index
    private static final String LINE_INDEX = "index";
    private static final String PARAMS_INDEX_WORDS = "words";
    private static final String PARAMS_INDEX_RATING = "rating";
    private static final String PARAMS_INDEX_SVVAG = "svvag";
    private static final String PARAMS_INDEX_USERS = "users";

    private BufferedReader input;
    
    public UIConsole() {
        input = new BufferedReader(new InputStreamReader(System.in));
    }
    
    public void run(){
        String line = null;
        boolean notExit = true;
        
        while(notExit){
            try {
                System.out.print(">");
                line = input.readLine();
                line = line.trim();
                line = line.toLowerCase();
                
                if(line.equals(LINE_EXIT)){
                    notExit = false;
                }
                else if(line.equals(LINE_HELP)){
                    printHelp();
                }
                else{
                    processLine(line);
                }
            }
            catch (IOException e) {
                AskLog.err("Bad input");
            }
        }
    }

    private void printHelp() {
        System.out.println("=========================");
        System.out.println("> AskFm Search Tools");
        System.out.println("> leo.di.grigio@gmail.com");
        System.out.println("==========================");
        System.out.println("> MISC");
        System.out.println("> 'help' - to help menu");
        System.out.println("> 'exit' - to exit");
        System.out.println("\n> INDEX");
        System.out.println("> 'index [param1] [param2] ... [paramN]' - to index base control");
        System.out.println("\n> INDEX PARAMS");
        System.out.println("> 'words' - to build words index");
        System.out.println("> 'rating' - to build rating table");
        System.out.println("> 'svvag' - to build SVVAG rating table");
        System.out.println("> 'users' - to found all @usernames");
        System.out.println("==========================");
    }

    private void processLine(final String line) {
        String [] arr = line.split(" ");
        
        if(arr.length > 0){
            switch (arr[0]) {
                case LINE_INDEX:
                    runIndexEngine(arr);
                    break;

                default:
                    break;
            }        
        }
    }
    
    private void runIndexEngine(String [] arr) {
        if(arr.length > 1){
            for(int i = 1; i < arr.length; ++i)
                switch (arr[i]) {
                    case PARAMS_INDEX_WORDS:
                        runIndexEngineWords();
                        break;
                    
                    case PARAMS_INDEX_RATING:
                        runIndexEngineRating();
                        break;
                        
                    case PARAMS_INDEX_SVVAG:
                        runIndexEngineSVVAG();
                        break;
                        
                    case PARAMS_INDEX_USERS:
                        runIndexEngineUsers();
                        break;
                        
                    default:
                        break;
            }
        }
    }

    private void runIndexEngineWords() {
        Index.words();
    }
    
    private void runIndexEngineRating() {
        Index.buildHtmlPages();
    }

    private void runIndexEngineSVVAG() {
        Index.buildRating();
    }

    private void runIndexEngineUsers() {
        Index.findUsers();
    }
}