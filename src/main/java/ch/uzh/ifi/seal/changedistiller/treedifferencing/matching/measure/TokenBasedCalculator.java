package ch.uzh.ifi.seal.changedistiller.treedifferencing.matching.measure;

/*
 * #%L
 * ChangeDistiller
 * %%
 * Copyright (C) 2011 - 2013 Software Architecture and Evolution Lab, Department of Informatics, UZH
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */

import java.util.Hashtable;
import java.util.Map;

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
        StringBuilder cleanedString = new StringBuilder();
        for (String s : leftTmp) {
            cleanedString.append(s);
        }
        return cleanedString.toString();
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
        Map<String, Integer> tokens = createTokenHashtable(leftTokens);
        double match = removeTokensOccuringInRightString(rightTokens, tokens);
        double maximumTokens = Math.max(leftTokens.length, rightTokens.length);
        return match / maximumTokens;
    }

    private double removeTokensOccuringInRightString(String[] rightTokens, Map<String, Integer> tokens) {
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

    private Map<String, Integer> createTokenHashtable(String[] leftTokens) {
        Map<String, Integer> tokens = new Hashtable<String, Integer>();

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
