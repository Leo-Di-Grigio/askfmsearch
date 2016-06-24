package main;

import java.io.IOException;

import main.job.UIJob;
//import main.ui.UIConsole;

public final class Main {

    public static void main(String [] args) throws IOException{
        System.setProperty("org.apache.commons.logging.Log", "org.apache.commons.logging.impl.NoOpLog");
        //new UIConsole().run();
        new UIJob().run();
    }
}