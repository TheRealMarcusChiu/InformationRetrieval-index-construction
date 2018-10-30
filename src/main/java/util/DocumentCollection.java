package util;

import lombok.Data;
import org.jsoup.Jsoup;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.*;

import static java.util.stream.Collectors.toMap;

@Data
public class DocumentCollection {

    List<Document> documents;
    Map<String, DictionaryEntry> dictionaryEntries;

    public DocumentCollection(File[] listOfFiles, NLP nlp) throws Exception {
        this.documents = new ArrayList<>();
        this.dictionaryEntries = new TreeMap<>();

        for (File file : listOfFiles) {
            String fileString = DocumentCollection.fileToString(file);
            org.jsoup.nodes.Document doc = Jsoup.parse(fileString);
            String text = doc.select("TEXT").first().html();
            this.addDocument(new Document(text, nlp));
        }

        this.parse();
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

    private void addDocument(Document document) {
        this.documents.add(document);
        Integer docID = documents.size();

        for(Map.Entry<String,Integer> entry : document.tokenCounts.entrySet()) {
            String key = entry.getKey();
            Integer count = entry.getValue();

            if (dictionaryEntries.containsKey(key)) {
                DictionaryEntry dictionaryEntry = dictionaryEntries.get(key);
                dictionaryEntry.addTermFrequency(count);
                dictionaryEntry.addDocumentID(docID);
            } else {
                dictionaryEntries.put(key,
                        new DictionaryEntry(key, count, docID));
            }
        }
    }

    private void parse() {
        this.dictionaryEntries = this.dictionaryEntries
                .entrySet()
                .stream()
                .sorted(Map.Entry.comparingByKey())
                .collect(toMap(Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e2, LinkedHashMap::new));
    }
}
