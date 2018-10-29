package util.construction;

import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;

public class Encoding {
    public static String deltaEncode(ArrayList<Integer> documentIDs) {
        ArrayList<Integer> documentGaps = new ArrayList<>();

        for (int i = 1; i < documentIDs.size(); i++) {
            documentGaps.add(documentIDs.get(i) - documentIDs.get(i-1));
        }

        documentGaps.add(0, documentIDs.get(0));

        StringBuilder encode = new StringBuilder();

        for (Integer gap : documentGaps) {
            encode.append(deltaEncode(gap));
        }

        return encode.toString();
    }

    public static String deltaEncode(Integer i) {
        String offset = Integer.toBinaryString(i).substring(1);
        return gammaEncode(offset.length() + 1) + offset;
    }

    public static String gammaEncode(final ArrayList<Integer> documentIDs) {
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

    public static String gammaEncode(Integer i) {
        String offset = Integer.toBinaryString(i).substring(1);
        String length = StringUtils.repeat("1", offset.length()) + "0";
        return length + offset;
    }
}
