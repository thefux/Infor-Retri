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

  /**
   * Return the editing distance of two Strings and return delta + 1 if it is
   * less than the editing distance.
   * @param x string 1 (the shorter string)
   * @param y string 2 (the longer string)
   * @param delta
   * @return
   */
  int prefixEditDistance(String x, String y, int delta) {
    if (y.length() < x.length()) {
      return prefixEditDistance(y, x, delta);
    }

    int[] array = new int[x.length() + delta];
    int minimum = x.length();
    for (int m = 0; m < array.length; m++) {
      array[m] = 0;

      for (int n = 0; n < x.length(); n++) {
        if (!(x.charAt(n) == y.charAt(n))) {
          array[m]++;
        }
      }

      minimum = Math.min(array[m], minimum);
    }

    return Math.min(minimum, delta + 1);
  }

  // Find all entities y with PED(x, y) ≤ δ for the given string x and a given
  // integer δ. First use the q-gram index to exclude all entities that do not
  // have a sufficient number of q-grams in common with x, as explained in the
  // lecture. Then, compute the PED only for the remaining candidate entities.
  // The method should record the number of PED computations as well. Return a
  // pair (matches, num_ped_computations), where (1) 'matches' is a list of
  // (entity, ped) pairs, where 'entity' is a matching entity y with
  // PED(x, y) ≤ δ and 'ped' is the actual PED value; (2) 'num_ped_computations'
  // is the number of PED computations done while computing the result.
  //
  // TEST CASE:
  //   QGramIndex index(3);
  //   index.buildFromFile("example.tsv");
  //   index.findMatches("frei", 0);
  // RESULT:
  //   ([(Entity(name="frei", score=3, description="a word"), 0)], 1)
  //
  // TEST CASE:
  //   QGramIndex index(3);
  //   index.buildFromFile("example.tsv");
  //   index.findMatches("frei", 2);
  // RESULT:
  //   ([(Entity(name="frei", score=3, description="a word"), 0),
  //     (Entity(name="brei", score=2, description="another word"), 1)], 2)
  //
  // TEST CASE:
  //   QGramIndex index(3);
  //   index.buildFromFile("example.tsv");
  //   index.findMatches("freibu", 2);
  // RESULT:
  //   ([(Entity(name="frei", score=3, description="a word"), 2)], 2)
  //  Pair<Array<Pair<Entity, int>>, int> findMatches(String x, int delta) {

  //  Matches findMatches(String x, int delta) {
  Matches findMatches(String x, int delta) {
    ArrayList<String> xQGrams = computeQGrams(x);
    ArrayList<String> entityQGrams;
    int numOfMatches;
    int distance;
    Matches result = new Matches();
    // Iterate over all the entities.
    for (int i = 0; i < entities.toArray().length; i++) {
      entityQGrams = computeQGrams(entities.get(i).name);
      numOfMatches = 0;
      for (int n = 0; n < xQGrams.toArray().length; n++) {
        for (int m = 0; m < entityQGrams.toArray().length; m++) {
//          System.out.println(xQGrams + "\t" + entityQGrams);
          if (entityQGrams.get(m).equals(xQGrams.get(n))) {
            // Another match!
            numOfMatches++;
          }
        }
      }

      //      if (numOfMatches >= x.length() - (this.q * delta)) {
      if (numOfMatches >= Math.max(x.length(), entities.get(i).name.length())
          - 1 - (delta - 1) * this.q) {

//        System.out.println("\n\n\nnumOfMatches " + numOfMatches
//            + "\tx.length() - (this.q * delta) "
//            + (x.length() - (this.q * + delta))
//            + "\tx.length() " + x.length()
//            + "\tthis.q " + this.q
//            + "\tdelta" + delta
//            + "\nentities.get(i).toString()\t" + entities.get(i).toString());

        // So many matches! => Calculate the editign distance...
        distance = prefixEditDistance(x, entities.get(i).name, delta);
//        System.out.println("x " + x + "\tentity " + entities.get(i).name);
//        System.out.println(distance);

        result.matches.add(new Match(entities.get(i), distance));
        result.numPedComputations++;
      }
    }

    return result;
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
   * Compute one sided q-grams for padded, normalized version of given string.
   */
  public ArrayList<String> computeOneSidedQGrams(String word) {
    ArrayList<String> result = new ArrayList<>();
    word = padding + normalize(word);
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
