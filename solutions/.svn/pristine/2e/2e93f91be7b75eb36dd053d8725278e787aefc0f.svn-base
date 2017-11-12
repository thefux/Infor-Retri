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
#include "./InvertedIndex.h"
#include "./EvaluateInvertedIndex.h"

using std::cout;
using std::endl;

// _____________________________________________________________________________
int main(int argc, char** argv) {
  if (argc != 6) {
    std::cerr << "Usage ./EvaluateInvertedIndexMain "
              << "<dataset_file> <benchmark_file> "
              << "<b> <k> <use_enhancement {1 | 0}>\n";
    exit(1);
  }

  string datasetFile = argv[1];
  string BenchmarkFile = argv[2];
  float b = atof(argv[3]);
  float k = atof(argv[4]);
  bool blEnhance = (argv[5] == string("1"));

  InvertedIndex ii;
  ii.readFromFile(datasetFile, b, k);

  EvaluateInvertedIndex eval;
  eval.readBenchmark(BenchmarkFile);

  std::tuple<float, float, float> result;
  result = evaluate(ii, eval.getBenchmark(), blEnhance);
  cout << "MP@3: " << std::get<0>(result) << endl
       << "MP@R: " << std::get<1>(result) << endl
       << "MAP : " << std::get<2>(result) << endl;

  return 0;
}
