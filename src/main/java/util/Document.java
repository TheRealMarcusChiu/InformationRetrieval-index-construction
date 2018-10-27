package util;

import lombok.Data;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;

import java.util.Map;
import java.util.TreeMap;

@Data
public class Document {

    Integer totalTokens;
    Map<String, Integer> tokenCounts;

    public Document(String content, Boolean enablePorterStemmer) {
        org.jsoup.nodes.Document doc = Jsoup.parse(content);

        Element element = doc.select("TEXT").first();
        String[] tokens = element.html()
                .replaceAll("[',/()-:]"," ")
                .replaceAll("\\.", "")
                .toLowerCase()
                .split("\\s+");

        if (enablePorterStemmer) {
            PorterStemmer ps = new PorterStemmer();
            for (int i = 0; i < tokens.length; i++) {
                tokens[i] = ps.stemWord(tokens[i]);
            }
        }

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
}
