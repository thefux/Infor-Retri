// Copyright 2017, University of Freiburg.
// Yi-Chun Lin <liny@cs.uni-freiburg.de>.

// DISCLAIMER: This is not an official master solution but is kindly provided
// by a student of this course (Yi-Chun Lin).
// We give no warranty on the correctness and completeness of this solution.
// The only official master solution is given by the code in the "python"
// folder.

#include <iostream>
#include <fstream>
#include <string>
#include <set>
#include "./EvaluateInvertedIndex.h"

// _____________________________________________________________________________
void EvaluateInvertedIndex::readBenchmark(const string& fileName) {
  string line;
  std::ifstream in(fileName.c_str());

  while (std::getline(in, line)) {
    size_t i = 0;
    size_t start = 0;
    string title;
    int record;
    while (i <= line.size()) {
      // parse title
      if (start == 0) {
        if (line[i] == '\t') {
          title = line.substr(0, i);
          start = i + 1;
        }
      // parse record
      } else if (i == line.size() || line[i] == ' ')  {
        if (i > start) {
          record = atoi(line.substr(start, i - start).c_str());
          _benchmark[title].push_back(record);
          start = i + 1;
        }
      }
      ++i;
    }
  }
}

// ____________________________________________________________________________
const Benchmark& EvaluateInvertedIndex::getBenchmark() const {
  return _benchmark;
}

// ____________________________________________________________________________
float precisionAtK(const vector<int>& resultList, const set<int>& relevantSet,
    const size_t k) {
  int hitCount = 0;

  // Look at the first k results, compute hit counts in the relevantSet.
  for (size_t i = 0; (i < k) && (i < resultList.size()); i++) {
    if (relevantSet.count(resultList[i]) == 1) {
      hitCount++;
    }
  }
  return static_cast<float>(hitCount) / k;
}

// ____________________________________________________________________________
float avgPrecision(const vector<int>& resultList, const set<int>& relevantSet) {
  float sumP = 0.0f;

  for (size_t i = 0; i < resultList.size(); i++) {
    if (relevantSet.count(resultList[i]) == 1) {
      sumP += precisionAtK(resultList, relevantSet, i+1);
    }
  }

  return sumP / relevantSet.size();
}

// ____________________________________________________________________________
std::tuple<float, float, float> evaluate(InvertedIndex& ii,
    const Benchmark& benchmark, const bool blEnhance) {
  float sumPat3 = 0.0f;
  float sumPatR = 0.0f;
  float sumAP = 0.0f;
  size_t size = benchmark.size();
  vector<int> resultList;

  for (const auto& elem : benchmark) {
    const string& query = elem.first;
    const auto& relevantList = elem.second;
    const std::set<int> relevantSet(relevantList.begin(), relevantList.end());

    ii.processQuery(query, blEnhance);
    resultList.clear();
    for (const auto& result : ii.getResultList()) {
      resultList.push_back(std::get<0>(result));
    }

    sumPat3 += precisionAtK(resultList, relevantSet, 3);
    sumPatR += precisionAtK(resultList, relevantSet, relevantList.size());
    sumAP += avgPrecision(resultList, relevantSet);
  }

  return std::make_tuple(sumPat3/size, sumPatR/size, sumAP/size);
}
