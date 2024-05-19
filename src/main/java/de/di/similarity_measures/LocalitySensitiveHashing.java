package de.di.similarity_measures;

import de.di.similarity_measures.helper.MinHash;
import de.di.similarity_measures.helper.Tokenizer;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class LocalitySensitiveHashing implements SimilarityMeasure {

    private final Tokenizer tokenizer;
    private final boolean bagSemantics;
    private final List<MinHash> minHashFunctions;

    public LocalitySensitiveHashing(final Tokenizer tokenizer, final boolean bagSemantics, final int numHashFunctions) {
        assert(tokenizer.getTokenSize() >= numHashFunctions);
        this.tokenizer = tokenizer;
        this.bagSemantics = bagSemantics;
        this.minHashFunctions = new ArrayList<>(numHashFunctions);
        for (int i = 0; i < numHashFunctions; i++) {
            this.minHashFunctions.add(new MinHash(i));
        }
    }

    @Override
    public double calculate(final String string1, final String string2) {
        String[] strings1 = this.tokenizer.tokenize(string1);
        String[] strings2 = this.tokenizer.tokenize(string2);
        return this.calculate(strings1, strings2);
    }

    @Override
    public double calculate(final String[] strings1, final String[] strings2) {
        int numHashFunctions = this.minHashFunctions.size();
        int matchingHashValues = 0;

        // Calculate MinHash signatures for strings1 and strings2
        List<String> signature1 = new ArrayList<>();
        List<String> signature2 = new ArrayList<>();
        for (int i = 0; i < numHashFunctions; i++) {
            String minHash1 = this.minHashFunctions.get(i).hash(strings1);
            String minHash2 = this.minHashFunctions.get(i).hash(strings2);
            signature1.add(minHash1);
            signature2.add(minHash2);
        }

        // Count the number of matching hash values between signatures
        for (int i = 0; i < numHashFunctions; i++) {
            if (signature1.contains(signature2.get(i))) {
                matchingHashValues++;
            }
        }

        // Calculate Jaccard similarity
        double jaccardSimilarity = (double) matchingHashValues / numHashFunctions;

        // Adjust for bag semantics
        if (bagSemantics) {
            // Calculate the union of unique hash values
            Set<String> union = new HashSet<>(signature1);
            union.addAll(signature2);
            // Calculate the intersection of unique hash values
            Set<String> intersection = new HashSet<>(signature1);
            intersection.retainAll(signature2);
            // Adjust similarity using bag semantics
            jaccardSimilarity = (double) intersection.size() / union.size();
        }

        return jaccardSimilarity;
    }
}
