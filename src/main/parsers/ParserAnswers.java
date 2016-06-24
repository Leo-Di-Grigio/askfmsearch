package main.parsers;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.LinkedBlockingQueue;

import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import main.database.Database;
import main.headers.HeaderAnswer;
import main.headers.HeaderUser;
import main.tools.AskLog;
import main.tools.Dateformat;
import main.tools.HttpTools;

public final class ParserAnswers implements Runnable {

    // constants
    private static final String CLASS_VIEW_MORE = "viewMore";
    private static final String CLASS_DATA_URL = "data-url";
    private static final String CLASS_ITEM_ANSWER = "streamItem-answer";
    
    private static final String CLASS_CONTENT_QUESTION = "streamItemContent-question";
    private static final String CLASS_CONTENT_ANSWER = "streamItemContent-answer";
    private static final String CLASS_CONTENT_LINK = "streamItemsAge";
    private static final String CLASS_CONTENT_LIKES = "counter";
    
    private static final String URL_ASK = "https://ask.fm/";
    private static final String URL_ASK_MORE_REQUEST = "https://ask.fm";
    
    private static final String RUS_ALPHABET = "¿¡¬√ƒ≈®∆«»… ÀÃÕŒœ–—“”‘’÷◊ÿŸ⁄€‹›ﬁﬂ‡·‚„‰Â∏ÊÁËÈÍÎÏÌÓÔÒÚÛÙıˆ˜¯˘˙˚¸˝˛ˇ";
    private static final double RUS_CHARS_RATIO = 0.45d;
    
    //
    private final int id;
    private final CountDownLatch latch;
    
    //
    private HttpClient client;
    private final LinkedBlockingQueue<HeaderUser> users;

    public ParserAnswers(CountDownLatch latch, int threadId, LinkedBlockingQueue<HeaderUser> users) {
        this.latch = latch;
        this.id = threadId;
        this.users = users;
        this.client = HttpClientBuilder.create().build();
    }
    
    @Override
    public void run() {
        AskLog.debug("t=" + id + " start");
        process();
        latch.countDown();
        AskLog.debug("t=" + id + " end");
    }

    private void process() {
        while(true){
            HeaderUser user = users.poll();
            
            if(user != null){
                AskLog.log("t=" + id + " parse \"" + user.name + "\"");
                ArrayList<HeaderAnswer> answers = parse(user);
                
                if(answers != null){
                    if(user.rus){
                        Database.writeAnswers(answers);
                        long maxDate = getLatestDate(answers);
                        
                        if(maxDate > user.lastDate){
                            Database.updateUserLastDate(user, maxDate);
                        }
                        
                        Database.updateUserRus(user, user.rus);
                    }
                }
            }
            else{
                break;
            }
        }
    }

    private long getLatestDate(ArrayList<HeaderAnswer> answers) {
        long date = 0L;
        
        for(HeaderAnswer answer: answers){
            if(answer.date > date){
                date = answer.date;
            }
        }
        
        return date;
    }

    private ArrayList<HeaderAnswer> parse(HeaderUser user){
        if(client == null || user == null){
            return null;
        }
        
        ArrayList<HeaderAnswer> result = new ArrayList<HeaderAnswer>();

        // header parsing
        String html = HttpTools.sendGet(client, URL_ASK + user.name);
        if(html == null){
            return null;
        }
        
        Document parsedData = Jsoup.parse(html);
        if(parsedData == null){
            return null;
        }
        
        ArrayList<HeaderAnswer> answers = processRequest(parsedData);
        if(answers != null){
            result.addAll(answers);
            answers = null;
        }
        
        if(resultRussianCheck(result)){
            // pages parsing
            while(isNextRequest(parsedData)){
                html = HttpTools.sendGet(client, processNextRequest(parsedData));
                
                if(html != null){
                    parsedData = Jsoup.parse(html);
                    
                    if(parsedData != null){
                        answers = processRequest(parsedData);
                        
                        if(answers != null){
                            result.addAll(answers);
                            answers = null;
                        }
                    }
                }
                else{
                    parsedData = null;
                }
            }
            
            // return result
            if(resultRussianCheck(result)){
                user.rus = true;
                return result;
            }
            else{
                user.rus = false;
                return null;
            }
        }
        else{
            return null;
        }
    }
    
