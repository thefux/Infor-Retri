// Copyright 2017, University of Freiburg,
// Chair of Algorithms and Data Structures.
// Björn Buchhold <buchhold@cs.uni-freiburg.de>,
// Patrick Brosi <brosi@cs.uni-freiburg.de>.

// DISCLAIMER: This is not an official master solution but is kindly provided
// by a student of this course (Yi-Chun Lin).
// We give no warranty on the correctness and completeness of this solution.
// The only official master solution is given by the code in the "python"
// folder.

#include <iostream>
#include <fstream>
#include <algorithm>
#include <math.h>
#include <climits>
#include <string>
#include "./InvertedIndex.h"

// _____________________________________________________________________________
void InvertedIndex::readFromFile(const string& fileName, const float b,
    const float k) {
  string line;
  std::ifstream in(fileName.c_str());
  int recordId = 0;
  // DL: document(record) length
  vector<int> DLlist;
  uint64_t sumDL = 0;

  // First pass: read the file and generate inverted_lists
  while (std::getline(in, line)) {
    recordId++;
    DLlist.push_back(0);

    size_t i = 0;
    size_t start = 0;
    while (i <= line.size()) {
      // The > 0 so that we don't split at non-ascii chars.
      // It is very important that one can search for "björn" :-).
      if (i == line.size() || (line[i] > 0 && !isalpha(line[i])))  {
        if (i > start) {
          DLlist[recordId-1]++;

          string word = getLowercase(line.substr(start, i - start));
          // Create new posting if the reocrd is not exsit.
          // Otherwise, increase term frequency in current posting.
          if (_invertedLists[word].empty() ||
            std::get<0>(_invertedLists[word].back()) != recordId) {
            _invertedLists[word].push_back(std::make_tuple(recordId, 1));
          } else {
            std::get<1>(_invertedLists[word].back())++;
          }
        }
        start = i + 1;
      }
      ++i;
    }
    sumDL += DLlist[recordId-1];
  }
  _numRecords = recordId;

  // Second pass: traverse inverted_lists,
  // iterate each inverted_list and replace tf by BM25:
  // tf' = tf * (k + 1) / (k * (1 - b + b * DL / AVDL) + tf) * log2(N/df),
  // where N is the total number of documents and df is the number of
  // documents that contains the word.

  for (auto& data : _invertedLists) {
    vector<Posting>& invList = data.second;
    if (invList.empty()) {
      continue;
    }
    float idf = log2f(static_cast<float>(_numRecords)/invList.size());
    float AVDL = static_cast<float>(sumDL) / _numRecords;
    for (auto& post : invList) {
      const int id = std::get<0>(post);
      float alpha = 1 - b + b * DLlist[id-1] / AVDL;
      float& tf = std::get<1>(post);
      tf *= (k + 1) / (k * alpha + tf) * idf;
    }
  }
}

// _____________________________________________________________________________
void InvertedIndex::processQuery(const string& strQuery, const bool blEnhance) {
  size_t i = 0;
  size_t start = 0;
  vector<vector<Posting>> recordLists;

  _queryList.clear();
  _resultList.clear();

  // parse strQuery
  while (i <= strQuery.size()) {
    // The > 0 so that we don't split at non-ascii chars.
    // It is very important that one can search for "björn" :-).
    if (i == strQuery.size() || (strQuery[i] > 0 && !isalnum(strQuery[i])))  {
      if (i > start) {
        string word = getLowercase(strQuery.substr(start, i - start));
        _queryList.push_back(word);
        // for each query word, find its invList, store in recordLists.
        recordLists.push_back(_invertedLists[word]);
      }
      start = i + 1;
    }
    ++i;
  }

  if (recordLists.empty()) {
    return;
  }

  // if use enhancement, take popularity into consideration.
  if (blEnhance) {
    for (auto& invList : recordLists) {
      for (auto& post : invList) {
        const int id = std::get<0>(post);
        float& tf = std::get<1>(post);
        float pop = (static_cast<float>(_numRecords) - id)/_numRecords;
        tf *= (0.5 + 0.5 * pop);
      }
    }
  }

  // merge recordLists.
  for (const auto& list : recordLists) {
    _resultList = merge(_resultList, list, blEnhance);
  }

  // Sort the resulting list by BM25 scores in descending order.
  auto sortFn = [&] (Posting a, Posting b) -> bool {
    return (std::get<1>(a) > std::get<1>(b));
  };
  std::sort(_resultList.begin(), _resultList.end(), sortFn);
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

// ____________________________________________________________________________
const vector<string>& InvertedIndex::getQueryList() const {
  return _queryList;
}

// ____________________________________________________________________________
const vector<Posting>& InvertedIndex::getResultList() const {
  return _resultList;
}

// ____________________________________________________________________________
vector<Posting> merge(const vector<Posting>& list1,
    const vector<Posting>& list2, const bool blEnhance) {
  size_t i = 0;
  size_t j = 0;
  vector<Posting> retList;
  float enhanceFactor = blEnhance ? 1.1 : 1;

  while (i < list1.size() || j < list2.size()) {
    const int recordI = i < list1.size() ? std::get<0>(list1[i]) : INT_MAX;
    const int recordJ = j < list2.size() ? std::get<0>(list2[j]) : INT_MAX;

    if (recordI == recordJ) {
      retList.push_back(std::make_tuple(recordI,
         enhanceFactor * (std::get<1>(list1[i]) + std::get<1>(list2[j]))));
      i++;
      j++;
    } else if (recordI < recordJ) {
      retList.push_back(list1[i]);
      i++;
    } else {
      retList.push_back(list2[j]);
      j++;
    }
  }

  return retList;
}

// ____________________________________________________________________________
void hightlight(string& str, const string& key, const string& ansi) {
  const string ansiStart = "\033[" + ansi + "m";
  const string ansiEnd = "\33[0m";
  size_t i = 0;
  size_t start = 0;

  while (i <= str.size()) {
    // The > 0 so that we don't split at non-ascii chars.
    // It is very important that one can search for "björn" :-).
    if (i == str.size() || (str[i] > 0 && !isalnum(str[i])))  {
      if (i > start) {
        string word = InvertedIndex::getLowercase(str.substr(start, i - start));
        if (word == InvertedIndex::getLowercase(key)) {
          str.insert(i, ansiEnd);
          str.insert(start, ansiStart);
          i = i + ansiStart.size() + ansiEnd.size();
        }
      }
      start = i + 1;
    }
    ++i;
  }
}
