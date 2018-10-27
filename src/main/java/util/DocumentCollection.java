package util;

import lombok.Data;

import java.util.*;

import static java.util.stream.Collectors.toMap;

@Data
public class DocumentCollection {

    List<Integer> tokenCountPerDocument;
    Integer averageTokensPerDocument;
    Integer totalTokens;
    Map<String, Integer> tokenCounts;
    Map<String, Integer> sortedTokenCounts;

    public DocumentCollection() {
        this.tokenCountPerDocument = new ArrayList<>();
        this.averageTokensPerDocument = 0;
        this.totalTokens = 0;
        this.tokenCounts = new TreeMap<>();
        this.sortedTokenCounts = new TreeMap<>();
    }

    public void addDocument(Document document) {
        this.totalTokens += document.totalTokens;
        this.tokenCountPerDocument.add(document.totalTokens);

        for(Map.Entry<String,Integer> entry : document.tokenCounts.entrySet()) {

            String key = entry.getKey();
            Integer count = entry.getValue();

            if (!this.tokenCounts.containsKey(key)) {
                this.tokenCounts.put(key, count);
            } else {
                this.tokenCounts.put(key, this.tokenCounts.get(key) + count);
            }
        }
    }

    public void parse() {
        this.averageTokensPerDocument = this.totalTokens / this.tokenCountPerDocument.size();
        this.sortedTokenCounts = this.tokenCounts
                .entrySet()
                .stream()
                .sorted(Collections.reverseOrder(Map.Entry.comparingByValue()))
                .collect(toMap(Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e2,
                                LinkedHashMap::new));
    }
}
