// Copyright 2017 University of Freiburg
// Claudius Korzen <korzen@cs.uni-freiburg.de>

import java.util.List;

/**
 * The main class of the inverted index.
 */
public class InvertedIndexMain {
  /**
   * The main method.
   */
  public static void main(String[] args) {
    if (args.length != 1) {
      System.out.println("Usage: java -jar InvertedIndexMain.jar <file>");
      System.exit(1);
    }

    String fileName = args[0];
    InvertedIndex ii = new InvertedIndex();
    ii.readFromFile(fileName);

    // Output the lengths of the inverted lists (= frequencies of the words).
    for (String word : ii.getWords()) {
      List<Integer> list = ii.getInvertedList(word);
      System.out.println(word + " " + (list != null ? list.size() : "0"));
    }
  }
}
