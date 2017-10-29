// Copyright 2017, University of Freiburg,
// Chair of Algorithms and Data Structures.
// Björn Buchhold <buchhold@cs.uni-freiburg.de>,
// Patrick Brosi <brosi@cs.uni-freiburg.de>.

#include <gtest/gtest.h>
#include "./InvertedIndex.h"


// _____________________________________________________________________________
TEST(InvertedIndexTest, readFromFile) {
  InvertedIndex ii;
  ii.readFromFile("example.txt");
  ASSERT_EQ(4, ii.getInvertedLists().size());
  ASSERT_EQ(1, ii.getInvertedLists().find("doc")->second[0]);
  ASSERT_EQ(2, ii.getInvertedLists().find("doc")->second[1]);
  ASSERT_EQ(3, ii.getInvertedLists().find("doc")->second[2]);
  ASSERT_EQ(1, ii.getInvertedLists().find("first")->second[0]);
  ASSERT_EQ(2, ii.getInvertedLists().find("second")->second[0]);
  ASSERT_EQ(3, ii.getInvertedLists().find("third")->second[0]);
}
