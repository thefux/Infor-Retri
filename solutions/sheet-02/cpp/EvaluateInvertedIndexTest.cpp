// Copyright 2017, University of Freiburg.
// Yi-Chun Lin <liny@cs.uni-freiburg.de>.

// DISCLAIMER: This is not an official master solution but is kindly provided
// by a student of this course (Yi-Chun Lin).
// We give no warranty on the correctness and completeness of this solution.
// The only official master solution is given by the code in the "python"
// folder.

#include <gtest/gtest.h>
#include "./EvaluateInvertedIndex.h"

// _____________________________________________________________________________
TEST(EvaluateInvertedIndexTest, readBenchmark) {
  EvaluateInvertedIndex eval;
  eval.readBenchmark("example-benchmark.txt");
  ASSERT_EQ(2, eval.getBenchmark().size());
  ASSERT_EQ(1, eval.getBenchmark().find("animated film")->second[0]);
  ASSERT_EQ(3, eval.getBenchmark().find("animated film")->second[1]);
  ASSERT_EQ(4, eval.getBenchmark().find("animated film")->second[2]);
  ASSERT_EQ(3, eval.getBenchmark().find("short film")->second[0]);
  ASSERT_EQ(4, eval.getBenchmark().find("short film")->second[1]);
}

// _____________________________________________________________________________
TEST(EvaluateFunctionTest, precisionAtK) {
  vector<int> resultList = { 5, 3, 6, 1, 2 };
  set<int> relevantSet = { 1, 2, 5, 6, 7, 8 };
  float prec = precisionAtK(resultList, relevantSet, 2);
  ASSERT_EQ(0.5, prec);
  prec = precisionAtK(resultList, relevantSet, 4);
  ASSERT_EQ(0.75, prec);
}

// _____________________________________________________________________________
TEST(EvaluateFunctionTest, avgPrecision) {
  vector<int> resultList = { 7, 17, 9, 42, 5 };
  set<int> relevantSet = { 5, 7, 12, 42 };
  float prec = avgPrecision(resultList, relevantSet);
  ASSERT_NEAR(0.525, prec, 0.0005);
}

// _____________________________________________________________________________
TEST(EvaluateFunctionTest, evaluate) {
  InvertedIndex ii;
  ii.readFromFile("example.txt", 0.75, 1.75);
  EvaluateInvertedIndex eval;
  eval.readBenchmark("example-benchmark.txt");

  std::tuple<float, float, float> result;
  result = evaluate(ii, eval.getBenchmark(), false);
  ASSERT_NEAR(0.667, std::get<0>(result), 0.0005);
  ASSERT_NEAR(0.833, std::get<1>(result), 0.0005);
  ASSERT_NEAR(0.694, std::get<2>(result), 0.0005);
}
