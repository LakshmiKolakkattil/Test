package de.di.similarity_measures;

import lombok.AllArgsConstructor;

import java.util.Arrays;

@AllArgsConstructor
public class Levenshtein implements SimilarityMeasure {

    public static int min(int... numbers) {
        return Arrays.stream(numbers).min().orElse(Integer.MAX_VALUE);
    }

    private final boolean withDamerau;

    @Override
    public double calculate(final String string1, final String string2) {
        double levenshteinSimilarity;

        if (withDamerau) {
            levenshteinSimilarity = calculateDamerauLevenshtein(string1, string2);
        } else {
            levenshteinSimilarity = calculateLevenshtein(string1, string2);
        }

        return levenshteinSimilarity;
    }

    private double calculateLevenshtein(final String string1, final String string2) {
        int len1 = string1.length();
        int len2 = string2.length();

        int[] prevRow = new int[len1 + 1];
        int[] currRow = new int[len1 + 1];

        for (int i = 0; i <= len1; i++) {
            prevRow[i] = i;
        }

        for (int j = 1; j <= len2; j++) {
            currRow[0] = j;
            for (int i = 1; i <= len1; i++) {
                int cost = (string1.charAt(i - 1) == string2.charAt(j - 1)) ? 0 : 1;
                currRow[i] = min(currRow[i - 1] + 1, prevRow[i] + 1, prevRow[i - 1] + cost);
            }
            System.arraycopy(currRow, 0, prevRow, 0, len1 + 1);
        }

        int levenshteinDistance = prevRow[len1];
        double maxLength = Math.max(len1, len2);
        double normalizedDistance = levenshteinDistance / maxLength;
        return 1 - normalizedDistance;
    }

    private double calculateDamerauLevenshtein(final String string1, final String string2) {
        int len1 = string1.length();
        int len2 = string2.length();

        int[][] dp = new int[len1 + 1][len2 + 1];

        for (int i = 0; i <= len1; i++) {
            dp[i][0] = i;
        }
        for (int j = 0; j <= len2; j++) {
            dp[0][j] = j;
        }

        for (int i = 1; i <= len1; i++) {
            for (int j = 1; j <= len2; j++) {
                int cost = (string1.charAt(i - 1) == string2.charAt(j - 1)) ? 0 : 1;
                dp[i][j] = min(dp[i - 1][j] + 1,  // deletion
                        dp[i][j - 1] + 1,          // insertion
                        dp[i - 1][j - 1] + cost);  // substitution

                if (i > 1 && j > 1 &&
                        string1.charAt(i - 1) == string2.charAt(j - 2) &&
                        string1.charAt(i - 2) == string2.charAt(j - 1)) {
                    dp[i][j] = min(dp[i][j], dp[i - 2][j - 2] + cost); // transposition
                }
            }
        }

        int damerauLevenshteinDistance = dp[len1][len2];
        double maxLength = Math.max(len1, len2);
        double normalizedDistance = damerauLevenshteinDistance / maxLength;
        return 1 - normalizedDistance;
    }

    @Override
    public double calculate(final String[] strings1, final String[] strings2) {
        double levenshteinSimilarity = 0;

        int[] upperupperLine = new int[strings1.length + 1];   // line for Damerau lookups
        int[] upperLine = new int[strings1.length + 1];        // line for regular Levenshtein lookups
        int[] lowerLine = new int[strings1.length + 1];        // line to be filled next by the algorithm

        // Fill the first line with the initial positions (= edits to generate string1 from nothing)
        for (int i = 0; i <= strings1.length; i++)
            upperLine[i] = i;

        for (int j = 1; j <= strings2.length; j++) {
            lowerLine[0] = j;
            for (int i = 1; i <= strings1.length; i++) {
                int cost = strings1[i - 1].equals(strings2[j - 1]) ? 0 : 1;
                lowerLine[i] = min(lowerLine[i - 1] + 1, upperLine[i] + 1, upperLine[i - 1] + cost);
            }
            // Swap the lines
            int[] temp = upperLine;
            upperLine = lowerLine;
            lowerLine = temp;
        }

        // Choose the appropriate line to get the Levenshtein distance
        int levenshteinDistance = upperLine[strings1.length];

        // Normalize the Levenshtein distance
        double maxLength = Math.max(strings1.length, strings2.length);
        double normalizedDistance = levenshteinDistance / maxLength;

        // Calculate the Levenshtein similarity
        levenshteinSimilarity = 1 - normalizedDistance;

        return levenshteinSimilarity;
    }
}
