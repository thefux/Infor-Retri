// Copyright 2017, University of Freiburg,
// Chair of Algorithms and Data Structures.
// Björn Buchhold <buchhold@cs.uni-freiburg.de>,
// Patrick Brosi <brosi@cs.uni-freiburg.de>.

#ifndef INVERTEDINDEX_H_
#define INVERTEDINDEX_H_

#include <unordered_map>
#include <string>
#include <vector>

using std::string;
using std::vector;

typedef std::unordered_map<string, vector<int>> InvLists;

// A simple inverted index as explained in lecture 1.
class InvertedIndex {
 public:
  // Constructs the inverted index from given file (one record per line).
  void readFromFile(const string& fileName);

  // Returns the inverted lists.
  const InvLists& getInvertedLists() const;

 private:
  std::unordered_map<string, vector<int>> _invertedLists;

  // Returns the lowercase version of a string.
  inline static string getLowercase(string orig);
};

#endif  // INVERTEDINDEX_H_
