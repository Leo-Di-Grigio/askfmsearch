package main.tools;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;

public final class Tools {
    
    public static String normalizeText(String word) {
        return word.toLowerCase().replaceAll("[^a-zà-ÿ0-9]", "");
    }
    
    public static int [] getArray(String str){
        String [] data = str.split(",");
        
        if(data.length > 0){
            int [] array = new int[Integer.parseInt(data[0])];
            for(int i = 1; i <= array.length; ++i){
                array[i - 1] = Integer.parseInt(data[i]);
            }
            return array;
        }
        else{
            return null;
        }
    }

    public static String buildInvertedIndex(HashMap<String, ArrayList<Integer>> index) {
        if(index.size() == 0){
            return "";
        }
        else{
            StringBuilder builder = new StringBuilder();
            
            Set<String> words = index.keySet();
            for(String word: words){
                builder.append(word).append(':');
                
                ArrayList<Integer> positions = index.get(word);
                for(int i = 0; i < positions.size(); ++i){
                    builder.append(positions.get(i));
                    
                    if(i < positions.size() - 1){
                        builder.append(',');
                    }
                }
                
                builder.append(';');
            }
            
            return builder.toString();
        }
    }
}