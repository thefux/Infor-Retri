// Copyright 2017, University of Freiburg,
// Chair of Algorithms and Data Structures.
// Björn Buchhold <buchhold@cs.uni-freiburg.de>,
// Patrick Brosi <brosi@cs.uni-freiburg.de>.

#include <iostream>
#include <fstream>
#include <algorithm>
#include <string>
#include "./InvertedIndex.h"

// _____________________________________________________________________________
void InvertedIndex::readFromFile(const string& fileName) {
  string line;
  std::ifstream in(fileName.c_str());
  uint64_t recordId = 0;

  while (std::getline(in, line)) {
    recordId++;

    size_t i = 0;
    size_t start = 0;
    while (i <= line.size()) {
      // The > 0 so that we don't split at non-ascii chars.
      // It is very important that one can search for "björn" :-).
      if (i == line.size() || (line[i] > 0 && !isalnum(line[i])))  {
        if (i > start) {
          string word = getLowercase(line.substr(start, i - start));
          // Automatically default constructs a list if the
          // word is seen for the first time.
          _invertedLists[word].push_back(recordId);
        }
        start = i + 1;
      }
      ++i;
    }
  }
}

// ____________________________________________________________________________
inline string InvertedIndex::getLowercase(string orig) {
  std::transform(orig.begin(), orig.end(), orig.begin(), ::tolower);
  return orig;
}

// ____________________________________________________________________________
const InvLists& InvertedIndex::getInvertedLists() const {
  return _invertedLists;
}
