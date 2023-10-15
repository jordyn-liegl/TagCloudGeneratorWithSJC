import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * This program inputs a text file and outputs it as an HTML file that has the
 * number of word's occurrence from the input.
 *
 * @author Kamilia Kamal Arifin and Jordyn Liegl
 *
 */
public final class TagCloudGenerator {

    /**
     * * String of separators.
     */
    private static final String SEPARATORS = " \t\n\r,-.!?[]';:/()";

    /**
     * Comparing the values (counts) in the pairs.
     */
    private static class NumericalSort
            implements Serializable, Comparator<Map.Entry<String, Integer>> {

        @Override
        public int compare(Map.Entry<String, Integer> o1,
                Map.Entry<String, Integer> o2) {
            Integer i1 = o1.getValue();
            Integer i2 = o2.getValue();
            int comp = i2.compareTo(i1);
            if (comp == 0) {
                String s1 = o1.getKey();
                String s2 = o2.getKey();
                comp = s1.compareToIgnoreCase(s2);
            }
            return comp;
        }
    }

    /**
     * Comparing the keys (words) in the pairs.
     */
    private static class AlphabeticalSort
            implements Serializable, Comparator<Map.Entry<String, Integer>> {

        @Override
        public int compare(Map.Entry<String, Integer> o1,
                Map.Entry<String, Integer> o2) {
            String s1 = o1.getKey();
            String s2 = o2.getKey();
            int comp = s1.compareToIgnoreCase(s2);
            if (comp == 0) {
                Integer i1 = o1.getValue();
                Integer i2 = o2.getValue();
                comp = i2.compareTo(i1);
            }
            return comp;
        }
    }

    /**
     * Private constructor so this utility class cannot be instantiated.
     */
    private TagCloudGenerator() {
    }

    /**
     * Outputs the header and corresponding links in the generated HTML file.
     *
     * @param out
     *            the output text file
     * @param fileName
     *            the filename of input file
     * @param num
     *            the given number of words by the user
     * @updates out
     */
    public static void headerHTML(PrintWriter out, String fileName,
            String num) {
        /*
         * Title.
         */
        out.println("<html>");
        out.println("<head>");
        out.println(
                "<title> Top " + num + " words in " + fileName + "</title>");

        /*
         * Links.
         */
        out.println(
                "<link href=\"http://web.cse.ohio-state.edu/software/2231/web-sw2/assignments/projects/tag-cloud-generator/data/tagcloud.css\">");
        out.println(
                "<link href=\"doc/tagcloud.css\" rel=\"stylesheet\" type=\"text/css\">");

        /*
         * Header.
         */
        out.println("</head>");
        out.println("<body>");
        out.println("<h2> Top " + num + " words in " + fileName + "</h2>");
        out.println("<hr>");
        out.println("<div class =\"cdiv\">");
        out.println("<p class =\"cbox\">");
    }

    /**
     * Generates the pairs of words and counts in the given file into the given
     * map.
     *
     * @param inFile
     *            the given file
     * @param wordsMap
     *            the Map to be replaced
     * @replaces wordsMap
     */
    public static void generateMap(BufferedReader inFile,
            Map<String, Integer> wordsMap) throws IOException {

        /*
         * Add the word and its count to the Map
         */
        String line = "";

        try {
            line = inFile.readLine();

            while (line != null) {

                int pos = 0;
                int len = line.length();
                while (pos < len) {

                    String word = nextWordOrSeparator(line, pos).toLowerCase();
                    if (SEPARATORS.indexOf(word.charAt(0)) < 0) {
                        if (wordsMap.containsKey(word)) {
                            int val = wordsMap.get(word);
                            val++;
                            wordsMap.put(word, val);
                        } else {
                            wordsMap.put(word, 1);
                        }
                    }
                    pos += word.length();
                }
                line = inFile.readLine();
            }
        } catch (IOException e) {
            System.err.println("Error reading file.");
        }

        try {
            inFile.close();
        } catch (IOException e) {
            System.err.println("Error closing file.");
        }
    }

    /**
     * Returns the first "word" (maximal length string of characters not in
     * {@code separators}) or "separator string" (maximal length string of
     * characters in {@code separators}) in the given {@code text} starting at
     * the given {@code position}.
     *
     * @param text
     *            the {@code String} from which to get the word or separator
     *            string
     * @param position
     *            the starting index
     * @return the first word or separator string found in {@code text} starting
     *         at index {@code position}
     * @requires 0 <= position < |text|
     * @ensures <pre>
     * nextWordOrSeparator =
     *   text[position, position + |nextWordOrSeparator|)  and
     * if entries(text[position, position + 1)) intersection separators = {}
     * then
     *   entries(nextWordOrSeparator) intersection separators = {}  and
     *   (position + |nextWordOrSeparator| = |text|  or
     *    entries(text[position, position + |nextWordOrSeparator| + 1))
     *      intersection separators /= {})
     * else
     *   entries(nextWordOrSeparator) is subset of separators  and
     *   (position + |nextWordOrSeparator| = |text|  or
     *    entries(text[position, position + |nextWordOrSeparator| + 1))
     *      is not subset of separators)
     * </pre>
     */
    public static String nextWordOrSeparator(String text, int position) {
        assert text != null : "Violation of: text is not null";
        assert 0 <= position : "Violation of: 0 <= position";
        assert position < text.length() : "Violation of: position < |text|";

        int endPos = position + 1;

        while (endPos < text.length()
                && (SEPARATORS.indexOf(text.charAt(position)) < 0 == SEPARATORS
                        .indexOf(text.charAt(endPos)) < 0)) {
            endPos++;
        }
        return text.substring(position, endPos);
    }

