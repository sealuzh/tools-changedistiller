/*
 * Copyright 2009 University of Zurich, Switzerland
 *
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
 */
package org.evolizer.changedistiller.treedifferencing.matching.measure;

import java.util.Hashtable;

/**
 * Implementation of a token based string similarity calculator.
 * 
 * @author fluri
 * 
 */
public class TokenBasedCalculator implements IStringSimilarityCalculator {

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
     * 
     */
    public TokenBasedCalculator() {
        this("\\s+");
    }

    /**
     * {@inheritDoc}
     */
    public double calculateSimilarity(String left, String right) {
        String leftString = left;
        String rightString = right;
        if (leftString.subSequence(0, 2).equals("//")) {
            String splitRegex = "//\\s*";
            String[] leftTmp = leftString.split(splitRegex);
            String[] rightTmp = rightString.split(splitRegex);
            leftString = "";
            rightString = "";
            for (String s : leftTmp) {
                leftString += s;
            }
            for (String s : rightTmp) {
                rightString += s;
            }
        } else if (leftString.subSequence(0, 2).equals("/*")) {
            String splitRegex = "/\\*+\\s*";
            String[] leftTmp = leftString.split(splitRegex);
            String[] rightTmp = rightString.split(splitRegex);
            leftString = "";
            rightString = "";
            for (String s : leftTmp) {
                leftString += s;
            }
            for (String s : rightTmp) {
                rightString += s;
            }

            splitRegex = "\\s*\\*/";
            try {
                leftString = leftString.split(splitRegex)[0];
            } catch (ArrayIndexOutOfBoundsException e) {
                leftString = leftString.replace('/', ' ');
            }
            try {
                rightString = rightString.split(splitRegex)[0];
            } catch (ArrayIndexOutOfBoundsException e) {
                rightString = rightString.replace('/', ' ');
            }

            leftString = leftString.replace('*', ' ').trim();
            rightString = rightString.replace('*', ' ').trim();
        }
        String[] leftTokens = leftString.split(fSeparator);
        String[] rightTokens = rightString.split(fSeparator);

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

        double maximumTokens = Math.max(leftTokens.length, rightTokens.length);
        return match / maximumTokens;
    }

}
