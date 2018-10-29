package util.construction;

import org.apache.commons.lang3.StringUtils;
import util.DictionaryEntry;
import util.Document;
import util.DocumentCollection;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Map;

public class IndexConstruction {
    DocumentCollection dc;
    String fileName;
    PostingsCompressionType postingsCompressionType;
    Integer dictionaryBlockingNumber;
    Boolean dictionaryCompressionFrontCoding;

    public IndexConstruction(DocumentCollection dc, String fileName) {
        this.dc = dc;
        this.fileName = "index/" + fileName;
        this.dictionaryBlockingNumber = 0;
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
        if (this.postingsCompressionType == PostingsCompressionType.GAMMA) {
            writeIndexGammaEncode();
        } else if (this.postingsCompressionType == PostingsCompressionType.DELTA) {
            writeIndexDeltaEncode();
        } else {
            writeIndexNormal(this.dc, this.fileName);
        }
    }

    private void writeIndexDeltaEncode() throws Exception {
        try (PrintWriter out = new PrintWriter(fileName)) {
            for (Map.Entry<String, DictionaryEntry> entry : dc.getDictionaryEntries().entrySet()) {
                DictionaryEntry de = entry.getValue();

                out.println(de.getTerm() + " " +
                        de.getDocumentIDs().size() + " " +
                        de.getTermFrequency() + " " +
                        new String(binaryString2ByteArray(deltaEncode(de.getDocumentIDs()))));
            }
            for (Document d : dc.getDocuments()) {
                out.println(d.getMax_tf() + " " + d.getDocLength());
            }
        }
    }

    private void writeIndexGammaEncode() throws Exception {
        try (PrintWriter out = new PrintWriter(fileName)) {
            for (Map.Entry<String, DictionaryEntry> entry : dc.getDictionaryEntries().entrySet()) {
                DictionaryEntry de = entry.getValue();

                out.println(de.getTerm() + " " +
                        de.getDocumentIDs().size() + " " +
                        de.getTermFrequency() + " " +
                        new String(binaryString2ByteArray(gammaEncode(de.getDocumentIDs()))));
            }
            for (Document d : dc.getDocuments()) {
                out.println(d.getMax_tf() + " " + d.getDocLength());
            }
        }
    }

    private void writeIndexNormal(DocumentCollection dc, String fileName) throws Exception {
        try (PrintWriter out = new PrintWriter(fileName)) {
            for (Map.Entry<String, DictionaryEntry> entry : dc.getDictionaryEntries().entrySet()) {
                DictionaryEntry de = entry.getValue();
                out.println(de.getTerm() + " " +
                        de.getDocumentIDs().size() + " " +
                        de.getTermFrequency() + " " +
                        StringUtils.join(de.getDocumentIDs(), ","));
            }
            for (Document d : dc.getDocuments()) {
                out.println(d.getMax_tf() + " " + d.getDocLength());
            }
        }
    }

    //Since JDK 7 - try resources
    private static void writeBytesToFile(byte[] bFile, String fileName) throws IOException {
        try (FileOutputStream fileOuputStream = new FileOutputStream(fileName, true)) {
            fileOuputStream.write(bFile);
        }
    }

    private static void fileStuff() throws Exception {
        byte b = (byte)Integer.parseInt("11111111", 2);
        System.out.println(b);
        byte[] bb = {b};
        writeBytesToFile(bb, "index/Index_Version1");

        File file = new File("index/Index_Version1");
        byte[] fileContent = Files.readAllBytes(file.toPath());
        System.out.println(fileContent);
    }

    private String deltaEncode(ArrayList<Integer> documentIDs) {
        ArrayList<Integer> documentGaps = new ArrayList<>();

        for (int i = 1; i < documentIDs.size(); i++) {
            documentGaps.add(documentIDs.get(i) - documentIDs.get(i-1));
        }

        documentGaps.add(0, documentIDs.get(0));

        StringBuilder encode = new StringBuilder();

        for (Integer gap : documentGaps) {
            String offset = Integer.toBinaryString(gap).substring(1);
            encode.append(gammaEncode(offset.length() + 1)).append(offset);
        }

        return encode.toString();
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

    private String gammaEncode(final ArrayList<Integer> documentIDs) {
        ArrayList<Integer> documentGaps = new ArrayList<>();

        for (int i = 1; i < documentIDs.size(); i++) {
            documentGaps.add(documentIDs.get(i) - documentIDs.get(i-1));
        }

        documentGaps.add(0, documentIDs.get(0));

        StringBuilder gammaEncode = new StringBuilder();

        for (Integer gap : documentGaps) {
            gammaEncode.append(gammaEncode(gap));
        }

        return gammaEncode.toString();
    }

    private String gammaEncode(Integer i) {
        String offset = Integer.toBinaryString(i).substring(1);
        String length = StringUtils.repeat("1", offset.length()) + "0";
        return length + offset;
    }
}
