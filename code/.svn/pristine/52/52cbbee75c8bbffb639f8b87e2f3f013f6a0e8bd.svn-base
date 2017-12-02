// Copyright 2017, University of Freiburg,
// Chair of Algorithms and Data Structures.
// Authors: Hannah Bast <bast@cs.uni-freiburg.de>,

import java.io.IOException;
import java.util.Map;
import java.util.ArrayList;

/**
 * Build inverted index from given file and output the inverted lists and their
 * lengths.
 */
public class QGramIndexMain {

  /**
   * Main function.
   */
  public static void main(String[] args) throws IOException {
    // Parse commmand line arguments.
    if (args.length != 1) {
      System.out.println("Usage: java -jar QGramIndexMain <entity-file>");
      System.exit(1);
    }

    String fileName = args[0];

    System.out.print("Building index from '" +  fileName + "' ...");

    // Build 3-gram index from given file.
    QGramIndex qgi = new QGramIndex(3);

    long start = System.currentTimeMillis();
    qgi.buildFromFile(fileName);
    long end = System.currentTimeMillis();

    System.out.println(" done in " + (end - start) + "ms.");

    // Print all q-grams and the lengths of their inverted list.
    for (Map.Entry<String, ArrayList<Integer>> entry 
        : qgi.invertedLists.entrySet()) {
      System.out.println(entry.getKey() + "\t" + entry.getValue());
    }
  }
}
