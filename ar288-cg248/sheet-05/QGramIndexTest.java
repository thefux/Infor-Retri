// Copyright 2017, University of Freiburg,
// Chair of Algorithms and Data Structures.
// Authors: Hannah Bast <bast@cs.uni-freiburg.de>,

import org.junit.Test;
import org.junit.Assert;
import java.io.IOException;
import java.util.Arrays;

/**
 * One unit test for each non-trivial method in the QGramIndex class.
 */
public class QGramIndexTest {

  @Test
  public void testConstructor() {
    QGramIndex qgi = new QGramIndex(3);
    Assert.assertEquals(3, qgi.q);
    Assert.assertEquals("$$", qgi.padding);
    Assert.assertEquals("{}", qgi.invertedLists.toString());
  }

  @Test
  public void testBuildFromFile() throws IOException {
    QGramIndex qgi = new QGramIndex(3);
    qgi.buildFromFile("example.tsv");
    Assert.assertEquals("{$$b=[2], $$f=[1], $br=[2], $fr=[1], bre=[2],"
                    + " ei$=[1, 2], fre=[1], i$$=[1, 2], rei=[1, 2]}",
            qgi.invertedLists.toString());
  }

  @Test
  public void testComputeQGrams() {
    QGramIndex qgi = new QGramIndex(3);
    Assert.assertEquals("[$$f, $fr, fre, rei, eib, ibu, bur, urg, rg$, g$$]",
                        qgi.computeQGrams("freiburg").toString());
  }

  @Test
  public void testNormalize() {
    QGramIndex qgi = new QGramIndex(3);
    Assert.assertEquals("freiburg", QGramIndex.normalize("Frei, burg !!"));
    Assert.assertEquals("freiburg", QGramIndex.normalize("freiburg"));
  }

  @Test
  public void testBuildFromFile2() throws IOException {
    QGramIndex qgi = new QGramIndex(3);
    qgi.buildFromFile("example.tsv");
    Assert.assertEquals("[Entity(name=\"frei\", score=3, description=\"a "
        + "word\"), Entity(name=\"brei\", score=2, description=\"another "
        + "word\")]", qgi.entities.toString());
  }

  @Test
  public void testMergeLists() throws IOException {
    QGramIndex qgi = new QGramIndex(3);
    int[][] lists = {{1, 1, 3, 5}, {2, 3, 3, 9, 9}};
    Assert.assertEquals("[1, 1, 2, 3, 3, 3, 5, 9, 9]", Arrays.toString(qgi
        .mergeLists(lists)));
  }

  // TEST CASE:
  //   prefixEditDistance("frei", "frei", 0);
  // RESULT:
  //   0
  @Test
  public void testPrefixEditDistance0() throws IOException {
    QGramIndex qgi = new QGramIndex(3);
    String x = "frei";
    String y = "frei";

    Assert.assertEquals(qgi.prefixEditDistance(x, y, 0), 0);
  }

  //
  // TEST CASE:
  //   prefixEditDistance("frei", "freiburg", 0);
  // RESULT:
  //   0
  @Test
  public void testPrefixEditDistance1() throws IOException {
    QGramIndex qgi = new QGramIndex(3);
    String x = "frei";
    String y = "freiburg";

    Assert.assertEquals(qgi.prefixEditDistance(x, y, 0), 0);
  }


  //
  // TEST CASE:
  //   prefixEditDistance("frei", "breifurg", 1);
  // RESULT:
  //   1
  @Test
  public void testPrefixEditDistance2() throws IOException {
    QGramIndex qgi = new QGramIndex(3);
    String x = "frei";
    String y = "breifurg";

    Assert.assertEquals(qgi.prefixEditDistance(x, y, 1), 1);
  }


  //
  // TEST CASE:
  //   prefixEditDistance("freiburg", "stuttgart", 2);
  // RESULT:
  //   3
  @Test
  public void testPrefixEditDistance3() throws IOException {
    QGramIndex qgi = new QGramIndex(3);
    String x = "freiburg";
    String y = "stuttgart";

    Assert.assertEquals(qgi.prefixEditDistance(x, y, 2), 3);
  }
}
