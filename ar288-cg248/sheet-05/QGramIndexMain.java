// Copyright 2017, University of Freiburg,
// Chair of Algorithms and Data Structures.
// Authors: Hannah Bast <bast@cs.uni-freiburg.de>, cg248, ar288

import java.io.IOException;
import java.util.Map;
import java.util.ArrayList;
import java.io.BufferedReader;
import java.io.InputStreamReader;

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

    BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
    Matches mches;
    while (true) {
      start = System.currentTimeMillis();
      System.out.println("Enter your search query: ");
      mches = qgi.findMatches(br.readLine(), 2);
      ArrayList<Match> sorted = qgi.rankMatches(mches.matches);
      end = System.currentTimeMillis();
      System.out.println(" done in " + (end - start) + "ms.");

      for (int i = 0; i < 5; i++) {
        if (i < sorted.toArray().length) {
          System.out.println(sorted.get(i).toString());
        }
      }

      if (sorted.toArray().length > 5) {
        System.out.println("Number of results: " + sorted.toArray().length);
      }
    }
  }
}
