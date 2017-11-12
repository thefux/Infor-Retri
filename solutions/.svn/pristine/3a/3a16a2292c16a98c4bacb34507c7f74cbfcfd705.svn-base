// Copyright 2017, University of Freiburg,
// Chair of Algorithms and Data Structures.
// Björn Buchhold <buchhold@cs.uni-freiburg.de>,
// Patrick Brosi <brosi@cs.uni-freiburg.de>.

// DISCLAIMER: This is not an official master solution but is kindly provided
// by a student of this course (Yi-Chun Lin).
// We give no warranty on the correctness and completeness of this solution.
// The only official master solution is given by the code in the "python"
// folder.

#ifndef INVERTEDINDEX_H_
#define INVERTEDINDEX_H_

#include <unordered_map>
#include <string>
#include <vector>
#include <tuple>

using std::string;
using std::vector;

typedef std::tuple<int, float> Posting;
typedef std::unordered_map<string, vector<Posting>> InvLists;

// A simple inverted index as explained in lecture 1.
class InvertedIndex {
 public:
  InvertedIndex() {}
  explicit InvertedIndex(InvLists lists) : _invertedLists(lists) {}

  // Constructs the inverted index from given file (one record per line).
  void readFromFile(const string& fileName, const float b, const float k);

  // Find all query in invLists and return their intersect.
  void processQuery(const string& strQuery, const bool blEnhance = false);

  // Getter functions
  const InvLists& getInvertedLists() const;
  const vector<string>& getQueryList() const;
  const vector<Posting>& getResultList() const;

  // Returns the lowercase version of a string.
  inline static string getLowercase(string orig);

 private:
  int _numRecords;
  std::unordered_map<string, vector<Posting>> _invertedLists;
  vector<string> _queryList;
  vector<Posting> _resultList;
};

// Helper function
vector<Posting> merge(const vector<Posting>& list1,
    const vector<Posting>& list2, const bool blEnhance = false);
void hightlight(string& str, const string& key, const string& ansi = "7");

#endif  // INVERTEDINDEX_H_
