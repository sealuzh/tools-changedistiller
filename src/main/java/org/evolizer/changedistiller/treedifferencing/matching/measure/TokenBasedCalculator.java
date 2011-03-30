package org.evolizer.changedistiller.treedifferencing.matching.measure;

import java.util.Hashtable;

/**
 * Implementation of a token based string similarity calculator.
 * 
 * @author Beat Fluri
 * 
 */
public class TokenBasedCalculator implements StringSimilarityCalculator {

    private String fSeparator;

    /**
     * Creates a new token based similarity calculator.
     * 
     * @param separator
     *            with which the tokens in the strings are separated
     */
    public TokenBasedCalculator(String separator) {
        fSeparator = separator;
    }

    /**
     * Creates a new token base similarity calculator with whitespace as separator.
     */
    public TokenBasedCalculator() {
        this("\\s+");
    }

    private String removeRegexOccurrences(String string, String regex) {
        String[] leftTmp = string.split(regex);
        String cleanedString = "";
        for (String s : leftTmp) {
            cleanedString += s;
        }
        return cleanedString;
    }

    @Override
    public double calculateSimilarity(String left, String right) {
        String leftString = left;
        String rightString = right;
        if (leftString.startsWith("//") || rightString.startsWith("//")) {
            leftString = removeRegexOccurrences(leftString, "//\\s*");
            rightString = removeRegexOccurrences(rightString, "//\\s*");
        } else if (leftString.startsWith("/*") || rightString.startsWith("/*")) {
            leftString = removeBlockCommentDelimiters(leftString);
            rightString = removeBlockCommentDelimiters(rightString);
        }
        String[] leftTokens = leftString.split(fSeparator);
        String[] rightTokens = rightString.split(fSeparator);
        Hashtable<String, Integer> tokens = createTokenHashtable(leftTokens);
        double match = removeTokensOccuringInRightString(rightTokens, tokens);
        double maximumTokens = Math.max(leftTokens.length, rightTokens.length);
        return match / maximumTokens;
    }

    private double removeTokensOccuringInRightString(String[] rightTokens, Hashtable<String, Integer> tokens) {
        double match = 0.0;
        // delete the tokens in right from the Hashtable
        for (int i = 0; i < rightTokens.length; i++) {
            String token = rightTokens[i];
            if (tokens.containsKey(token)) {
                match++;
                if (tokens.get(token) > 1) {
                    Integer nrTokensForString = tokens.remove(token);
                    tokens.put(token, nrTokensForString - 1);
                } else {
                    tokens.remove(token);
                }
                rightTokens[i] = "";
            }
        }
        return match;
    }

    private Hashtable<String, Integer> createTokenHashtable(String[] leftTokens) {
        Hashtable<String, Integer> tokens = new Hashtable<String, Integer>();

        // fill the Hashtable with the tokens from left
        for (String token : leftTokens) {
            if (tokens.containsKey(token)) {
                Integer nrTokensForString = tokens.remove(token);
                tokens.put(token, nrTokensForString + 1);
            } else {
                tokens.put(token, 1);
            }
        }
        return tokens;
    }

    private String removeBlockCommentDelimiters(String leftString) {
        String result = removeRegexOccurrences(leftString, "/\\*+\\s*");
        return removeBlockCommentEnd(result);
    }

    private String removeBlockCommentEnd(String leftString) {
        String result = leftString;
        try {
            result = leftString.split("\\s*\\*/")[0];
        } catch (ArrayIndexOutOfBoundsException e) {
            result = leftString.replace('/', ' ');
        }
        return result.replace('*', ' ').trim();
    }

}
