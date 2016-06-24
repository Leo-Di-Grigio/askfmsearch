package main.index;

public class Index {

    public static void words() {
        IndexWords.build();
    }

    public static void buildHtmlPages() {
        IndexHtmlPages.build();
    }
    
    public static void buildRating(){
        IndexStats.build();
    }

    public static void findUsers() {
        IndexFindUsers.build();
    }
    
    public static void buildSearchIndex() {
        IndexSearch.build();
    }
}