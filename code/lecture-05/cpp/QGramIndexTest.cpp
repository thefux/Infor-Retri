// Copyright 2017, University of Freiburg,
// Chair of Algorithms and Data Structures.
// Authors: Björn Buchhold <buchholb@cs.uni-freiburg.de>,
//          Patrick Brosi <brosi@cs.uni-freiburg.de>,
//          Claudius Korzen <korzen@cs.uni-freiburg.de>.

#include <gtest/gtest.h>
#include "./QGramIndex.h"


// _____________________________________________________________________________
TEST(QGramIndexTest, testBuildFromFile) {
  QGramIndex index(3);
  index.buildFromFile("example.tsv");

  ASSERT_EQ(9, index._invertedLists.size());

  ASSERT_EQ(1, index._invertedLists["$$b"].size());
  ASSERT_EQ(2, index._invertedLists["$$b"][0]);
  ASSERT_EQ(1, index._invertedLists["$$f"].size());
  ASSERT_EQ(1, index._invertedLists["$$f"][0]);
  ASSERT_EQ(1, index._invertedLists["$br"].size());
  ASSERT_EQ(2, index._invertedLists["$br"][0]);
  ASSERT_EQ(1, index._invertedLists["$fr"].size());
  ASSERT_EQ(1, index._invertedLists["$fr"][0]);
  ASSERT_EQ(1, index._invertedLists["bre"].size());
  ASSERT_EQ(2, index._invertedLists["bre"][0]);
  ASSERT_EQ(1, index._invertedLists["fre"].size());
  ASSERT_EQ(1, index._invertedLists["fre"][0]);
  ASSERT_EQ(2, index._invertedLists["ei$"].size());
  ASSERT_EQ(1, index._invertedLists["ei$"][0]);
  ASSERT_EQ(2, index._invertedLists["ei$"][1]);
  ASSERT_EQ(2, index._invertedLists["i$$"].size());
  ASSERT_EQ(1, index._invertedLists["i$$"][0]);
  ASSERT_EQ(2, index._invertedLists["i$$"][1]);
  ASSERT_EQ(2, index._invertedLists["rei"].size());
  ASSERT_EQ(1, index._invertedLists["rei"][0]);
  ASSERT_EQ(2, index._invertedLists["rei"][1]);
}

// _____________________________________________________________________________
TEST(QGramIndexTest, testComputeQGrams) {
  QGramIndex index(3);
  auto qgrams = index.computeQGrams("freiburg");
  ASSERT_EQ(qgrams.size(), 10);
  ASSERT_EQ(qgrams[0], "$$f");
  ASSERT_EQ(qgrams[1], "$fr");
  ASSERT_EQ(qgrams[2], "fre");
  ASSERT_EQ(qgrams[3], "rei");
  ASSERT_EQ(qgrams[4], "eib");
  ASSERT_EQ(qgrams[5], "ibu");
  ASSERT_EQ(qgrams[6], "bur");
  ASSERT_EQ(qgrams[7], "urg");
  ASSERT_EQ(qgrams[8], "rg$");
  ASSERT_EQ(qgrams[9], "g$$");
}

// _____________________________________________________________________________
TEST(QGramIndexTest, testNormalizeString) {
  ASSERT_EQ("freiburg", QGramIndex::normalize("Frei, burg !!"));
  ASSERT_EQ("freiburg", QGramIndex::normalize("freiburg"));
}
