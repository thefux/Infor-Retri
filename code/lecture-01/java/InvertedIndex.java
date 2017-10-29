// Copyright 2017 University of Freiburg
// Claudius Korzen <korzen@cs.uni-freiburg.de>

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * A simple inverted index as explained in lecture 1.
 */
public class InvertedIndex {
  /**
   * The inverted lists.
   */
  protected Map<String, List<Integer>> invertedLists;

  /**
   * Creates an empty inverted index.
   */
  public InvertedIndex() {
    this.invertedLists = new HashMap<>();
  }

  /**
   * Constructs the inverted index from given file (one record per line).
   * 
   * @param file
   *        The path of the file to process.
   */
  public void readFromFile(String file) {
    try (BufferedReader reader = Files.newBufferedReader(Paths.get(file))) {
      int recordId = 0;
      String line;
      while ((line = reader.readLine()) != null) {
        String[] words = line.split("[^A-Za-z]+");
        for (String word : words) {
          word = word.toLowerCase().trim();

          // Ignore the word if it is empty.
          if (word.isEmpty()) {
            continue;
          }

          // If word seen first time, create inverted list.
          if (!this.invertedLists.containsKey(word)) {
            this.invertedLists.put(word, new ArrayList<>());
          }
          // Append record id to inverted list.
          this.invertedLists.get(word).add(recordId);
        }
        recordId++;
      }
    } catch (IOException e) {
      System.err.println("An error occured on reading the file:");
      e.printStackTrace();
    }
  }

  /**
   * Returns the words within this inverted index.
   */
  public Set<String> getWords() {
    return this.invertedLists.keySet();
  }

  /**
   * Returns the inverted list for the given word. Returns null if this inverted
   * index doesn't contain an inverted list for the word.
   * 
   * @param word
   *        The word to process.
   */
  public List<Integer> getInvertedList(String word) {
    return this.invertedLists.get(word);
  }
}
