// Copyright 2017, University of Freiburg,
// Chair of Algorithms and Data Structures.
// Björn Buchhold <buchhold@cs.uni-freiburg.de>,
// Patrick Brosi <brosi@cs.uni-freiburg.de>.

#include <iostream>
#include <string>
#include "./InvertedIndex.h"

// _____________________________________________________________________________
int main(int argc, char** argv) {
  if (argc != 2) {
    std::cerr << "Usage ./InvertedIndexMain <file>\n";
    exit(1);
  }

  string fileName = argv[1];
  InvertedIndex ii;
  ii.readFromFile(fileName);

  // Output the lengths of the inverted lists (= frequencies of the words).
  for (auto it = ii.getInvertedLists().begin();
      it != ii.getInvertedLists().end();
      ++it) {
    std::cout << it->first << ' ' << it->second.size() << std::endl;
  }
}
