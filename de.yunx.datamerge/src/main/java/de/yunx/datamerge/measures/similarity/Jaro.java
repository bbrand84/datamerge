package de.yunx.datamerge.measures.similarity;

public class Jaro extends Similarity{
    /**
     * gets the similarity of the two strings using Jaro distance.
     *
     * from http://stackoverflow.com/questions/2848807/optimizing-jaro-winkler-algorithm
     * @param string1 the first input string
     * @param string2 the second input string
     * @return a value between 0-1 of the similarity
     */
    public double getSimilarity(final String string1, final String string2) {

        //get half the length of the string rounded up - (this is the distance used for acceptable transpositions)
        final int halflen = ((Math.min(string1.length(), string2.length())) / 2) + ((Math.min(string1.length(), string2.length())) % 2);

        //get common characters
        final StringBuffer common1 = getCommonCharacters(string1, string2, halflen);
        final StringBuffer common2 = getCommonCharacters(string2, string1, halflen);

        //check for zero in common
        if (common1.length() == 0 || common2.length() == 0) {
            return 0.0f;
        }

        //check for same length common strings returning 0.0f is not the same
        if (common1.length() != common2.length()) {
            return 0.0f;
        }

        //get the number of transpositions
        int transpositions = 0;
        int n=common1.length();
        for (int i = 0; i < n; i++) {
            if (common1.charAt(i) != common2.charAt(i))
                transpositions++;
        }
        transpositions /= 2.0f;

        //calculate jaro metric
        return (common1.length() / ((double) string1.length()) +
                common2.length() / ((double) string2.length()) +
                (common1.length() - transpositions) / ((double) common1.length())) / 3.0f;
    }

    /**
     * returns a string buffer of characters from string1 within string2 if they are of a given
     * distance seperation from the position in string1.
     *
     * @param string1
     * @param string2
     * @param distanceSep
     * @return a string buffer of characters from string1 within string2 if they are of a given
     *         distance seperation from the position in string1
     */
    private static StringBuffer getCommonCharacters(final String string1, final String string2, final int distanceSep) {
        //create a return buffer of characters
        final StringBuffer returnCommons = new StringBuffer();
        //create a copy of string2 for processing
        final StringBuffer copy = new StringBuffer(string2);
        //iterate over string1
        int n=string1.length();
        int m=string2.length();
        for (int i = 0; i < n; i++) {
            final char ch = string1.charAt(i);
            //set boolean for quick loop exit if found
            boolean foundIt = false;
            //compare char with range of characters to either side

            for (int j = Math.max(0, i - distanceSep); !foundIt && j < Math.min(i + distanceSep, m - 1); j++) {
                //check if found
                if (copy.charAt(j) == ch) {
                    foundIt = true;
                    //append character found
                    returnCommons.append(ch);
                    //alter copied string2 for processing
                    copy.setCharAt(j, (char)0);
                }
            }
        }
        return returnCommons;
    }
    
    public static void main(String[] args) {
    	Jaro j = new Jaro();
    	System.out.println(j.getSimilarity("SMELECTRONICS", "ELECTRONICS"));
    	System.out.println(j.getSimilarity("ELECTRONICS", "ELECTRONICS"));
    	System.out.println(j.getSimilarity("SELECTRONICS", "ELECTRONICS"));
    	System.out.println(j.getSimilarity("ELECTRONICS", "ELECTRONICSo"));
    }
}