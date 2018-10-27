package util;

import java.util.ArrayList;
import java.util.Arrays;

public class StopWords {
    public static ArrayList<String> STOP_WORDS;

    static{
        STOP_WORDS = new ArrayList<>(Arrays.asList(
                "a",
                "all",
                "an",
                "and",
                "any",
                "are",
                "as",
                "be",
                "been",
                "but",
                "by ",
                "few",
                "for",
                "have",
                "he",
                "her",
                "here",
                "him",
                "his",
                "how",
                "i",
                "in",
                "is",
                "it",
                "its",
                "many",
                "me",
                "my",
                "none",
                "of",
                "on ",
                "or",
                "our",
                "she",
                "some",
                "the",
                "their",
                "them",
                "there",
                "they",
                "that ",
                "this",
                "us",
                "was",
                "what",
                "when",
                "where",
                "which",
                "who",
                "why",
                "will",
                "with",
                "you",
                "your"
        )
        );
    }

    public static String[] remove(String[] tokens) {
        ArrayList<String> newTokens = new ArrayList<>();
        for (String token : tokens) {
            if (!STOP_WORDS.contains(token)) newTokens.add(token);
        }
        return newTokens.toArray(new String[newTokens.size()]);
    }
}
