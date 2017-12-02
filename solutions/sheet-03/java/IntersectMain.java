// Copyright 2017, University of Freiburg,
// Chair of Algorithms and Data Structures.
// Author: Claudius Korzen <korzen@cs.uni-freiburg.de>.

/**
 * The main class to evaluate the efficiency of various algorithms for
 * intersecting two posting lists.
 */
public class IntersectMain {
  /**
   * The main method.
   * 
   * @param args
   *        The command line arguments.
   */
  public static void main(String[] args) {
    if (args.length < 2) {
      System.out.println("Usage: java -jar IntersectMain.jar <posting lists>");
      System.exit(1);
      return;
    }

    int numLists = args.length;

    // Read the posting lists.
    PostingList[] lists = new PostingList[numLists];
    for (int i = 0; i < numLists; i++) {
      System.out.print("Reading list '" + args[i] + "' ... ");
      System.out.flush();
      PostingList list = new PostingList();
      list.readFromFile(args[i]);
      lists[i] = list;
      System.out.println("Done. Size: " + lists[i].size() + ".");
    }

    // Do the efficiency measurements. Repeat each measurement 5 times.
    int numRuns = 5;

    // Intersect the lists pairwise.
    for (int i = 0; i < numLists; i++) {
      for (int j = 0; j < i; j++) {
        System.out.println();
        System.out.println("Intersect '" + args[i] + "' & '" + args[j] + "'.");

        // Intersect lists[i] and lists[j] using the baseline.
        long totalTime = 0;
        System.out.println("Using the baseline:");
        for (int run = 0; run < numRuns; run++) {
          long time1 = System.nanoTime();
          PostingList list = PostingList.intersectBaseline(lists[i], lists[j]);
          long time2 = System.nanoTime();
          long time = (time2 - time1) / 1000;
          System.out.print("  Run #" + run + ": ");
          System.out.print("Time needed: " + time + "μs. ");
          System.out.println("Result size: " + list.size());
          totalTime += time;
        }
        System.out.println("  Average time: " + (totalTime / numRuns) + "μs.");

        // Intersect lists[i] and lists[j] using an improved algorithm.
        totalTime = 0;
        System.out.println("Using the improved algorithm:");
        for (int run = 0; run < numRuns; run++) {
          long time1 = System.nanoTime();
          PostingList list = PostingList.intersect(lists[i], lists[j]);
          long time2 = System.nanoTime();
          long time = (time2 - time1) / 1000;
          System.out.print("  Run #" + run + ": ");
          System.out.print("Time needed: " + time + "μs. ");
          System.out.println("Result size: " + list.size());
          totalTime += time;
        }
        System.out.println("  Average time: " + (totalTime / numRuns) + "μs.");
      }
    }
    
    // Intersect all lists.
    System.out.println();
    System.out.println("Intersecting all lists.");
    long time1 = System.currentTimeMillis();
    PostingList list = lists[0];
    for (int i = 0; i < numLists; i++) {
      list = PostingList.intersect(list, lists[i]);
    }
    long time2 = System.currentTimeMillis();
    long time = time2 - time1;
    System.out.println("time needed: " + time + "ms. ");
    System.out.println("result list: " + list);
  }
}
