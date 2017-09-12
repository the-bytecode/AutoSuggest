package com.techexplore.autosuggest.framework.processor;

import com.techexplore.autosuggest.domain.AutoSuggestRequest;
import com.techexplore.autosuggest.framework.ApplicationConstants;
import com.techexplore.autosuggest.framework.searchalgorithm.TrieNode;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by chandrashekar.v on 9/12/2017.
 */
@Service(value = ApplicationConstants.FUZZY)
public class FuzzyAutoSuggestProcessor extends AbstractAutoSuggestProcessor {

    private AtomicInteger counter = new AtomicInteger();

    @Override
    public List<String> search(AutoSuggestRequest request, final TrieNode root) {
        getCounter();
        List<String> suggestions = new ArrayList<>();
        processFuzzySearch(request.getFuzziThreshold(), request.getStart(), root, request.getAtmost(), suggestions);
        setCounter(null);
        return suggestions;
    }

    private void processFuzzySearch(int fuzziness, String word, final TrieNode root, final int atMost, List<String> suggestions) {

        int[] currentRow = new int[word.length() + 1];
        for (int i = 0; i < currentRow.length; i++) {
            currentRow[i] = i;
        }

        TrieNode currentNode = root;

        for (Map.Entry<Character, TrieNode> entry : currentNode.getChildren().entrySet())
            searchRecursive(entry.getValue(), entry.getKey(), word, currentRow, fuzziness,
                    word.toCharArray(), atMost, suggestions);

    }

    private void searchRecursive(TrieNode node, Character key, String word, int[] previousRow, int fuzziness, char[] wordChars,
                                 final int atMost, List<String> suggestions) {
        if (getCounter().get() >= atMost)
            return;

        int columns[] = new int[word.length() + 1];
        int[] currentRow = new int[word.length() + 1];
        currentRow[0] = previousRow[0] + 1;

        int index = 1;
        int insertCost = 0;
        int delCost = 0;
        int replaceCost = 0;
        for (int i = 1; i < columns.length; i++) {
            insertCost = currentRow[i - 1] + 1;
            delCost = previousRow[i] + 1;

            if (wordChars[i - 1] != key.charValue()) {
                replaceCost = previousRow[i - 1] + 1;
            } else {
                replaceCost = previousRow[i - 1];
            }
            currentRow[index++] = Math.min(Math.min(insertCost, delCost), replaceCost);
        }

        if (currentRow[currentRow.length - 1] <= fuzziness && StringUtils.isNoneBlank(node.getWord()) &&
                StringUtils.trimToEmpty(node.getWord()).length() >= word.length()) {
            suggestions.add(node.getWord());
            getCounter().getAndIncrement();
        }

        OptionalInt min = Arrays.stream(currentRow).min();
        if (min.getAsInt() <= fuzziness) {
            {
                for (Map.Entry<Character, TrieNode> entry : node.getChildren().entrySet()) {
                    searchRecursive(entry.getValue(), entry.getKey(), word, currentRow, fuzziness, word.toCharArray(), atMost, suggestions);
                }
            }
        }

    }

    public AtomicInteger getCounter() {
        if (Objects.isNull(counter)) {
            counter = new AtomicInteger(0);
        }
        return counter;
    }

    public void setCounter(AtomicInteger counter) {
        this.counter = counter;
    }
}
