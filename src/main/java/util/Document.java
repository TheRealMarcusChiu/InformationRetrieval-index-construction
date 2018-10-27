package util;

import lombok.Data;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;

import java.util.ArrayList;
import java.util.Map;
import java.util.TreeMap;

@Data
public class Document {

    public static PorterStemmer ps;
    public static StanfordLemmatizer sl;

    Integer totalTokens;
    Map<String, Integer> tokenCounts;

    public Document(String text, NLP nlp) {
        text = text
                .toLowerCase()
                .replaceAll("[^a-zA-Z]+"," ");

        String[] tokens = {};
        if (nlp == NLP.PORTER_STEMMER) {
            tokens = porterStemmer(text);
        } else if (nlp == NLP.STANFORD_LEMMATIZER) {
            tokens = stanfordLemmatizer(text);
        }

        tokens = StopWords.remove(tokens);

        this.totalTokens = tokens.length;
        this.tokenCounts = new TreeMap<>();

        for (String token : tokens){
            if (!this.tokenCounts.containsKey(token)) {
                this.tokenCounts.put(token, 1);
            } else {
                this.tokenCounts.put(token, this.tokenCounts.get(token) + 1);
            }
        }
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
