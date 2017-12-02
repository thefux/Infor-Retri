
// Copyright 2017, University of Freiburg,
// Chair of Algorithms and Data Structures.
// Claudius Korzen <korzen@cs.uni-freiburg.de>.

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

/**
 * A list of postings of form (docId, score).
 */
public class PostingList {
  /**
   * The value of the sentinel.
   */
  protected static final int SENTINEL = Integer.MAX_VALUE;
  
  /**
   * The docIds of the postings in this list.
   */
  protected int[] ids;

  /**
   * The scores of the postings in this list.
   */
  protected int[] scores;

  /**
   * The capacity of this list.
   */
  protected int capacity;

  /**
   * The number of postings in this list.
   */
  protected int numPostings;

  // ==========================================================================

  /**
   * Reads a posting list from the given file.
   * 
   * @param fileName
   *        The path to the file to read.
   */
  public void readFromFile(String fileName) {
    try (BufferedReader br = Files.newBufferedReader(Paths.get(fileName))) {
      // Read the number of postings in the file from the first line.
      int numPostings = Integer.parseInt(br.readLine());

      // Reserve enough space for the postings (+1 for the sentinel).
      reserve(numPostings + 1);

      // Read the file line by line, each in the format <id>WHITESPACE<score>.
      String line;
      while ((line = br.readLine()) != null) {
        // Split the line into <id> and <score>.
        String[] parts = line.split("\\s+");
        int id = Integer.parseInt(parts[0]);
        int score = Integer.parseInt(parts[1]);

        // Add the posting (id, score) to this list.
        addPosting(id, score);
      }

      // Add a sentinel to the end of the list.
      addPosting(SENTINEL, SENTINEL);
      this.numPostings--;
    } catch (IOException e) {
      System.err.println("Couldn't read the file: " + e.getMessage());
      System.exit(1);
    }
  }

  // ==========================================================================

  /**
   * Intersects the two given posting lists using the basic "zipper" algorithm.
   * 
   * @param l1
   *        The first posting list.
   * @param l2
   *        The second posting list.
   *
   * @return The intersection of the two lists.
   */
  public static PostingList intersectBaseline(PostingList l1, PostingList l2) {
    PostingList result = new PostingList();
    result.reserve(Math.min(l1.size(), l2.size()));

    int i1 = 0;
    int i2 = 0;

    while (i1 < l1.size() && i2 < l2.size()) {
      while (i1 < l1.size() && l1.getId(i1) < l2.getId(i2)) {
        i1++;
      }

      if (i1 == l1.size()) {
        break;
      }

      while (i2 < l2.size() && l2.getId(i2) < l1.getId(i1)) {
        i2++;
      }

      if (i2 == l2.size()) {
        break;
      }

      if (l1.getId(i1) == l2.getId(i2)) {
        result.addPosting(l1.getId(i1), l1.getScore(i1) + l2.getScore(i2));
        i1++;
        i2++;
      }
    }
    return result;
  }

  // ==========================================================================

  /**
   * Intersects the two given posting lists using an improved algorithm that
   * uses at least three non-trivial ideas presented in the lecture.
   *
   * @param l1
   *        The first posting list.
   * @param l2
   *        The second posting list.
   *
   * @return The intersection of the two lists.
   */
  public static PostingList intersect(PostingList l1, PostingList l2) {
    // Make sure that the first list is the smaller list.
    if (l1.size() > l2.size()) {
      return intersect(l2, l1);
    }


    // If the difference in the sizes is small, use the basic zipper algorithm.
    if ((float) l1.size() / (float) l2.size() < 0.01f) {
      return intersectSearchRemainder(l1, l2);
      // return intersectGallopSearch(l1, l2);
    }

    // Intersect the lists using the galloping search algorithm.
    return intersectBaseline(l1, l2);
  }

  /**
   * Intersects the two given posting lists using galloping search. Expects that
   * the first given list is the smaller list.
   *
   * @param l1
   *        The first posting list.
   * @param l2
   *        The second posting list.
   *
   * @return The intersection of the two lists.
   */
  static PostingList intersectGallopSearch(PostingList l1, PostingList l2) {
    // Create the result list with enough space (the max size of the result is
    // equal to the size of the smaller (= first) list).
    PostingList result = new PostingList();
    result.reserve(l1.size());

    int i1 = 0;
    int i2 = 0;

    while (i1 < l1.size() && i2 < l2.size()) {
      while (l1.getId(i1) == l2.getId(i2) && l1.getId(i1) != SENTINEL) {
        result.addPosting(l1.getId(i1), l1.getScore(i1) + l2.getScore(i2));
        i1++;
        i2++;
      } 
      
      while (l1.getId(i1) > l2.getId(i2)) {
        // Find an upper bound (the first index of the element in l2 that is
        // larger than the current element in l1) using exponential search.
        int upper = expSearch(l1.getId(i1), l2, i2);

        // Find the current id of l1 in the interval l2[lower:upper].
        i2 = binSearch(l1.getId(i1), l2, i2 + (upper >> 1), i2 + upper);
      }

      while (l1.getId(i1) < l2.getId(i2)) {
        i1++;
      }
    }

    return result;
  }

