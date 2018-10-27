import org.jsoup.Jsoup;
import util.Document;
import util.DocumentCollection;
import util.NLP;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.Map;

public class Main {

    public static void main(String args[]) throws Exception {
        validateRequirements();
        NLP nlp = getNLP(args);

        DocumentCollection dc = new DocumentCollection();

        long startTime = System.currentTimeMillis();

        File[] listOfFiles = new File("collection/").listFiles();
        for (File file : listOfFiles) {
            String fileString = fileToString(file);
            org.jsoup.nodes.Document doc = Jsoup.parse(fileString);
            String text = doc.select("TEXT").first().html();
            dc.addDocument(new Document(text, nlp));
        }

        long endTime = System.currentTimeMillis();

        System.out.println("Time program took to acquire the text characteristics: " + (endTime - startTime) + " milliseconds");

        dc.parse();

        printStats(dc);
    }

    private static String fileToString(File file) throws Exception {
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            StringBuilder sb = new StringBuilder();
            String line = br.readLine();

            while (line != null) {
                sb.append(line);
                sb.append(System.lineSeparator());
                line = br.readLine();
            }
            return sb.toString();
        }
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

    private static void validateRequirements() {
        if (!System.getProperty("java.version").substring(0,3).equals("1.8")) {
            System.out.println("Stanford NLP libraries requires java 1.8");
            System.out.println("current version: " + System.getProperty("java.version"));
            System.exit(0);
        }
    }

    private static NLP getNLP(String[] args) {
        NLP nlp = NLP.NONE;

        if (args.length != 0) {
            nlp = NLP.valueOf(args[0]);
        }

        return nlp;
    }
}
