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
   * @param args The command line arguments.
   */
  public static void main(String[] args) {
    if (args.length < 2) {
      System.out.println("Usage: java -jar IntersectMain.jar <posting lists>");
      System.exit(1);
      return;
    }

    int numLists = args.length;
    long totalTimeBaseLine = 0;
    long totalIntersect = 0;
    int totalRuns = 0;

    // Read the posting lists.
    PostingList[] lists = new PostingList[numLists];
    for (int i = 0; i < numLists; i++) {
      System.out.print("Reading list '" + args[i] + "' ... ");
      System.out.flush();
      PostingList list = new PostingList();
      list.readFromFile(args[i]);
      lists[i] = list;
      System.out.println("Done. Size: " +  lists[i].size() + ".");
    }


    // Intersect the lists pairwise.
    System.out.println();
    for (int i = 0; i < numLists; i++) {
      for (int j = 0; j < i; j++) {
        for (int rounds = 0; rounds < 5; rounds++) {
          System.out.println("Intersect '" + args[i] + "' & '" + args[j] + "'"
                  + ".");

          // Intersect lists[i] and lists[j] using the baseline.
          long time1 = System.nanoTime();
          PostingList list = PostingList.intersectBaseline(lists[i], lists[j]);
          long time2 = System.nanoTime();
          long timeBaseLine = (time2 - time1) / 1000;
          System.out.print("  Time needed: " + timeBaseLine + "us. ");
          System.out.println("Result size: " + list.size());
          totalTimeBaseLine += timeBaseLine;

          // Intersect lists[i] and lists[j] using the intersect.
          long time3 = System.nanoTime();
          PostingList list2 = PostingList.intersect(lists[i], lists[j]);
          long time4 = System.nanoTime();
          long timeIntersect = (time4 - time3) / 1000;
          System.out.print("  Time needed: " + timeIntersect + "us. ");
          System.out.println("Result size: " + list2.size());
          totalIntersect += timeIntersect;
          totalRuns++;
        }
        System.out.println("\n Average baseline time: " + (totalTimeBaseLine
                / totalRuns) + "us" + ".");
        System.out.println("\n Average intersect time: " + (totalIntersect
                / totalRuns) + "us" + ".");
      }
    }
    System.out.println("\n Overall average baseline time: " + (totalTimeBaseLine
            / totalRuns) + "us" + ".");
    System.out.println("\n Overall average intersect time: " + (totalIntersect
            / totalRuns) + "us" + ".");
  }
}