  /**
   * Does an exponential search on the given posting list in order to find the
   * first index of the element that is larger than the given value, with
   * exponential steps, starting at the given index.
   * 
   * @param value
   *        The value to find in the given posting list.
   * @param list
   *        The posting list to process.
   * @param start
   *        The starting index.
   *
   * @return The first index of the element in the list that is larger than the
   *         given value.
   */
  static int expSearch(int value, PostingList list, int start) {
    if (start >= list.size()) {
      return 0;
    }
    
    if (list.getId(start) >= value) {
      return 0;
    }
    
    int i = 1;
    while (start + i < list.size() && list.getId(start + i) < value) {
      i <<= 1;
    }
    return i;
  }

  /**
   * Does a binary search in order to find the index of the given value in the
   * interval list[start:end]. Returns the given end index if the list doesn't
   * contain the given value in the given interval.
   * 
   * @param value
   *        The value to find in the given posting list.
   * @param list
   *        The posting list to process.
   * @param start
   *        The start index of the interval to search.
   * @param end
   *        The end index of the interval to search.
   *
   * @return The index of the value in the given list or the end index of the
   *         interval if the list doesn't contain the value in the given
   *         interval.
   */
  static int binSearch(int value, PostingList list, int start, int end) {
    // Make sure that 'start' and 'end' doesn't exceed the list boundaries.
    start = Math.max(0, start);
    end = Math.min(list.size(), end);

    while (start < end) {
      int half = start + ((end - start) / 2);

      if (list.getId(half) < value) {
        // Proceed in the right half.
        start = half + 1;
        continue;
      }

      if (list.getId(half) > value) {
        // Proceed in the left half.
        end = half;
        continue;
      }
      return half;
    }
    // Return 'end' if the list doesn't contain the value in the interval.
    return end;
  }

  // ==========================================================================

  /**
   * Intersects the two given posting lists using binary search in the remainder
   * of the longer list. Expects that the first given list is the smaller list.
   * Please note: This method is *NOT* used, but was implemented for
   * experimental reasons and is retained for reference.
   * 
   * @param l1
   *        The first posting list.
   * @param l2
   *        The second posting list.
   *
   * @return The intersection of the two lists.
   */
  static PostingList intersectSearchRemainder(PostingList l1, PostingList l2) {
    // Create the result list with enough space (the max size of the result is
    // equal to the size of the smaller (= first) list).
    PostingList result = new PostingList();
    result.reserve(l1.size());

    int i1 = 0;
    int i2 = 0;

    while (i1 < l1.size() && i2 < l2.size()) {
      while (l1.getId(i1) == l2.getId(i2) && l1.getId(i1) != SENTINEL) {
        result.addPosting(l1.getId(i1), l1.getScore(i1) + l2.getScore(i2));
        i1++;
        i2++;
      } 
      
      while (l1.getId(i1) > l2.getId(i2)) {
        // Find the current id of l1 in the interval l2[i2:].
        i2 = binSearch(l1.getId(i1), l2, i2, l2.size());
      }

      while (l1.getId(i1) < l2.getId(i2)) {
        i1++;
      }
    }

    return result;
  }

  // ==========================================================================

  /**
   * Reserves space for n postings in this list.
   * 
   * @param n
   *        The number of postings.
   */
  public void reserve(int n) {
    this.ids = new int[n];
    this.scores = new int[n];
    this.capacity = n;
    this.numPostings = 0;
  }

  /**
   * Adds the given posting to this list.
   * 
   * @param id
   *        The id of the posting.
   * @param score
   *        The score of the posting.
   */
  public void addPosting(int id, int score) {
    this.ids[this.numPostings] = id;
    this.scores[this.numPostings] = score;
    this.numPostings++;
  }

  /**
   * Returns the id of the i-th posting.
   * 
   * @param i
   *        The index of the posting.
   * 
   * @return The id of the i-th posting.
   */
  public int getId(int i) {
    return this.ids[i];
  }

  /**
   * Returns the score of the i-th posting.
   * 
   * @param i
   *        The index of the posting.
   * 
   * @return The score of the i-th posting.
   */
  public int getScore(int i) {
    return this.scores[i];
  }

  /**
   * Returns the number of postings in this list.
   * 
   * @return The number of postings in this list.
   */
  public int size() {
    return this.numPostings;
  }

  // ==========================================================================

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("[");
    for (int i = 0; i < size(); i++) {
      sb.append("(" + getId(i) + ", " + getScore(i) + ")");
      if (i < size() - 1) {
        sb.append(", ");
      }
    }
    sb.append("]");
    return sb.toString();
  }
}
