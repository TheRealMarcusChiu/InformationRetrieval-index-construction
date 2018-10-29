package util;

import lombok.Data;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.TreeMap;

import static java.util.stream.Collectors.toMap;

@Data
public class Document {

    public static PorterStemmer ps;
    public static StanfordLemmatizer sl;

    Integer docLength;
    Integer max_tf;

    Map<String, Integer> tokenCounts;

    public Document(String text, NLP nlp) {
        text = text.replaceAll("[^a-zA-Z]+"," ").toLowerCase();

        String[] tokens = tokenize(text, nlp);

        this.docLength = tokens.length;
        tokens = StopWords.remove(tokens);

        this.tokenCounts = countTokensAndSort(tokens);
        this.max_tf      = this.tokenCounts.entrySet().stream().findFirst().get().getValue();
    }

    private Map<String, Integer> countTokensAndSort(String[] tokens) {
        Map<String, Integer> tokenCounts = new TreeMap<>();

        for (String token : tokens){
            if (!tokenCounts.containsKey(token)) {
                tokenCounts.put(token, 1);
            } else {
                tokenCounts.put(token, tokenCounts.get(token) + 1);
            }
        }

        tokenCounts = tokenCounts
                .entrySet()
                .stream()
                .sorted(Collections.reverseOrder(Map.Entry.comparingByValue()))
                .collect(toMap(Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e2,
                        LinkedHashMap::new));

        return tokenCounts;
    }

    private String[] tokenize(String text, NLP nlp) {
        String[] tokens;

        if (nlp == NLP.PORTER_STEMMER) {
            tokens = porterStemmer(text);
        } else if (nlp == NLP.STANFORD_LEMMATIZER) {
            tokens = stanfordLemmatizer(text);
        } else {
            tokens = text.split("[ ]+");
        }

        return tokens;
    }

    private String[] porterStemmer(String text) {
        if (ps == null) ps = new PorterStemmer();

        String[] tokens = text.split("[ ]+");
        for (int i = 0; i < tokens.length; i++) {
            tokens[i] = ps.stemWord(tokens[i]);
        }

        return tokens;
    }

    private String[] stanfordLemmatizer(String text) {
        if (sl == null) sl = new StanfordLemmatizer();

        return sl.lemmatize(text);
    }
}
