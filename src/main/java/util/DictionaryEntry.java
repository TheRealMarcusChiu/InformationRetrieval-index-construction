package util;

import lombok.Data;

import java.util.ArrayList;

@Data
public class DictionaryEntry {

    String term;
    Integer termFrequency;
    ArrayList<Integer> documentIDs;

    DictionaryEntry(String term, Integer termFrequency, Integer documentID) {
        this.term = term;
        this.termFrequency = termFrequency;
        this.documentIDs = new ArrayList<>();
        this.documentIDs.add(documentID);
    }

    public void addDocumentID(Integer documentID) {
        this.documentIDs.add(documentID);
    }

    public void addTermFrequency(Integer termFrequency) {
        this.termFrequency += termFrequency;
    }
}
