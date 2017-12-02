// Copyright 2017, University of Freiburg,
// Chair of Algorithms and Data Structures.
// Claudius Korzen <korzen@cs.uni-freiburg.de>.

import org.junit.Assert;
import org.junit.Test;

/**
 * One unit test for each non-trivial method in the Intersect class.
 */
public class PostingListTest {
  /**
   * Tests for the method readFromFile().
   */
  @Test
  public void testReadFromFile() {
    PostingList l1 = new PostingList();
    PostingList l2 = new PostingList();
    PostingList l3 = new PostingList();
    l1.readFromFile("example1.txt");
    l2.readFromFile("example2.txt");
    l3.readFromFile("example3.txt");
    Assert.assertEquals("[(2, 5), (3, 1), (6, 2)]", l1.toString());
    Assert.assertEquals("[(1, 1), (2, 4), (4, 3), (6, 3)]", l2.toString());
    Assert.assertEquals("[(5, 1), (7, 2)]", l3.toString());
  }

  /**
   * Tests for the method intersectBaseline().
   */
  @Test
  public void testIntersectBaseline() {
    PostingList l1 = new PostingList();
    PostingList l2 = new PostingList();
    PostingList l3 = new PostingList();
    l1.readFromFile("example1.txt");
    l2.readFromFile("example2.txt");
    l3.readFromFile("example3.txt");
    PostingList result1 = PostingList.intersectBaseline(l1, l2);
    PostingList result2 = PostingList.intersectBaseline(l1, l3);
    Assert.assertEquals("[(2, 9), (6, 5)]", result1.toString());
    Assert.assertEquals("[]", result2.toString());
  }
  
  /**
   * Tests for the method intersect().
   */
  @Test
  public void testIntersect() {
    PostingList l1 = new PostingList();
    PostingList l2 = new PostingList();
    PostingList l3 = new PostingList();
    l1.readFromFile("example1.txt");
    l2.readFromFile("example2.txt");
    l3.readFromFile("example3.txt");
    PostingList result1 = PostingList.intersectBaseline(l1, l2);
    PostingList result2 = PostingList.intersectBaseline(l1, l3);
    Assert.assertEquals("[(2, 9), (6, 5)]", result1.toString());
    Assert.assertEquals("[]", result2.toString());
  }
  
  /**
   * Tests for the method intersectGallopSearch().
   */
  @Test
  public void testIntersectGallopSearch() {
    PostingList l1 = new PostingList();
    PostingList l2 = new PostingList();
    PostingList l3 = new PostingList();
    l1.readFromFile("example1.txt");
    l2.readFromFile("example2.txt");
    l3.readFromFile("example3.txt");
    PostingList result1 = PostingList.intersectBaseline(l1, l2);
    PostingList result2 = PostingList.intersectBaseline(l1, l3);
    Assert.assertEquals("[(2, 9), (6, 5)]", result1.toString());
    Assert.assertEquals("[]", result2.toString());
  }

  /**
   * Tests for the method intersectGallopSearch().
   */
  @Test
  public void testIntersectSearchRemainder() {
    PostingList l1 = new PostingList();
    PostingList l2 = new PostingList();
    PostingList l3 = new PostingList();
    l1.readFromFile("example1.txt");
    l2.readFromFile("example2.txt");
    l3.readFromFile("example3.txt");
    PostingList result1 = PostingList.intersectBaseline(l1, l2);
    PostingList result2 = PostingList.intersectBaseline(l1, l3);
    Assert.assertEquals("[(2, 9), (6, 5)]", result1.toString());
    Assert.assertEquals("[]", result2.toString());
  }

  /**
   * Tests for the method expSearch().
   */
  @Test
  public void testExpSearch() {
    PostingList l2 = new PostingList();
    l2.readFromFile("example2.txt");
    Assert.assertEquals(0, PostingList.expSearch(1, l2, 0));
    Assert.assertEquals(0, PostingList.expSearch(1, l2, 1));
    Assert.assertEquals(0, PostingList.expSearch(1, l2, 2));
    Assert.assertEquals(1, PostingList.expSearch(2, l2, 0));
    Assert.assertEquals(0, PostingList.expSearch(2, l2, 1));
    Assert.assertEquals(0, PostingList.expSearch(2, l2, 2));
    Assert.assertEquals(2, PostingList.expSearch(4, l2, 0));
    Assert.assertEquals(1, PostingList.expSearch(4, l2, 1));
    Assert.assertEquals(0, PostingList.expSearch(4, l2, 2));
    Assert.assertEquals(4, PostingList.expSearch(6, l2, 0));
    Assert.assertEquals(2, PostingList.expSearch(6, l2, 1));
    Assert.assertEquals(1, PostingList.expSearch(6, l2, 2));
    Assert.assertEquals(0, PostingList.expSearch(6, l2, 3));
    Assert.assertEquals(4, PostingList.expSearch(10, l2, 0));
  }
  
  /**
   * Tests for the method binSearch().
   */
  @Test
  public void testBinSearch() {
    PostingList l2 = new PostingList();
    l2.readFromFile("example2.txt");
    Assert.assertEquals(0, PostingList.binSearch(1, l2, 0, l2.size()));
    Assert.assertEquals(1, PostingList.binSearch(2, l2, 0, l2.size()));
    Assert.assertEquals(2, PostingList.binSearch(4, l2, 0, l2.size()));
    Assert.assertEquals(3, PostingList.binSearch(6, l2, 0, l2.size()));
    Assert.assertEquals(0, PostingList.binSearch(1, l2, 0, 0));
    Assert.assertEquals(0, PostingList.binSearch(2, l2, 0, 0));
    Assert.assertEquals(2, PostingList.binSearch(4, l2, 0, 2));
    Assert.assertEquals(2, PostingList.binSearch(6, l2, 0, 2));
  }
}
