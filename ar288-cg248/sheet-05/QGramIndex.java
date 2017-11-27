// Copyright 2017, University of Freiburg,
// Chair of Algorithms and Data Structures.
// Authors: Hannah Bast <bast@cs.uni-freiburg.de>,

import java.nio.file.Files;
import java.nio.file.Paths;
import java.io.BufferedReader;
import java.io.IOException;
import java.util.TreeMap;
import java.util.ArrayList;
import java.util.Arrays;

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

  /**
   * Merge two arrays and return the sorted result.
   * @param lists
   * @return int[] sorted result of merging the two arrays.
   */
  int[] mergeLists(int[][] lists) {
    int length = lists[0].length + lists[1].length;
    int[] result = new int[length];
    for (int i = 0; i < lists[0].length; i++) {
      result[i] = lists[0][i];
    }

    for (int i = 0; i < lists[1].length; i++) {
      result[lists[0].length + i] = lists[1][i];
    }
    Arrays.sort(result);
    return result;
  }

  // Compute the prefix edit distance of the two given strings x and y and
  // return it if it is smaller or equal to the given δ. Otherwise return δ + 1.
  //
  // NOTE: The method must run in time O(|x| * (|x| + δ)), as explained in the
  // lecture.
  //
  // TEST CASE:
  //   prefixEditDistance("frei", "frei", 0);
  // RESULT:
  //   0
  //
  // TEST CASE:
  //   prefixEditDistance("frei", "freiburg", 0);
  // RESULT:
  //   0
  //
  // TEST CASE:
  //   prefixEditDistance("frei", "breifurg", 1);
  // RESULT:
  //   1
  //
  // TEST CASE:
  //   prefixEditDistance("freiburg", "stuttgart", 2);
  // RESULT:
  //   3
  int prefixEditDistance(String x, String y, int delta) {
    int[] array = new int[x.length() + delta];
    int minimum = x.length();
    for (int m = 0; m < array.length; m++) {
      array[m] = 0;

      for (int n = 0; n < x.length(); n++) {
        System.out.println(x.charAt(n));
        System.out.println(y.charAt(n));
        if(!(x.charAt(n) == y.charAt(n))) {
          array[m]++;
        }
      }

      minimum = Math.min(array[m], minimum);
    }

    return Math.min(minimum, delta + 1);
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
  ArrayList<Entity> entities;
}
