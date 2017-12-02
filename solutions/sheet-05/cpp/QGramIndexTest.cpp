// Copyright 2017, University of Freiburg,
// Chair of Algorithms and Data Structures.
// Authors: Björn Buchhold <buchholb@cs.uni-freiburg.de>,
//          Patrick Brosi <brosi@cs.uni-freiburg.de>,
//          Claudius Korzen <korzen@cs.uni-freiburg.de>.

#include <gtest/gtest.h>
#include "./QGramIndex.h"


// _____________________________________________________________________________
TEST(QGramIndexTest, testBuildFromFile) {
  QGramIndex index(3, false);
  index.buildFromFile("example.tsv");

  ASSERT_EQ(7, index._invertedLists.size());

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
  ASSERT_EQ(2, index._invertedLists["rei"].size());
  ASSERT_EQ(1, index._invertedLists["rei"][0]);
  ASSERT_EQ(2, index._invertedLists["rei"][1]);
}

// _____________________________________________________________________________
TEST(QGramIndexTest, testMergeLists) {
  std::vector<size_t> first { 1, 1, 3, 5 };
  std::vector<size_t> second { 2, 3, 3, 9, 9 };
  std::vector<const std::vector<size_t>* > lists { &first, &second };

  // std::vector<std::pair<size_t, size_t>> result = QGramIndex::mergeLists
  // (lists);

//  ASSERT_EQ(5, result.size());
//  ASSERT_EQ(1, result[0].first);
//  ASSERT_EQ(2, result[0].second);
//  ASSERT_EQ(2, result[1].first);
//  ASSERT_EQ(1, result[1].second);
//  ASSERT_EQ(3, result[2].first);
//  ASSERT_EQ(3, result[2].second);
//  ASSERT_EQ(5, result[3].first);
//  ASSERT_EQ(1, result[3].second);
//  ASSERT_EQ(9, result[4].first);
//  ASSERT_EQ(2, result[4].second);
}

// _____________________________________________________________________________
TEST(QGramIndexTest, testPrefixEditDistance) {
  ASSERT_EQ(0, QGramIndex::prefixEditDistance("frei", "frei", 0));
  ASSERT_EQ(0, QGramIndex::prefixEditDistance("frei", "freiburg", 0));
  ASSERT_EQ(1, QGramIndex::prefixEditDistance("frei", "breifurg", 1));
  ASSERT_EQ(3, QGramIndex::prefixEditDistance("freiburg", "stuttgart", 2));
}

// _____________________________________________________________________________
TEST(QGramIndexTest, testFindMatches) {
  QGramIndex index(3, false);
  index.buildFromFile("example.tsv");

  auto result0 = index.findMatches("frei", 0);
  ASSERT_EQ(1, result0.first.size());
  ASSERT_EQ("frei", result0.first[0].name);
  ASSERT_EQ(3, result0.first[0].score);
  ASSERT_EQ("a word", result0.first[0].description);
  ASSERT_EQ(0, result0.first[0].ped);
  ASSERT_EQ(1, result0.second);

  auto result1 = index.findMatches("frei", 2);
  ASSERT_EQ(2, result1.first.size());
  ASSERT_EQ("frei", result1.first[0].name);
  ASSERT_EQ(3, result1.first[0].score);
  ASSERT_EQ("a word", result1.first[0].description);
  ASSERT_EQ(0, result1.first[0].ped);
  ASSERT_EQ("brei", result1.first[1].name);
  ASSERT_EQ(2, result1.first[1].score);
  ASSERT_EQ("another word", result1.first[1].description);
  ASSERT_EQ(1, result1.first[1].ped);
  ASSERT_EQ(2, result1.second);

  auto result2 = index.findMatches("freibu", 2);
  ASSERT_EQ(1, result2.first.size());
  ASSERT_EQ("frei", result2.first[0].name);
  ASSERT_EQ(3, result2.first[0].score);
  ASSERT_EQ("a word", result2.first[0].description);
  ASSERT_EQ(2, result2.first[0].ped);
  ASSERT_EQ(2, result2.second);
}

// _____________________________________________________________________________
TEST(QGramIndexTest, testRankMatches) {
  std::vector<Entity> matches;

  Entity entity0("foo", 3);
  entity0.ped = 2;
  matches.push_back(entity0);

  Entity entity1("bar", 7);
  entity1.ped = 0;
  matches.push_back(entity1);

  Entity entity2("baz", 2);
  entity2.ped = 1;
  matches.push_back(entity2);

  Entity entity3("boo", 5);
  entity3.ped = 1;
  matches.push_back(entity3);

  std::vector<Entity> ranked = QGramIndex::rankMatches(matches);
  ASSERT_EQ(4, ranked.size());
  ASSERT_EQ("bar", ranked[0].name);
  ASSERT_EQ("boo", ranked[1].name);
  ASSERT_EQ("baz", ranked[2].name);
  ASSERT_EQ("foo", ranked[3].name);
}

// _____________________________________________________________________________
TEST(QGramIndexTest, testComputeQGrams) {
  QGramIndex index(3, false);
  auto qgrams = index.computeQGrams("freiburg");
  ASSERT_EQ(qgrams.size(), 8);
  ASSERT_EQ(qgrams[0], "$$f");
  ASSERT_EQ(qgrams[1], "$fr");
  ASSERT_EQ(qgrams[2], "fre");
  ASSERT_EQ(qgrams[3], "rei");
  ASSERT_EQ(qgrams[4], "eib");
  ASSERT_EQ(qgrams[5], "ibu");
  ASSERT_EQ(qgrams[6], "bur");
  ASSERT_EQ(qgrams[7], "urg");
}

// _____________________________________________________________________________
TEST(QGramIndexTest, testNormalizeString) {
  ASSERT_EQ("freiburg", QGramIndex::normalize("Frei, burg !!"));
  ASSERT_EQ("freiburg", QGramIndex::normalize("freiburg"));
}
