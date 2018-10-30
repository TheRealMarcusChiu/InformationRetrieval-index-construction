import util.DictionaryEntry;
import util.DocumentCollection;
import util.NLP;
import util.construction.IndexConstruction;
import util.construction.PostingsCompressionType;

import java.io.File;
import java.util.ArrayList;
import java.util.Map;

public class Main {

    public static void main(String args[]) throws Exception {
        validateRequirements();

        File[] listOfFiles = new File("collection/").listFiles();
        DocumentCollection dc;

        Long readingTime = System.nanoTime();
        dc = new DocumentCollection(listOfFiles, NLP.STANFORD_LEMMATIZER);
        readingTime = System.nanoTime() - readingTime;

        ArrayList<DictionaryEntry> entries  = new ArrayList<>();
        for (Map.Entry<String, DictionaryEntry> entry : dc.getDictionaryEntries().entrySet()) {
            entries.add(entry.getValue());
        }

        Long t = System.nanoTime();
        new IndexConstruction(dc, "Index_Version1.uncompress")
                .buildIndex();
        t = System.nanoTime() - t + readingTime;
        System.out.println("Index_Version1.uncompress: " + t.toString());

        t = System.nanoTime();
        new IndexConstruction(dc, "Index_Version1.compressed")
                .withPostingsCompressionType(PostingsCompressionType.GAMMA)
                .withDictionaryBlockingCompression(8)
                .buildIndex();
        t = System.nanoTime() - t + readingTime;
        System.out.println("Index_Version1.compressed: " + t.toString());



        readingTime = System.nanoTime();
        dc = new DocumentCollection(listOfFiles, NLP.PORTER_STEMMER);
        readingTime = System.nanoTime() - readingTime;

        t = System.nanoTime();
        new IndexConstruction(dc, "Index_Version2.uncompress")
                .buildIndex();
        t = System.nanoTime() - t + readingTime;
        System.out.println("Index_Version2.uncompress: " + t.toString());

        t = System.nanoTime();
        new IndexConstruction(dc, "Index_Version2.compressed")
                .withPostingsCompressionType(PostingsCompressionType.DELTA)
                .withDictionaryBlockingAndFrontCoding(8)
                .buildIndex();
        t = System.nanoTime() - t + readingTime;
        System.out.println("Index_Version2.compressed: " + t.toString());
    }

    private static void validateRequirements() {
        if (!System.getProperty("java.version").substring(0,3).equals("1.8")) {
            System.out.println("Stanford NLP libraries requires java 1.8");
            System.out.println("current version: " + System.getProperty("java.version"));
            System.exit(0);
        }
    }
}
