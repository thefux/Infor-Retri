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
  }

  // Build a q-gram index from the entity names of the given file. The expected
  // file format is one line per entity with tab-separated columns. The first
  // column contains the entity name (needed for indexing), the second column
  // a popularity score (needed for ranking), the third column a short
  // description of the entity (needed only to enrich the output) and
  // the remaining columns some additional information about the entity (needed
  // only for some optional stuff). The first line contains a header with a
  // short description of each column.
  //
  // Before computing the q-grams, normalize each string by lowercasing
  // and removing all non-word characters (including whitespace) as shown in
  // the lecture.
  //
  // Pay attention to either keep duplicates in the lists or keep a count of
  // the number of each id, e.g. represented by a pair (id, num).
  //
  // TEST CASE:
  //   QGramIndex index(3);
  //   index.buildFromFile("example.tsv");
  //   index.invertedLists;
  // RESULT:
  //   {
  //     "$$b": [2],
  //     "$$f": [1],
  //     "$br": [2],
  //     "$fr": [1],
  //     "bre": [2],
  //     "fre": [1],
  //     "rei": [1, 2]
  //   }
  //   OR
  //   {
  //     "$$b": [(2, 1)],
  //     "$$f": [(1, 1)],
  //     "$br": [(2, 1)],
  //     "$fr": [(1, 1)],
  //     "bre": [(2, 1)],
  //     "fre": [(1, 1)],
  //     "rei": [(1, 1), (2, 1)]
  //   }
  //
  // TEST CASE:
  //   QGramIndex index(3);
  //   index.buildFromFile("example.tsv");
  //   index.entities;
  // RESULT:
  //   [
  //     Entity(name="frei", score=3, description="a word"),
  //     Entity(name="brei", score=2, description="another word")
  //   ]
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
    }
  }

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
  // Array<Entity> entities;
}
