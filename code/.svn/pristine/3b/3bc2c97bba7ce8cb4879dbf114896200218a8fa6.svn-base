// Copyright 2017, University of Freiburg,
// Chair of Algorithms and Data Structures.
// Authors: Björn Buchhold <buchholb@cs.uni-freiburg.de>,
//          Patrick Brosi <brosi@cs.uni-freiburg.de>,
//          Claudius Korzen <korzen@cs.uni-freiburg.de>.

#include <string>
#include <vector>
#include <fstream>
#include <iostream>

#include "./QGramIndex.h"

// _____________________________________________________________________________
void QGramIndex::buildFromFile(const std::string& fileName) {
  _invertedLists.clear();

  std::string line;

  // Read the file line by line.
  std::ifstream in(fileName.c_str(), std::ios_base::in);
  size_t wordId = 0;
  // Ignore first line.
  std::getline(in, line);
  // Iterate through the remaining lines.
  while (std::getline(in, line)) {
    size_t posTab1 = line.find('\t');
    std::string word = line.substr(0, posTab1 + 1);
    wordId++;

    // Compute the q-grams of the (normalized) entity name.
    for (const std::string& qGram : computeQGrams(word)) {
      _invertedLists[qGram].push_back(wordId);
    }
  }
}

// _____________________________________________________________________________
std::vector<std::string> QGramIndex::computeQGrams(const std::string& word)
    const {
  std::vector<std::string> result;

  std::string padded = _padding + normalize(word) + _padding;
  for (size_t i = 0; i < padded.size() - _q + 1; ++i) {
    result.push_back(padded.substr(i, _q));
  }

  return result;
}

// _____________________________________________________________________________
std::string QGramIndex::normalize(const std::string& str) {
  std::string s;
  for (size_t i = 0; i < str.size(); ++i) {
    if (!std::isalnum(str[i])) {
      continue;
    }
    s += static_cast<char>(tolower(str[i]));
  }

  return s;
}
