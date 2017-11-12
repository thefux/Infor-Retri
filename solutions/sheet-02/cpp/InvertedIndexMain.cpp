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
#include <iomanip>
#include <fstream>
#include <string>
#include "./InvertedIndex.h"

using std::cout;
using std::cin;
using std::endl;

// _____________________________________________________________________________
int main(int argc, char** argv) {
  if (argc != 5) {
    std::cerr << "Usage ./InvertedIndexMain <file> <b> <k> "
              << "<use_enhancement {1 | 0}>\n";
    exit(1);
  }

  string fileName = argv[1];
  float b = atof(argv[2]);
  float k = atof(argv[3]);
  bool blEnhance = (argv[4] == string("1"));
  InvertedIndex ii;
  ii.readFromFile(fileName, b, k);

  string strQuery;
  std::ifstream in(fileName.c_str());
  string line;
  int recordId = 0;
  size_t resultId = 0;
  vector<int> resultList;

  while (true) {
    cout << endl << "Please enter keywords\n>";
    getline(cin, strQuery);
    ii.processQuery(strQuery, blEnhance);

    // go back to the beginning of the file
    in.clear();
    in.seekg(0, std::ios::beg);
    recordId = 0;
    resultId = 0;

    // get the first 3 results
    resultList.clear();
    for (size_t i = 0; i < 3; i++) {
      if (i >= ii.getResultList().size()) {
        break;
      }
      resultList.push_back(std::get<0>(ii.getResultList()[i]));
    }

    // go through the file, print out the first 3 results in resultList
    while (std::getline(in, line)) {
      recordId++;

      if (resultId >= resultList.size()) {
        break;
      }

      if (recordId == resultList[resultId]) {
        for (const auto& key : ii.getQueryList()) {
          hightlight(line, key, "1;33");
        }
        cout << "Result " << resultId + 1 << ": " << endl;
        cout << line << endl << endl;
        resultId++;

        // since now the resultList is sorted by BM25, the record ID in
        // the resultList may not be ordered, so we need to go back to
        // the beginning of the file evertime we found a mateched record.
        in.clear();
        in.seekg(0, std::ios::beg);
        recordId = 0;
      }
    }
  }
}
