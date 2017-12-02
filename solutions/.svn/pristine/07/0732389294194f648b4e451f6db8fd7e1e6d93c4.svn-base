// Copyright 2017, University of Freiburg,
// Chair of Algorithms and Data Structures.
// Authors: Patrick Brosi <brosi@cs.uni-freiburg.de>,
//          Claudius Korzen <korzen@cs.uni-freiburg.de>.

#include <stddef.h>
#include <sstream>
#include <string>
#include <chrono>
#include <iostream>
#include <vector>
#include <algorithm>
#include <iterator>
#include "./PostingList.h"

// _____________________________________________________________________________
int main(int argc, char **argv) {
  if (argc < 3) {
    std::cerr << "Usage " << argv[0] << " <posting lists>\n";
    exit(1);
  }

  size_t numLists = argc - 1;

  // Read the posting lists.
  std::vector<PostingList> lists;
  for (size_t i = 0; i < numLists; i++) {
    std::cout << "Reading list '" << argv[i + 1] << "' ... ";
    PostingList list;
    list.readFromFile(argv[i + 1]);
    lists.push_back(list);
    std::cout << "Done. Size: " << lists[i].size() << "." << std::endl;
  }

  // Do the efficiency measurements. Repeat each measurement 5 times.
  size_t numRuns = 5;

  // Intersect the lists pairwise.
  for (size_t i = 0; i < numLists; i++) {
    for (size_t j = 0; j < i; j++) {
      std::cout << std::endl;
      std::cout << "Intersect '" << argv[i + 1] << "' & '" << argv[j + 1]
              << "'." << std::endl;

      // Intersect lists[i] and lists[j] using the baseline.
      int totalTime1 = 0;
      std::cout << "Using the baseline:" << std::endl;
      for (size_t run = 0; run < numRuns; run++) {
        auto time1 = std::chrono::high_resolution_clock::now();
        PostingList res = PostingList::intersectBaseline(lists[i], lists[j]);
        auto time2 = std::chrono::high_resolution_clock::now();
        size_t time = std::chrono::duration_cast<std::chrono::microseconds>
                        (time2 - time1).count();
        std::cout << "  Run # " << run << ": "
                  << "Time needed: " << time << "µs. "
                  << "Result size: " << res.size() << std::endl;
        totalTime1 += time;
      }
      std::cout << "  Average time: " << (totalTime1 / numRuns) << "µs."
          << std::endl;

      // Intersect lists[i] and lists[j] using an improved algorithm.
      int totalTime2 = 0;
      std::cout << "Using the improved algorithm:" << std::endl;
      for (size_t run = 0; run < numRuns; run++) {
        auto time1 = std::chrono::high_resolution_clock::now();
        PostingList res = PostingList::intersect(lists[i], lists[j]);
        auto time2 = std::chrono::high_resolution_clock::now();
        size_t time = std::chrono::duration_cast<std::chrono::microseconds>
                        (time2 - time1).count();
        std::cout << "  Run # " << run << ": "
                  << "Time needed: " << time << "µs. "
                  << "Result size: " << res.size() << std::endl;
        totalTime2 += time;
      }
      std::cout << "  Average time: " << (totalTime2 / numRuns) << "µs."
          << std::endl;
    }

    if (numLists > 0) {
      // Intersect all lists.
      std::cout << std::endl;
      std::cout << "Intersecting all lists." << std::endl;
      auto time1 = std::chrono::high_resolution_clock::now();
      PostingList list = lists[0];
      for (size_t i = 0; i < numLists; i++) {
        list = PostingList::intersect(list, lists[i]);
      }
      auto time2 = std::chrono::high_resolution_clock::now();
      size_t time = std::chrono::duration_cast<std::chrono::microseconds>
                        (time2 - time1).count();
      // Transform the ids to string.
      std::stringstream list_str;
      std::copy(list.ids.begin(), list.ids.end(),
          std::ostream_iterator<size_t>(list_str, " "));
      std::cout << "Time needed: " << time << "µs. "
                  << "Result ids: "  << list_str.str() << std::endl;
    }
  }
}
