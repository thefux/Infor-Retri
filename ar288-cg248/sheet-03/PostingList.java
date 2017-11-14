// Copyright 2017, University of Freiburg,
// Chair of Algorithms and Data Structures.
// Author: Claudius Korzen <korzen@cs.uni-freiburg.de>.

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.charset.Charset;


/**
 * A list of postings of form (docId, score).
 */
public class PostingList {
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

  /**
   * The lower bound in this list.
   */
  double lb = 0;

  // ==========================================================================

  /**
   * Reads a posting list from the given file.
   *
   * @param fileName
   *        The path to the file to read.
   */
  public void readFromFile(String fileName) {
    Charset charset = Charset.forName("UTF-8");
    try (BufferedReader br = Files.newBufferedReader(Paths.get(fileName),
            charset)) {
      // Read the number of postings in the file from the first line.
      int numPostings = Integer.parseInt(br.readLine());

      // Reserve enough space for the postings.
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
      addPosting(Integer.MAX_VALUE, 0);
      this.numPostings = this.numPostings - 1;
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

  // Implement a new method for intersecting two posting lists that
  // uses at least three non-trivial ideas presented in the
  // lecture. The goal is to beat the baseline implementation for
  // all scenarios of the exercise sheet. Note that you can also
  // implement several algorithms and switch between them depending
  // on the sizes of the input lists (or depending on any
  // information that you find to be useful). Note: Each implemented
  // method must pass the test case provided for the baseline
  // implementation.

  /**
   * Intersects the two given posting lists using an improved algorithm that
   * choses from four non-trivial ideas presented in the lecture.
   * 1. Sentinels.
   * 2. BinarySearch (with sentinels and recursive).
   * 3. BinarySearch with shifting lower bound.
   * 4. Galloping binary search.
   *
   * @param l1
   *        The first posting list.
   * @param l2
   *        The second posting list.
   *
   * @return The intersection of the two lists.
   */
  public static PostingList intersect(PostingList l1, PostingList l2) {

    double n = 0;
    double k = 0;

    int sw = 3;

    if (l1.size() < l2.size()) {
      k = l1.size();
      n = l2.size();

    } else {
      k = l2.size();
      n = l1.size();
    }

    // time of 0
    double zipWorst = n + k;

    // time of 1 and 2
    // double binBest = k * (Math.log(n) / Math.log(2));
    double binWorst = k * (Math.log(n - k) / Math.log(2));

    // time of 1 and 2
    double gallopingBin = k * (Math.log(n) / Math.log(2) - k);

    double best = Math.min(zipWorst, binWorst);
    best = Math.min(best, gallopingBin);

    // Highest complexity (not assymptotic complexity).
    if (gallopingBin == best) {
      sw = 3;
    }

    // Medium complexity (not assymptotic complexity).
    if (binWorst == best) {
      // 1 or 2, whichever is faster...
      sw = 2;
    }

    // Smallest complexity (not assymptotic complexity).
    if (zipWorst == best) {
      sw = 0;
    }

    switch (sw) {
      // Recursive binary search.
      // Makes use of a shrinking list to be searched
      // => better than O(k * log_2(n)) and worse than O(k * log_2(n - k)):
      case 1 :
        int i1 = 0;
        int lb = 0;
        int ub = l2.size();
        int mb = (ub + lb) / 2;

        PostingList result = new PostingList();
        result.reserve(Math.min(l1.size(), l2.size()));
        return intersectBinarySearchRecursive(l1, l2, i1, lb, ub, mb, result,
                0, 0);

      // Binary search with loops instead of recursion.
      // Makes use of a shrinking list to be searched
      // => better than O(k * log_2(n)) and worse than O(k * log_2(n - k)):
      case 2 :
        return intersectBinarySearchUsingSentinels(l1, l2);

      // Galloping binary search.
      // The run time is: O(k * (2*log_2(n) - 1)) = O(k * log_2(n) - k)
      // => Assymptotically always better than galloping linear search.
      // => Assymptotically also always better than the linear search.
      case 3:
        return intersectGallopingBinarySearch(l1, l2);

      default :
        break;
    }

    // Worst case: O(n + k)
    return intersectZipper(l1, l2);
  }

  /**
   * <p>
   * Intersect the binary two posting lists using a binary search. Sentinels
   * are also used, but it was not possible to completely avoid if
   * statements because control variables were needed to avoid getting stuck
   * in loops. The lists are swapped if l1 is longer than l2...</p>
   *
   * <p>
   * Worst case scenario:
   * l1 = [1, 2, 3, 4, 5, 6, 7]       k elements
   * l2 = [1, 2, 3, 4, 5, 6, 7, 8]    m = log_2(n) elements </p>
   *
   * <p>
   * => Number of steps: log_2(n - 0) + log_2(n - 1) + ... + log_2(n - k)
   * => Worse than k * log_2(n - k)
   *</p>
   *
   * @param l1
   * list 1.
   *
   * @param l2
   * list 2.
   *
   * @return
   * PostingList intersection of the two PostingLists l1 and l2.
   */
  public static PostingList intersectBinarySearchUsingSentinels(
          PostingList l1,
          PostingList
                  l2)  {
    // Swap to make sure l1 is smaller.
    if (l1.size() > l2.size()) {
      return intersectBinarySearchUsingSentinels(l2, l1);
    }

    int i1 = 0;
    int lb = 0;
    int ub = l2.size();
    int mb;
    int old;
    int switches = 0;

    PostingList result = new PostingList();
    result.reserve(l1.size());

    while (l1.getId(i1) < Integer.MAX_VALUE) {
      switches = 0;
      mb = (ub + lb) / 2;
      System.out.println("    lb: " + lb + "    mb " + mb + "    ub " + ub);
      while (l2.getId(mb) - l1.getId(i1) != 0 && switches < 2) {

        while (l1.getId(i1) < l2.getId(mb) && switches < 2) {
          old = mb;
          mb = (lb + mb) / 2;

          if (old - mb < 2) {
            switches++;
          }
        }

        while (l1.getId(i1) > l2.getId(mb) && switches < 2) {
          old = mb;
          mb = (ub + mb) / 2;

          if (mb - old < 2) {
            switches++;
          }
        }
      }

      System.out.println("l1: " + l1.getId(i1));
      System.out.println("l2: " + l2.getId(mb));

      if (l1.getId(i1) == l2.getId(mb)) {
        result.addPosting(l1.getId(i1), l1.getScore(i1) + l2.getScore(mb));

        // Only search the remainder of the list in the next
        // iteration.
        lb = mb;
      }

      i1++;
    }
    return result;
  }


  /**
   * An intersect method using binary a recursive binary search. Notice that
   * lower bound increases to ensure that only the remaining part of the list
   * is searched in future iterations. l1 and l2 are swapped if l1 is longer
   * than l2.
   *
   * @param l1
   * The first (smaller) list.
   * @param l2
   * The second (larger) list.
   * @param i1
   * The current position in the shorter list.
   * @param lb
   * The (slowly increasing) lower bound of the range that still has to be
  searched.
   * @param ub
   * The (not changing) upper bound of the range that still has to be searched.
   * @param mb
   * The current position of the binary search.
   * @param result
   * The PostingList that is to be modified.
   * @param switches
   * A control variable that ensures that the recursion aborts and it doesn't
  toggle arround a non existant element the list that is to be searched.
  This variable also ensures that the last variable (Integer.MAX_VALUE)
  does not get added to the intersection.
   * @param state
   * A control variable to decide if it should operate as a recursive binary
  search in O(k * log(n)) or if it should operate as a part of my
  galloping binary search...
   * @return
   * PostingList intersection of l1 and l2.
   */
  public static PostingList intersectBinarySearchRecursive(PostingList l1,
                                                           PostingList l2,
                                                           double i1, double lb,
                                                           double ub, double mb,
                                                           PostingList result,
                                                           double switches,
                                                           double state) {
    double old;

    System.out.println("\n Searching for: " + Math.round(l1.getId((int) i1)));

    // Swap to make sure l1 is smaller.
    if (l1.size() > l2.size()) {
      return intersectBinarySearchRecursive(l2, l1, 0, 0, l1.size(), (0
              + l1.size()) / 2, result, 0, state);
    }

    if (l1.getId((int) Math.round(i1)) < l2.getId((int) Math.round(mb))) {
      old = mb;
      mb = (lb + mb) / 2.0;

//      if (mb != switches) {
//        return intersectBinarySearchRecursive(l1, l2, i1, lb, ub, mb,
//                result, old, state);
//      }

      System.out.println("old: " + old + "  mb: " + mb + "  switches: " +
              switches);

      if (switches < 2) {
        // If we are stuck in a recursion due to odd numbers.
        if ((ub + mb) / 2 == old || (lb + mb) / 2 == old) {
          System.out.println("Ping1");
          // Make it an even number.
          //mb = mb + 1;
//        if (old - mb < 2) {
//        if ((old - mb < 2) || (l1.getId(i1) > l2.getId((ub + mb) / 2))) {
          //if (mb - lb < 2) {
          return intersectBinarySearchRecursive(l1, l2, i1, lb, ub, mb,
                  result, ++switches, state);
        } else {
          System.out.println("Ping2");
          return intersectBinarySearchRecursive(l1, l2, i1, lb, ub, mb,
                  result, switches, state);
        }
      }
    }

    if (l1.getId((int) Math.round(i1)) > l2.getId((int) Math.round(mb))) {
      old = mb;
      mb = ((ub + mb) / 2);


//      if ((((double) ub + (double) mb) / 2.0 - (double) ((ub + mb) / 2)) >=
//              0.5) {
//        mb = ((ub + mb) / 2) + 1;
//      } else {
//        mb = ((ub + mb) / 2);
//      }
//
//      if (mb != switches) {
//        return intersectBinarySearchRecursive(l1, l2, i1, lb, ub, mb,
//                result, old, state);
//      }

      System.out.println("old: " + old + "  mb: " + mb + "  switches: " +
              switches);

      if (switches < 2) {
        // If we are stuck in a recursion due to odd numbers.
        if ((ub + mb) / 2 == old || (lb + mb) / 2 == old) {
          System.out.println("Ping3");
          // Make it an even number:
          //mb = mb - 1;
//        if (mb - old < 2) {
//          if ((ub - lb)(ub - mb) < 2) {
          return intersectBinarySearchRecursive(l1, l2, i1, lb, ub, mb,
                  result, ++switches, state);
        } else {
          System.out.println("Ping4");
          return intersectBinarySearchRecursive(l1, l2, i1, lb, ub, mb,
                  result, switches, state);
        }
      }
    }
//    }

    if (l1.getId((int) Math.round(i1)) == l2.getId((int) Math.round(mb))) {
      result.addPosting(l1.getId((int) Math.round(i1)), l1.getScore((int)
              Math.round(i1)) + l2.getScore((int) Math.round(mb)));
      // Only search through the remaining list in future:
      result.lb = mb;
    }

    if (l1.getId((int) Math.round(i1)) < Integer.MAX_VALUE && state == 0) {
      i1++;
      return intersectBinarySearchRecursive(l1, l2, i1, lb, ub, mb, result,
              0, state);
    }


    return result;
  }




  /**
   * Intersects the two given posting lists using an improved algorithm that
   * uses sentinels to compute the result faster in multi core processors.
   *
   * @param l1
   * @param l2
   * @return
   */
  public static PostingList intersectZipper(PostingList l1, PostingList l2) {
    PostingList result = new PostingList();

    result.reserve(Math.min(l1.size(), l2.size()));

    int i1 = 0;
    int i2 = 0;
    while (true) {
      while (l1.getId(i1) < l2.getId(i2)) {
        i1++;
      }

      while (l2.getId(i2) < l1.getId(i1)) {
        i2++;
      }

      if (l1.getId(i1) == l2.getId(i2)) {
        if (l1.getId(i1) == Integer.MAX_VALUE) {
          break;
        }

        if (l1.getId(i1) == l2.getId(i2)) {
          result.addPosting(l1.getId(i1), l1.getScore(i1) + l2.getScore(i2));
          i1++;
          i2++;
        }
      }
    }
    return result;
  }

  /**
   * <p>
   * Intersect with a galloping + binary search.
   * Looking for 5 in: l2 = [1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14]
   *</p>
   *
   * <p>
   * Galloping:
   * step   state
   * 0.     [1] < [5]
   * 1.     [2] < [5]
   * 2.     [4] < [5]
   * 3.     [8] > [5]
   *</p>
   *
   * <p>
   *        Binary:       Linear:
   * step   state         state
   * 4.     [6] > [5]     [7] > [5]
   * 5.     [5] = [5]     [6] > [5]
   * 6.     done          [5] = [5]
   * 7.     done          done
   *</p>
   *
   *<p>
   * => Binary search between 4 and 8 rather than linear search between 4 and 8.
   * The linear search would have taken 5 steps => Worst case scenario: O(n)
   * The binary search takes 5 steps => Worst case scenario: less than O(n)
   *</p>
   *
   * <p>
   * In the worst case scenario:
   *</p>
   *
   * <p>
   * l2 has n = 2^m elements and the element to be found is positioned at place
   * n_i = 2^(m -1) + 2^(0).
   *</p>
   *
   * <p>
   * First the galloping search would have to do log_2(2^m) = m steps. Then
   * the binary search would have to do log_2(2^(m-1)) = m - 1 steps in order
   * to find n_i.
   * </p>
   *
   *<p>
   * This means a maximum of 2m - 1 steps must be taken, where m = log_2(n).
   *</p>
   *
   * <p>
   * => The galloping binary search is in O(2*log_2(n) - 1) = O(log_2(n)).
   *</p>
   *<p>
   * When repeated k times, the run time is O(k * (2*log_2(n) - 1)) = O(k *
   * log_2(n) - k)
   *</p>
   * @param l1
   * PostingList 1.
   * @param l2
   * PostingList 2.
   * @return
   * PostingList intersection of 1 and 2.
   */
  public static PostingList intersectGallopingBinarySearch(PostingList l1,
                                                           PostingList l2) {
    // Swap to make sure l1 is smaller.
    if (l1.size() > l2.size()) {
      return intersectGallopingBinarySearch(l2, l1);
    }

    PostingList result = new PostingList();
    result.reserve(Math.min(l1.size(), l2.size()));

    int i1 = 0;
    int i2 = l2.size();
    int lb = 0;
    int mb = 0;
    int ub = 0;
    int jump = 1;

    while (l1.getId(i1) < Integer.MAX_VALUE) {
      jump = 1;

      i2 = lb;
      while (l1.getId(i1) > l2.getId(i2)) {
        lb = i2;
        i2 = i2 + jump;

        if (i2 > l2.size()) {
          i2 = l2.size();
          break;
        }

        if (l1.getId(i1) == l2.getId(i2)) {
          i2 = i2 + jump;
          break;
        }

        jump = 2 * jump;
      }

      ub = i2;
      mb = (lb + ub) / 2;

      result = intersectBinarySearchRecursive(l1, l2, i1, lb, ub, mb, result,
              0, 1);
      i1++;
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
