// Copyright 2017 University of Freiburg
// Claudius Korzen <korzen@cs.uni-freiburg.de>

import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.junit.Assert;
import org.junit.Test;

/**
 * One unit test for each non-trivial method in the InvertedIndex class.
 */
public class InvertedIndexTest {
  /**
   * Tests for the method readFromFile().
   */
  @Test
  public void testReadFromFile() {
    InvertedIndex ii = new InvertedIndex();
    ii.readFromFile("example.txt");

    // Convert the inverted index to a TreeMap in order to get a deterministic
    // order of the inverted lists. But still use HashMap for the inverted
    // index, because it is more efficient than TreeMap (efficiency does not
    // matter in unit tests).
    Map<String, List<Integer>> sortedLists = new TreeMap<>(ii.invertedLists);

    Assert.assertEquals("{doc=[0, 1, 2], first=[0], second=[1], third=[2]}",
        sortedLists.toString());
  }
}
