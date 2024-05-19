package de.di.similarity_measures;

import de.di.similarity_measures.helper.Tokenizer;
import lombok.AllArgsConstructor;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@AllArgsConstructor
public class Jaccard implements SimilarityMeasure {

    // The tokenizer that is used to transform string inputs into token lists.
    private final Tokenizer tokenizer;

    // A flag indicating whether the Jaccard algorithm should use set or bag semantics for the similarity calculation.
    private final boolean bagSemantics;

    /**
     * Calculates the Jaccard similarity of the two input strings. Note that the Jaccard similarity may use set or
     * multiset, i.e., bag semantics for the union and intersect operations. The maximum Jaccard similarity with
     * multiset semantics is 1/2 and the maximum Jaccard similarity with set semantics is 1.
     * @param string1 The first string argument for the similarity calculation.
     * @param string2 The second string argument for the similarity calculation.
     * @return The multiset Jaccard similarity of the two arguments.
     */

    @Override
    public double calculate(String string1, String string2) {
        string1 = (string1 == null) ? "" : string1;
        string2 = (string2 == null) ? "" : string2;

        String[] strings1 = this.tokenizer.tokenize(string1);
        String[] strings2 = this.tokenizer.tokenize(string2);
        return this.calculate(strings1, strings2);
    }

    /**
     * Calculates the Jaccard similarity of the two string lists. Note that the Jaccard similarity may use set or
     * multiset, i.e., bag semantics for the union and intersect operations. The maximum Jaccard similarity with
     * multiset semantics is 1/2 and the maximum Jaccard similarity with set semantics is 1.
     * @param strings1 The first string list argument for the similarity calculation.
     * @param strings2 The second string list argument for the similarity calculation.
     * @return The multiset Jaccard similarity of the two arguments.
     */
    @Override
    public double calculate(String[] strings1, String[] strings2) {
        double jaccardSimilarity = 0;

        // Convert arrays to sets if set semantics is required
        Set<String> set1, set2;
        if (!bagSemantics) {
            set1 = new HashSet<>(Arrays.asList(strings1));
            set2 = new HashSet<>(Arrays.asList(strings2));
        } else {
            // For bag semantics, create a frequency map of tokens
            Map<String, Integer> frequencyMap1 = createFrequencyMap(strings1);
            Map<String, Integer> frequencyMap2 = createFrequencyMap(strings2);

            // Convert frequency maps to sets for easier intersection and union calculations
            set1 = frequencyMap1.keySet();
            set2 = frequencyMap2.keySet();
        }

        // Calculate intersection size
        Set<String> intersection = new HashSet<>(set1);
        intersection.retainAll(set2);
        int intersectionSize = intersection.size();

        // Calculate union size
        int unionSize;
        if (bagSemantics) {
            // For bag semantics, union includes all elements
            unionSize = Math.max(set1.size(), set2.size());
        } else {
            // For set semantics, union is distinct elements
            Set<String> union = new HashSet<>(set1);
            union.addAll(set2);
            unionSize = union.size();
        }

        // Calculate Jaccard similarity
        if (unionSize != 0) {
            jaccardSimilarity = (double) intersectionSize / unionSize;
        }
        return jaccardSimilarity;
    }

    private Map<String, Integer> createFrequencyMap(String[] strings) {
        Map<String, Integer> frequencyMap = new HashMap<>();
        for (String str : strings) {
            frequencyMap.put(str, frequencyMap.getOrDefault(str, 0) + 1);
        }
        return frequencyMap;
    }
}