    private boolean resultRussianCheck(ArrayList<HeaderAnswer> result) {
        if(result != null && result.size() > 0){
            long lettersCount = 0;
            long lettersTotal = 0;
            
            for(int i = 0; i < result.size(); ++i){
                HeaderAnswer answer = result.get(i);
                
                if(answer != null && answer.textAnswer != null){
                    for(int j = 0; j < answer.textAnswer.length(); ++j){
                        if(RUS_ALPHABET.contains("" + answer.textAnswer.charAt(j))){
                            lettersCount++;
                        }
                    }
                    
                    lettersTotal += answer.textAnswer.length();
                }
            }
            
            double lettersRatio = (double)lettersCount/(double)lettersTotal;
            
            if(lettersRatio >= RUS_CHARS_RATIO){
                AskLog.log("t=" + id + " rus_ratio = " + lettersRatio);
                return true;
            }
            else{
                return false;
            }
        }
        else{
            return false;
        }
    }
    
    private boolean isNextRequest(Document parsedData) {
        if(parsedData == null){
            return false;
        }
        else{
            Iterator<Element> iterator = parsedData.getElementsByClass(CLASS_VIEW_MORE).iterator();
            
            if(iterator.hasNext()){
                return true;
            }
            else{
                return false;
            }
        }
    }

    private String processNextRequest(Document parsedData){
        Iterator<Element> iterator = parsedData.getElementsByClass(CLASS_VIEW_MORE).iterator();
        
        if(iterator.hasNext()){
            return URL_ASK_MORE_REQUEST + (iterator.next().attr(CLASS_DATA_URL).toString().replace("&amp;", "&"));
        }
        else {
            return null;
        }
    }
    
    private ArrayList<HeaderAnswer> processRequest(Document parsedData) {
        ArrayList<HeaderAnswer> result = new ArrayList<HeaderAnswer>();

        Iterator<Element> iterator = parsedData.getElementsByClass(CLASS_ITEM_ANSWER).iterator();
        while(iterator.hasNext()){
            Element element = iterator.next();
            
            String textHref = getHref(element);
            String user = getUser(textHref);
            String textQuestion = getTextField(element, CLASS_CONTENT_QUESTION);
            String textAnswer = getTextField(element, CLASS_CONTENT_ANSWER);
            int likes = getIntField(element, CLASS_CONTENT_LIKES);
            long date = getDate(element);
            
            result.add(new HeaderAnswer(textHref, 0, user, textQuestion, textAnswer, likes, date));
        }
        
        if(result.isEmpty()){
            return null;
        }
        else{
            return result;
        }
    }

    private String getHref(Element element){
        Iterator<Element> iteratorMeta = element.getElementsByClass(CLASS_CONTENT_LINK).iterator();
        
        StringBuilder builder = new StringBuilder("");
        while(iteratorMeta.hasNext()){
            builder.append(iteratorMeta.next().attr("href").toString());
        }
        
        return builder.toString();
    }

    private String getUser(String textHref) {
        String [] text = textHref.split("/");
        return text[1];
    }
    
    private String getTextField(Element element, final String htmlClass){
        Iterator<Element> iterator = element.getElementsByClass(htmlClass).iterator();
        StringBuilder builder = new StringBuilder("");
        
        while(iterator.hasNext()){            
            builder.append(iterator.next().text()).append("\r\n");
        }
        
        return builder.toString();
    }
    
    private int getIntField(Element element, String htmlClass) {
        Iterator<Element> iterator = element.getElementsByClass(htmlClass).iterator();
        
        while(iterator.hasNext()){            
            return Integer.parseInt(iterator.next().text());
        }
        
        return 0;
    }
    
    // <a class="streamItemsAge extra" data-hint="March 09, 2016 17:12:44 GMT" href="/GODSHX/answers/135317934057">5 days ago</a>
    private long getDate(Element element) {
        Iterator<Element> iteratorMeta = element.getElementsByClass(CLASS_CONTENT_LINK).iterator();
        
        StringBuilder builder = new StringBuilder("");
        while(iteratorMeta.hasNext()){
            builder.append(iteratorMeta.next().attr("data-hint").toString());
        }

        return Dateformat.main(builder.toString());
    }
}