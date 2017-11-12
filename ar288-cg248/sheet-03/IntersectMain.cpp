// Copyright 2017, University of Freiburg,
// Chair of Algorithms and Data Structures.
// Authors: Patrick Brosi <brosi@cs.uni-freiburg.de>,
//          Claudius Korzen <korzen@cs.uni-freiburg.de>.

#include <stddef.h>
#include <chrono>
#include <iostream>
#include <vector>
#include "./PostingList.h"

// _____________________________________________________________________________
int main(int argc, char **argv) {
  if (argc < 3) {
    std::cerr << "Usage " << argv[0] << " <posting lists>\n";
    exit(1);
  }

  size_t numLists = argc - 1;
  int totalTime = 0;
  size_t totalRuns = 0;

  // Read the posting lists.
  std::vector<PostingList> lists;
  for (size_t i = 0; i < numLists; i++) {
    std::cout << "Reading list '" << argv[i + 1] << "' ... ";
    PostingList list;
    list.readFromFile(argv[i + 1]);
    lists.push_back(list);
    std::cout << "Done. Size: " << lists[i].size() << "." << std::endl;
  }

  // Intersect the lists pairwise.
  std::cout << std::endl;
  for (size_t i = 0; i < numLists; i++) {
    for (size_t j = 0; j < i; j++) {
      std::cout << "Intersect '" << argv[i + 1] << "' & '" << argv[j + 1]
            << "'." << std::endl;

      // Intersect lists[i] and lists[j] using the baseline.
      auto time1 = std::chrono::high_resolution_clock::now();
      PostingList list = PostingList::intersectBaseline(lists[i], lists[j]);
      auto time2 = std::chrono::high_resolution_clock::now();
      size_t time = std::chrono::duration_cast<std::chrono::microseconds>
                      (time2 - time1).count();
      std::cout << "  Time needed: " << time << "µs. " << "Result size: "
            << list.size() << std::endl;
      totalTime += time;
      totalRuns++;
    }
  }
  std::cout << std::endl;
  std::cout << "Average time: " << (totalTime / totalRuns) << "µs."
      << std::endl;
}
