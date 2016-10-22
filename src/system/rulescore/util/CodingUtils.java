package system.rulescore.util;

import java.io.*;

/**
 * NOTE: To modify coding scheme, modify the two methods decodeLine encodeLine
 */
public class CodingUtils {

    final String actualRulesFolder = "actual_rules";
    final String encodedRulesFolder = "encoded_rules";
    //used to check that decoding is working
    final String decodedTestFolder = "decoded_test";

    //actual rule files
    final String actualAddressRules = actualRulesFolder + "/" + "address-rules.clp";
    final String deftemplates = actualRulesFolder + "/" + "deftemplates.clp";
    final String actualPaymentRules = actualRulesFolder + "/" + "payment-rules.clp";
    final String actualQuantityRules = actualRulesFolder + "/" + "quantity-rules.clp";
    final String[] actualFiles = {actualAddressRules, deftemplates, actualPaymentRules, actualQuantityRules};
    //encoded rule files
    final String actualAddressRulesEnc = encodedRulesFolder + "/" + "address-rules-enc.txt";
    final String deftemplatesEnc = encodedRulesFolder + "/" + "deftemplates-enc.txt";
    final String actualPaymentRulesEnc = encodedRulesFolder + "/" + "payment-rules-enc.txt";
    final String actualQuantityRulesEnc = encodedRulesFolder + "/" + "quantity-rules-enc.txt";
    final String[] encodedFiles = {actualAddressRulesEnc, deftemplatesEnc, actualPaymentRulesEnc, actualQuantityRulesEnc};
    //	decoded rule files for testing
    final String actualAddressRulesDec = decodedTestFolder + "/" + "address-rules.clp";
    final String deftemplatesDec = decodedTestFolder + "/" + "deftemplates.clp";
    final String actualPaymentRulesDec = decodedTestFolder + "/" + "payment-rules.clp";
    final String actualQuantityRulesDec = decodedTestFolder + "/" + "quantity-rules.clp";
    final String[] decodedFiles = {actualAddressRulesDec, deftemplatesDec, actualPaymentRulesDec, actualQuantityRulesDec};
    //newline character
    final static String n = System.getProperty("line.separator");

    ////////////CODE TO BE USED BY APPLICATION ///////////////////
    public static String decodeAsString(File encodedFile)
            throws IOException {

        StringBuilder outStr = new StringBuilder();

        try {
            BufferedReader br = new BufferedReader(new FileReader(encodedFile));

            String line = null;
            while ((line = br.readLine()) != null) {
                outStr.append(decodeLine(line)).append(n);

            }

            br.close();

        } catch (Exception e) {
            throw new IOException(e.getMessage());
        }
        return outStr.toString();
    }

    public static String decodeAsString(String encodedFilename)
            throws IOException {
        File in = new File(encodedFilename);
        return decodeAsString(in);
    }

    ////////////// CODE TO BE USED BY TEACHER TO SET UP PROJECT ////////////
    public static void main(String[] args) {
        CodingUtils cu = new CodingUtils();
        try {
            cu.encodeAllActual();
            cu.decodeAllEncodedForTesting();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void encodeAllActual()
            throws IOException {
        for (int i = 0; i < actualFiles.length; ++i) {
            encode(actualFiles[i], encodedFiles[i]);
        }

    }

    public void decodeAllEncodedForTesting()
            throws IOException {
        for (int i = 0; i < actualFiles.length; ++i) {
            decode(encodedFiles[i], decodedFiles[i]);
        }

    }

    ///////////////// HELPER METHODS ////////////////////////
    private void encode(String actualFilename, String encodedFilename)
            throws IOException {
        File in = new File(actualFilename);
        File out = new File(encodedFilename);
        StringBuilder outStr = new StringBuilder();

        try {
            BufferedReader br = new BufferedReader(new FileReader(in));
            BufferedWriter bw = new BufferedWriter(new FileWriter(out));
            String line = null;
            while ((line = br.readLine()) != null) {
                outStr.append(encodeLine(line)).append(n);

            }
            bw.write(outStr.toString());
            br.close();
            bw.close();
        } catch (Exception e) {
            throw new IOException(e.getMessage());
        }

    }

    private void decode(String encodedFilename, String decodedFilename)
            throws IOException {
        File in = new File(encodedFilename);
        File out = new File(decodedFilename);
        StringBuilder outStr = new StringBuilder();

        try {
            BufferedReader br = new BufferedReader(new FileReader(in));
            BufferedWriter bw = new BufferedWriter(new FileWriter(out));
            String line = null;
            while ((line = br.readLine()) != null) {
                outStr.append(decodeLine(line)).append(n);

            }
            bw.write(outStr.toString());
            br.close();
            bw.close();
        } catch (Exception e) {
            throw new IOException(e.getMessage());
        }

    }

    private String encodeLine(String line) {
        StringBuilder sb = new StringBuilder();
        char[] chars = line.toCharArray();

        for (int i = 0; i < chars.length; ++i) {
            char c = chars[i];
            if (c != '\n' && c != '\r') {
                int k = (int) c;
                k++;
                sb.append((char) k);
            }
        }
        return sb.toString();
    }

    private static String decodeLine(String line) {
        StringBuilder sb = new StringBuilder();
        char[] chars = line.toCharArray();

        for (int i = 0; i < chars.length; ++i) {
            char c = chars[i];
            if (c != '\n' && c != '\r') {
                int k = (int) c;
                k--;
                sb.append((char) k);
            }
        }
        return sb.toString();
    }
}
