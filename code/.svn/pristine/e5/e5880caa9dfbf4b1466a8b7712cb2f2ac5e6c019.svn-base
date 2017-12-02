
// Copyright 2017, University of Freiburg,
// Chair of Algorithms and Data Structures.
// Authors: Björn Buchhold <buchholb@cs.uni-freiburg.de>,
//          Patrick Brosi <brosi@cs.uni-freiburg.de>,
//          Claudius Korzen <korzen@cs.uni-freiburg.de>.

#ifndef QGRAMINDEX_H_
#define QGRAMINDEX_H_

#include <unordered_map>
#include <string>
#include <vector>

// First steps towards a q-gram index, written during class.
class QGramIndex {
 public:
  explicit QGramIndex(size_t q) : _q(q) {
    for (size_t i = 0; i < q - 1; ++i) { _padding += '$'; }
  }

  // Build index from given file (one line per entity, see ES5).
  void buildFromFile(const std::string& fileName);

  // Compute q-grams for padded, normalized version of given string.
  std::vector<std::string> computeQGrams(const std::string& word) const;

  // Normalize the given string (remove non-word characters and lower case).
  static std::string normalize(const std::string& str);

  // The q from the q-gram index.
  size_t _q;

  // The padding (q - 1 times $).
  std::string _padding;

  // The inverted lists (one per q-gram).
  std::unordered_map<std::string, std::vector<size_t>> _invertedLists;
};

#endif  // QGRAMINDEX_H_
