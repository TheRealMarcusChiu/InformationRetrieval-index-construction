import util.Document;
import util.DocumentCollection;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Map;

public class Main {

    private static String fileToString(File file) {
        StringBuilder contentBuilder = new StringBuilder();
        try {
            BufferedReader in = new BufferedReader(new FileReader(file));
            String str;
            while ((str = in.readLine()) != null) {
                contentBuilder.append(str);
            }
            in.close();
        } catch (IOException e) {
        }
        return contentBuilder.toString();
    }

    private static void printStats(DocumentCollection dc) {
        System.out.println("number of tokens in collection: " + dc.getTotalTokens());
        System.out.println("number of unique tokens in collection: " + dc.getTokenCounts().size());
        System.out.println("number of words that occur only once in collection: " +
                dc.getSortedTokenCounts().entrySet().stream().filter(x -> 1 == x.getValue()).count());

        Integer i = 1;
        for (Map.Entry<String, Integer> entry : dc.getSortedTokenCounts().entrySet()) {
            System.out.println(i.toString() + ". " + entry.getKey() + " = " + entry.getValue().toString());
            i++;
            if (i > 30) break;
        }

        System.out.println("average number of tokens per document: " + dc.getAverageTokensPerDocument());
    }

    public static void main(String args[]) {
        Boolean enablePorterStemmer = args.length == 1 && args[0].equals("enablePorterStemmer");

        DocumentCollection dc = new DocumentCollection();

        long startTime = System.currentTimeMillis();

        File[] listOfFiles = new File("collection/").listFiles();
        for (File file : listOfFiles) {
            dc.addDocument(new Document(fileToString(file), enablePorterStemmer));
        }

        long endTime = System.currentTimeMillis();

        System.out.println("Time program took to acquire the text characteristics: " + (endTime - startTime) + " milliseconds");

        dc.parse();

        printStats(dc);
    }
}
