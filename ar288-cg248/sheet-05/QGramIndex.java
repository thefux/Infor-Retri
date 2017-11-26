// Copyright 2017, University of Freiburg,
// Chair of Algorithms and Data Structures.
// Authors: Hannah Bast <bast@cs.uni-freiburg.de>,

import java.nio.file.Files;
import java.nio.file.Paths;
import java.io.BufferedReader;
import java.io.IOException;
import java.util.TreeMap;
import java.util.ArrayList;

/**
 * First steps towards a q-gram index, written during class.
 */
public class QGramIndex {

  /**
   * Create an empty QGramIndex.
   */
  public QGramIndex(int q) {
    this.q = q;
    this.padding = new String(new char[q - 1]).replace("\u0000", "$");
    this.invertedLists = new TreeMap<>();
    this.entities = new ArrayList<Entity>();
  }

  /**
   * Build index from given file (one line per entity, see ES5).
   */
  public void buildFromFile(String fileName) throws IOException {
    BufferedReader br = Files.newBufferedReader(Paths.get(fileName));
    String line;
    int wordId = 0;
    // Ignore first line.
    br.readLine();
    // Iterate through the remaining lines.
    while ((line = br.readLine()) != null) {
      String word = line.split("\t")[0];
      wordId += 1;
      for (String qGram : computeQGrams(word)) {
        if (!invertedLists.containsKey(qGram)) {
          invertedLists.put(qGram, new ArrayList<>());
        }
        invertedLists.get(qGram).add(wordId);
      }
      entities.add(new Entity(word, Integer.parseInt(line.split("\t")[1]),
              line.split("\t")[2]));
    }
  }

  // Merge the given inverted lists. Pay attention to either keep duplicates in
  // the result list or keep a count of the number of each id.
  //
  // NOTE: It is ok, if you do this merging by simply concatenating the lists
  // and then sort the concatenation. That is, you do not have to make use of
  // the fact, that the lists are already sorted.
  //
  // TEST CASE:
  //   mergeLists([1, 1, 3, 5], [2, 3, 3, 9, 9]);
  // RESULT:
  //   [1, 1, 2, 3, 3, 3, 5, 9, 9]
  //   OR
  //   [(1, 2), (2, 1), (3, 3), (5, 1), (9, 2)]
  Array<int> mergeLists(Array<Array<int>> lists) {

  }
//  OR
//  Array<Pair<int,int>> mergeLists(Array<Array<int>> lists);

  /**
   * Compute q-grams for padded, normalized version of given string.
   */
  public ArrayList<String> computeQGrams(String word) {
    ArrayList<String> result = new ArrayList<>();
    word = padding + normalize(word) + padding;
    for (int i = 0; i < word.length() - q + 1; i++) {
      result.add(word.substring(i, i + q));
    }
    return result;
  }

  /**
   * Normalize the given string (remove non-word characters and lower case). In
   * the lecture, this was part of the qGrams method, but we also need it as a
   * separate method when computing the EDs for the remaining candidates.
   */
  public static String normalize(String str) {
    return str.toLowerCase().replaceAll("\\W", "");
  }

  // The q from the q-gram index.
  int q;

  // The padding (q - 1 times $).
  String padding;

  // The inverted lists (one per q-gram).
  TreeMap<String, ArrayList<Integer>> invertedLists;

  // The entities, as given in the entity file.
  ArrayList<Entity> entities;
}
