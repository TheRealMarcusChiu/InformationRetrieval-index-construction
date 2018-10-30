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

    public IndexConstruction withDictionaryBlockingAndFrontCoding(Integer k) {
        this.dictionaryBlockingNumber = k;
        this.dictionaryCompressionFrontCoding = true;
        return this;
    }

    private Integer find(String num1, String num2) {
        Integer count = 0;
        for(int i = 0; i < num1.length() && i < num2.length(); i++) {
            if(num1.charAt(i) == num2.charAt(i)){
                count++;
            } else {
                break;
            }
        }
        return count;
    }

    public void buildIndex() throws Exception {
        try (PrintWriter out = new PrintWriter(fileName)) {
            StringBuilder onelineVocabulary = new StringBuilder();
            Integer kCount = dictionaryBlockingNumber;

            ArrayList<DictionaryEntry> entries  = new ArrayList<>();
            for (Map.Entry<String, DictionaryEntry> entry : dc.getDictionaryEntries().entrySet()) {
                entries.add(entry.getValue());
            }

            Boolean is = false;
            Boolean isSuffix = false;
            String previous = "";
            for (int i = 0; i < entries.size(); i++) {
                DictionaryEntry de = entries.get(i);

                String termField = de.getTerm();
                if (dictionaryBlockingNumber != -1) {

                    if (dictionaryCompressionFrontCoding) {
                        if (!is) {
                            if (termField.length() >= 4) {
                                if ((i+1) < entries.size()) {
                                    String nextTerm = entries.get(i+1).getTerm();
                                    if (nextTerm.length() >= 4) {
                                        Integer ii = find(termField, nextTerm);
                                        if (ii >= 4) {
                                            previous = termField.substring(0, ii);
                                            is = true;
                                            isSuffix = false;
                                            termField = previous + "*" + termField.substring(ii);
                                        } else {
                                            is = false;
                                            isSuffix = false;
                                        }
                                    } else {
                                        is = false;
                                        isSuffix = false;
                                    }
                                } else {
                                    is = false;
                                    isSuffix = false;
                                }
                            } else {
                                is = false;
                                isSuffix = false;
                            }
                        } else {
                            Integer iit = find(previous, termField);
                            if (iit >= previous.length()) {
                                termField = termField.substring(iit);
                                isSuffix = true;
                            } else {
                                if (termField.length() >= 4) {
                                    if ((i+1) < entries.size()) {
                                        String nextTerm = entries.get(i+1).getTerm();
                                        if (nextTerm.length() >= 4) {
                                            Integer ii = find(termField, nextTerm);
                                            if (ii >= 4) {
                                                previous = termField.substring(0, ii);
                                                is = true;
                                                isSuffix = false;
                                                termField = previous + "*" + termField.substring(ii);
                                            } else {
                                                is = false;
                                                isSuffix = false;
                                            }
                                        } else {
                                            is = false;
                                            isSuffix = false;
                                        }
                                    } else {
                                        is = false;
                                        isSuffix = false;
                                    }
                                } else {
                                    is = false;
                                    isSuffix = false;
                                }
                            }
                        }
                    }

                    int vocabBeforeSize = onelineVocabulary.length();
                    int size = termField.contains("*") ? termField.length() - 1 : termField.length();
                    onelineVocabulary.append(Integer.toString(size));
                    if (isSuffix) {
                        onelineVocabulary.append("◊");
                    }
                    onelineVocabulary.append(termField);
                    if (kCount.equals(dictionaryBlockingNumber)) {
                        termField = Integer.toString(vocabBeforeSize);
                        kCount = 1;
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
