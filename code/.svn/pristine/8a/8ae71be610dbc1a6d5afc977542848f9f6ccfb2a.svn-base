// Copyright 2017, University of Freiburg,
// Chair of Algorithms and Data Structures.
// Authors: Björn Buchhold <buchholb@cs.uni-freiburg.de>,
//          Patrick Brosi <brosi@cs.uni-freiburg.de>,
//          Claudius Korzen <korzen@cs.uni-freiburg.de>.

#include <stdio.h>
#include <iostream>
#include <string>
#include <chrono>

#include "./QGramIndex.h"

// _____________________________________________________________________________
int main(int argc, char** argv) {
  // Parse command line arguments.
  if (argc < 2) {
    std::cerr << "Usage: " << argv[0] << " <entity-file>\n";
    exit(1);
  }

  std::string fileName = argv[1];

  std::cout << "Building index from '" << fileName << "' ...";
  std::cout << std::flush;

  // Build 3-gram index from given file.
  QGramIndex index(3);

  auto start = std::chrono::high_resolution_clock::now();
  index.buildFromFile(fileName);
  auto end = std::chrono::high_resolution_clock::now();

  std::cout << " done in "
            << std::chrono::duration_cast<std::chrono::microseconds>
               (end - start).count() << "us." << std::endl;

  // Print all q-grams and the lengths of their inverted list.
  for (auto it : index._invertedLists) {
    std::cout << it.first << "\t" << it.second.size() << std::endl;
  }

  return 0;
}
