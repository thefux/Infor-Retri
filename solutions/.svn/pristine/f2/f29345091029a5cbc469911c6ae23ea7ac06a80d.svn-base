// Copyright 2017, University of Freiburg.
// Yi-Chun Lin <liny@cs.uni-freiburg.de>.

// DISCLAIMER: This is not an official master solution but is kindly provided
// by a student of this course (Yi-Chun Lin).
// We give no warranty on the correctness and completeness of this solution.
// The only official master solution is given by the code in the "python"
// folder.

#ifndef EVALUATEINVERTEDINDEX_H_
#define EVALUATEINVERTEDINDEX_H_

#include <unordered_map>
#include <string>
#include <vector>
#include <tuple>
#include <set>
#include "./InvertedIndex.h"

using std::string;
using std::vector;
using std::set;

typedef std::unordered_map<string, vector<int>> Benchmark;

class EvaluateInvertedIndex {
 public:
  // Read and store benchmark data from given file (one query per line)
  void readBenchmark(const string& fileName);

  // Getter functions
  const Benchmark& getBenchmark() const;

 private:
  std::unordered_map<string, vector<int>> _benchmark;
};

// Evaluation functions
float precisionAtK(const vector<int>& resultList, const set<int>& relevantSet,
    const size_t k);
float avgPrecision(const vector<int>& resultList, const set<int>& relevantSet);

// Evaluate certain inverted index given a benchmark
std::tuple<float, float, float> evaluate(InvertedIndex& ii,
    const Benchmark& benchmark, const bool blEnhance);

#endif  // EVALUATEINVERTEDINDEX_H_
