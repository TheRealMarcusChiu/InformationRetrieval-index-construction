package util.construction;

import org.apache.commons.lang3.StringUtils;
import util.DictionaryEntry;
import util.Document;
import util.DocumentCollection;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Map;

public class IndexConstruction {
    private DocumentCollection dc;
    private String fileName;
    private PostingsCompressionType postingsCompressionType;
    private Integer dictionaryBlockingNumber;
    private Boolean dictionaryCompressionFrontCoding;

    public IndexConstruction(DocumentCollection dc, String fileName) {
        this.dc = dc;
        this.fileName = "index/" + fileName;
        this.dictionaryBlockingNumber = -1;
        this.postingsCompressionType = PostingsCompressionType.NONE;
        this.dictionaryCompressionFrontCoding = false;
    }

    public IndexConstruction withPostingsCompressionType(PostingsCompressionType pct) {
        this.postingsCompressionType = pct;
        return this;
    }

    public IndexConstruction withDictionaryBlockingCompression(Integer k) {
        this.dictionaryBlockingNumber = k;
        return this;
    }

    public IndexConstruction withDictionaryFrontCoding() {
        this.dictionaryCompressionFrontCoding = true;
        return this;
    }

    public void buildIndex() throws Exception {
        try (PrintWriter out = new PrintWriter(fileName)) {
            StringBuilder onelineVocabulary = new StringBuilder();
            Integer kCount = 0;

            for (Map.Entry<String, DictionaryEntry> entry : dc.getDictionaryEntries().entrySet()) {
                DictionaryEntry de = entry.getValue();

                String termField = de.getTerm();
                if (dictionaryBlockingNumber != -1) {
                    int vocabBeforeSize = onelineVocabulary.length();
                    onelineVocabulary.append(Integer.toString(termField.length())).append(termField);
                    if (kCount.equals(this.dictionaryBlockingNumber)) {
                        termField = Integer.toString(vocabBeforeSize);
                        kCount = 0;
                    } else {
                        termField = "";
                        kCount++;
                    }
                }
                out.println(termField + " " +
                        de.getDocumentIDs().size() + " " +
                        de.getTermFrequency() + " " +
                        posting(de.getDocumentIDs(), this.postingsCompressionType));
            }
            if (dictionaryBlockingNumber != -1) {
                out.print(onelineVocabulary.toString());
            }
            for (Document d : dc.getDocuments()) {
                out.println(d.getMax_tf() + " " + d.getDocLength());
            }
        }
    }

    private String posting(ArrayList<Integer> documentIDs, PostingsCompressionType postingsCompressionType) {
        String t;

        if (postingsCompressionType == PostingsCompressionType.GAMMA) {
            t = new String(binaryString2ByteArray(Encoding.gammaEncode(documentIDs)));
        } else if (postingsCompressionType == PostingsCompressionType.DELTA) {
            t = new String(binaryString2ByteArray(Encoding.deltaEncode(documentIDs)));
        } else {
            t = StringUtils.join(documentIDs, ",");
        }

        return t;
    }

    private byte[] binaryString2ByteArray(String binaryString) {
        // split string into lengths of 8. last string could be length 1-8
        String[] strs = binaryString.split("(?<=\\G.{8})");

        byte[] bytes = new byte[strs.length];
        for (int i = 0; i < strs.length; i++) {
            bytes[i] = (byte)Integer.parseInt(strs[i], 2);
        }

        return bytes;
    }
}
