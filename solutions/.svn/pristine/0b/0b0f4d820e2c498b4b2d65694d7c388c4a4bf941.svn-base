// Copyright 2017, University of Freiburg,
// Chair of Algorithms and Data Structures.
// Authors: Björn Buchhold <buchholb@cs.uni-freiburg.de>,
//          Patrick Brosi <brosi@cs.uni-freiburg.de>,
//          Claudius Korzen <korzen@cs.uni-freiburg.de>.

#include <math.h>
#include <string>
#include <vector>
#include <iostream>
#include <chrono>

#include "./QGramIndex.h"

// _____________________________________________________________________________
int main(int argc, char** argv) {
  // Parse command line arguments.
  if (argc < 2) {
    std::cerr << "Usage: " << argv[0] << " <entity-file> [--with-synonyms]\n"
        << std::endl;
    exit(1);
  }

  std::string fileName = argv[1];
  bool withSynonyms = false;
  if (argc > 2) {
    std::string withSynonymsStr = argv[2];
    withSynonyms = withSynonymsStr.compare("--with-synonyms") == 0;
  }

  std::cout << "Building index from '" << fileName << "' ... ";
  std::cout << std::flush;

  // Build 3-gram index from given file.
  auto start1 = std::chrono::high_resolution_clock::now();
  QGramIndex index(3, withSynonyms);
  index.buildFromFile(fileName);
  auto end1 = std::chrono::high_resolution_clock::now();

  std::cout << "done in "
            << std::chrono::duration_cast<std::chrono::milliseconds>
               (end1 - start1).count() << "ms." << std::endl;

  while (true) {
    std::cout << std::string(80, '-') << std::endl;

    std::string query;
    std::cout << "Query: " << std::flush;
    std::getline(std::cin, query);

    // Normalize the query.
    query = QGramIndex::normalize(query);
    int delta = query.size() / 4;

    auto start2 = std::chrono::high_resolution_clock::now();
    auto result = index.findMatches(query, delta);
    auto end2 = std::chrono::high_resolution_clock::now();

    auto matches = result.first;

    std::cout << std::endl;
    std::cout << "Found " << matches.size() << " matches. ";

    size_t numResults = std::min(size_t(5), matches.size());
    if (numResults > 0) {
      std::cout << "The top-" << numResults << " results are:" << std::endl;

      for (size_t i = 0; i < numResults; i++) {
        auto e = matches[i];

        std::cout << "\n\033[1m(" << (i + 1) << ") " << e.name << "\033[0m ";
        if (e.matchedSynonym.size() > 0) {
          std::cout << "(Matched Synonym: '" << e.matchedSynonym << "')"
            << std::endl;
        } else {
          std::cout << std::endl;
        }
        std::cout << "Description:   " << e.description << std::endl;
        if (e.wikipediaUrl.size() > 0) {
          std::cout << "Wikipedia-URL: " << e.wikipediaUrl << std::endl;
        }
        if (e.wikidataId.size() > 0) {
          std::cout << "Wikidata-URL:  http://www.wikidata.org/wiki/"
            << e.wikidataId << std::endl;
        }
        std::cout << "PED:           " << e.ped << std::endl;
        std::cout << "Score:         " << e.score << std::endl;
      }
    }

    std::cout << std::endl;
    std::cout << "Time needed to find matches: " <<
        std::chrono::duration_cast<std::chrono::milliseconds>
        (end2 - start2).count() << "ms. ";
    std::cout << "#PED computations: " << result.second << "." << std::endl;
  }

  return 0;
}
