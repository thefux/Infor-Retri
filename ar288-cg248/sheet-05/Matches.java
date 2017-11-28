// Copyright 2017, University of Freiburg,
// Chair of Algorithms and Data Structures.
// Author: cg248, ar288

import java.nio.file.Files;
import java.nio.file.Paths;
import java.io.BufferedReader;
import java.io.IOException;
import java.util.TreeMap;
import java.util.ArrayList;

/**
 * Entities to be searched.
 */
public class Matches {

  /**
   * Create an empty Entity.
   */
  public Matches() {
    this.matches = new ArrayList<Match>();
    this.numPedComputations = 0;
  }

  /**
   * return the contents of this class
   * @return String: contents of this class
   */
  public String toString() {
    return ("(" + matches.toString() + ", " + numPedComputations + ")");
  }

  public ArrayList<Match> matches;
  public int numPedComputations;
}
