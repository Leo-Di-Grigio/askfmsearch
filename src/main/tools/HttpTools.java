package main.tools;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;

public final class HttpTools {

    private static final String USER_AGENT = "Mozilla/5.0 (Windows NT 6.3; WOW64)";
    
    public static String sendGet(HttpClient client, String url) {        
        HttpGet request = new HttpGet(url);
        request.addHeader("Host", "ask.fm");
        request.addHeader("Accept","*/*");
        request.addHeader("X-Requested-With","XMLHttpRequest");
        request.addHeader("Accept-Encoding","gzip, deflate, sdch");
        request.addHeader("Accept-Language","ru-RU,ru;q=0.8,en-US;q=0.6,en;q=0.4");
        request.addHeader("User-Agent", USER_AGENT);
        request.addHeader("Connection", "keep-alive");

        try {
            HttpResponse response = client.execute(request);
            
            if(response != null){
                BufferedReader rd = new BufferedReader(new InputStreamReader(response.getEntity().getContent(), "UTF-8"));
                StringBuffer result = new StringBuffer();
                String line = null;
            
                while ((line = rd.readLine()) != null) {
                    result.append(line);
                    //result.append("\n");
                }
            
                response.getEntity().getContent().close();
                return result.toString();
            }
            else{
                return null;
            }
        }
        catch (ClientProtocolException e) {
            AskLog.err("HttpTools.sendGet(): ClientProtocolException");
            return null;
        }
        catch (IOException e) {
            AskLog.err("HttpTools.sendGet(): IOException");
            return null;
        }
    }
}
