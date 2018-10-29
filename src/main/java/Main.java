import util.DocumentCollection;
import util.NLP;
import util.construction.IndexConstruction;
import util.construction.PostingsCompressionType;

import java.io.File;

public class Main {

    public static void main(String args[]) throws Exception {
        validateRequirements();

        File[] listOfFiles = new File("collection/").listFiles();
        DocumentCollection dc;

//        dc = new DocumentCollection(listOfFiles, NLP.STANFORD_LEMMATIZER);
//
//        new IndexConstruction(dc, "Index_Version1.uncompress")
//                .buildIndex();
//
//        new IndexConstruction(dc, "Index_Version1.compressed")
//                .withPostingsCompressionType(PostingsCompressionType.GAMMA)
//                .buildIndex();


        dc = new DocumentCollection(listOfFiles, NLP.PORTER_STEMMER);

        new IndexConstruction(dc, "Index_Version2.uncompress")
                .buildIndex();

        new IndexConstruction(dc, "Index_Version2.compressed")
                .withPostingsCompressionType(PostingsCompressionType.DELTA)
                .buildIndex();
    }

    private static void validateRequirements() {
        if (!System.getProperty("java.version").substring(0,3).equals("1.8")) {
            System.out.println("Stanford NLP libraries requires java 1.8");
            System.out.println("current version: " + System.getProperty("java.version"));
            System.exit(0);
        }
    }
}