    /**
     * With a SortingMachine, the map will first be sorted by count then
     * alphabetically. For the given number of words, then print out the word
     * and its corresponding size.
     *
     * @param wordsMap
     *            map of words and counts
     * @param num
     *            the given number of words printed
     * @param out
     *            the HTML file that's being written
     * @updates out
     */
    private static void sortTagCloud(Map<String, Integer> wordsMap, int num,
            PrintWriter out) {

        /*
         * Create set of map entries.
         */
        Set<Map.Entry<String, Integer>> wordsSet = wordsMap.entrySet();

        /*
         * Create a list from the set.
         */
        List<Map.Entry<String, Integer>> sortList = new ArrayList<Map.Entry<String, Integer>>(
                wordsSet);

        /*
         * Clear set.
         */
        wordsSet.clear();

        /*
         * If the given number of words is greater than the total number of
         * words in the input file, than the max number of words in the list
         * will be the size of the list.
         */
        int maxNum = num;
        if (num > sortList.size()) {
            maxNum = sortList.size();
        }

        /*
         * Sort the list by count and ensure there are only num of entries.
         */
        sortList.sort(new NumericalSort());
        sortList = sortList.subList(0, maxNum);

        /*
         * Add the sorted list to the wordsMap.
         */
        for (int i = 0; i < maxNum; i++) {
            String key = sortList.get(i).getKey();
            Integer value = sortList.get(i).getValue();
            wordsMap.put(key, value);
        }

        /*
         * Get the largest and smallest for fontSize.
         */
        int smallest = sortList.get(maxNum - 1).getValue();
        int largest = sortList.get(0).getValue();

        /*
         * Sort the list alphabetically.
         */
        sortList.sort(new AlphabeticalSort());

        /*
         * Get the font size of each word and print it to the HTML file.
         */
        int i = 0;
        while (i < maxNum) {
            String font = fontSize(largest, smallest,
                    sortList.get(i).getValue());

            out.println("<span style=\"cursor:default\" class=\"" + font
                    + "\" title=\"count: " + sortList.get(i).getValue() + "\">"
                    + sortList.get(i).getKey() + "</span>");
            i++;
        }
    }

    /**
     * Get the font size based on the highest, smallest and current count value.
     *
     * @param largest
     *            the largest count in the map
     * @param smallest
     *            the smallest count in the map
     * @param count
     *            the value of the word
     * @return "f" + font
     */
    public static String fontSize(int largest, int smallest, int count) {
        final int maxFont = 48;
        final int minFont = 11;
        int font = maxFont - minFont;

        /*
         * Calculate the font size based on the max and min font sizes, the max
         * and min counts, and the given count.
         */
        if (smallest != largest) {
            font = (((maxFont - minFont) * (count - smallest))
                    / (largest - smallest)) + minFont;
        } else {
            font = maxFont;
        }
        /*
         * The font given in the string format.
         */
        return "f" + font;
    }

    /**
     * Outputs the footer in the generated HTML file.
     *
     * @param out
     *            the HTML file
     * @updates out
     */
    public static void footerHTML(PrintWriter out) {
        out.println("</p>");
        out.println("</div>");
        out.println("</body>");
        out.println("</html>");

    }

    /**
     * Main method.
     *
     * @param args
     *            the command line arguments
     */
    public static void main(String[] args) {

        BufferedReader in = new BufferedReader(
                new InputStreamReader(System.in));

        /*
         * Asks user for input file, output file, and number of words. Also open
         * the input and output files
         */
        System.out.println("Enter name of input file: ");
        String fileName = "";
        try {
            fileName = in.readLine();
        } catch (IOException e) {
            System.err.println("Error reading input stream");
            return;
        }

        BufferedReader inFile = null;
        try {
            inFile = new BufferedReader(new FileReader(fileName));
        } catch (IOException e) {
            System.err.println("Error opening file");
            return;
        }

        System.out.println("Enter name of output file: ");
        String htmlName = "";
        try {
            htmlName = in.readLine();
        } catch (IOException e) {
            System.err.println("Error reading input stream");
            return;
        }

        PrintWriter outFile = null;
        try {
            outFile = new PrintWriter(
                    new BufferedWriter(new FileWriter(htmlName)));
        } catch (IOException e) {
            System.err.println("Error opening file");
            return;
        }

        System.out
                .println("Enter number of words to be included in tag cloud: ");
        String num = "";
        try {
            num = in.readLine();
        } catch (IOException e) {
            System.err.println("Error reading input stream");
            return;
        }

        /*
         * Print the header for the html file.
         */
        headerHTML(outFile, fileName, num);

        /*
         * Initialize, generate, and sort the map to be printed to the tag
         * cloud.
         */
        Map<String, Integer> wordsMap = new HashMap<>();

        try {
            generateMap(inFile, wordsMap);
        } catch (IOException e1) {
            System.err.println("generateMap was unable to be called");
        }
        sortTagCloud(wordsMap, Integer.parseInt(num), outFile);

        /*
         * Print the footer for the html file.
         */
        footerHTML(outFile);

        /*
         * Close input and output streams.
         */
        try {
            in.close();
        } catch (IOException e) {
            System.err.println("Error closing file");
        }

        try {
            inFile.close();
        } catch (IOException e) {
            System.err.println("Error closing file");
        }

        outFile.close();
    }
}
