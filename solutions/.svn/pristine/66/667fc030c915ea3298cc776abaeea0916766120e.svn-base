// Copyright 2017, University of Freiburg,
// Chair of Algorithms and Data Structures.
// Björn Buchhold <buchhold@cs.uni-freiburg.de>,
// Patrick Brosi <brosi@cs.uni-freiburg.de>.

// DISCLAIMER: This is not an official master solution but is kindly provided
// by a student of this course (Yi-Chun Lin).
// We give no warranty on the correctness and completeness of this solution.
// The only official master solution is given by the code in the "python"
// folder.

#include <gtest/gtest.h>
#include "./InvertedIndex.h"

#define DELTA 0.0005
void cmpPosting(const Posting& post1, const Posting& post2,
    float delta = DELTA) {
  ASSERT_EQ(std::get<0>(post1), std::get<0>(post2));
  ASSERT_NEAR(std::get<1>(post1), std::get<1>(post2), delta);
}

// _____________________________________________________________________________
TEST(InvertedIndexTest, readFromFile) {
  InvertedIndex ii;
  ii.readFromFile("example.txt", 0.75, 1.75);
  ASSERT_EQ(6, ii.getInvertedLists().size());

  vector<Posting> posts;
  vector<string> ansKeyList = { "animated", "animation", "film",
      "movie", "non", "short" };
  vector<vector<Posting>> ansPostingList = {
      { std::make_tuple(1, 0.459), std::make_tuple(2, 0.402),
          std::make_tuple(4, 0.358) },
      { std::make_tuple(3, 2.211) },
      { std::make_tuple(2, 0.969), std::make_tuple(4, 0.863) },
      { std::make_tuple(1, 0.0), std::make_tuple(2, 0.0),
          std::make_tuple(3, 0.0), std::make_tuple(4, 0.0) },
      { std::make_tuple(2, 1.938) },
      { std::make_tuple(3, 1.106), std::make_tuple(4, 1.313) }};

  for (size_t i = 0; i < ansKeyList.size(); i++) {
    const string& key = ansKeyList[i];
    const vector<Posting> ansPosts = ansPostingList[i];

    posts = ii.getInvertedLists().find(key)->second;
    ASSERT_EQ(ansPosts.size(), posts.size());
    for (size_t j = 0; j < ansPosts.size(); j++) {
      cmpPosting(ansPosts[j], posts[j]);
    }
  }

  InvertedIndex ii2;
  ii2.readFromFile("example.txt", 0, std::numeric_limits<float>::max());
  ASSERT_EQ(6, ii2.getInvertedLists().size());

  vector<vector<Posting>> ansPostingList2 = {
      { std::make_tuple(1, 0.415), std::make_tuple(2, 0.415),
          std::make_tuple(4, 0.415) },
      { std::make_tuple(3, 2.0) },
      { std::make_tuple(2, 1.0), std::make_tuple(4, 1.0) },
      { std::make_tuple(1, 0.0), std::make_tuple(2, 0.0),
          std::make_tuple(3, 0.0), std::make_tuple(4, 0.0) },
      { std::make_tuple(2, 2.0) },
      { std::make_tuple(3, 1.0), std::make_tuple(4, 2.0) }};

  for (size_t i = 0; i < ansKeyList.size(); i++) {
    const string& key = ansKeyList[i];
    const vector<Posting> ansPosts = ansPostingList2[i];

    posts = ii2.getInvertedLists().find(key)->second;
    ASSERT_EQ(ansPosts.size(), posts.size());
    for (size_t j = 0; j < ansPosts.size(); j++) {
      cmpPosting(ansPosts[j], posts[j]);
    }
  }
}

// _____________________________________________________________________________
TEST(InvertedIndexTest, processQuery) {
  InvLists lists = {
      { "foo", { std::make_tuple(1, 0.2), std::make_tuple(3, 0.6) }},
      { "bar", { std::make_tuple(1, 0.4), std::make_tuple(2, 0.7),
          std::make_tuple(3, 0.5) }},
      { "baz", { std::make_tuple(2, 0.1) }}
  };
  InvertedIndex ii(lists);
  ii.processQuery("foo bar");
  vector<Posting> retList = ii.getResultList();
  ASSERT_EQ(3, retList.size());
  cmpPosting(std::make_tuple(3, 1.1), retList[0]);
  cmpPosting(std::make_tuple(2, 0.7), retList[1]);
  cmpPosting(std::make_tuple(1, 0.6), retList[2]);
}

// _____________________________________________________________________________
TEST(HelperTest, merge) {
  vector<Posting> p1 = { std::make_tuple(1, 2.1), std::make_tuple(5, 3.2) };
  vector<Posting> p2 = { std::make_tuple(1, 1.7), std::make_tuple(2, 1.3),
      std::make_tuple(5, 3.3) };
  vector<Posting> retP = merge(p1, p2);
  ASSERT_EQ(3, retP.size());
  cmpPosting(std::make_tuple(1, 3.8), retP[0], 0);
  cmpPosting(std::make_tuple(2, 1.3), retP[1], 0);
  cmpPosting(std::make_tuple(5, 6.5), retP[2], 0);
}

// _____________________________________________________________________________
TEST(HelperTest, hightlight) {
  string str = "test key test";
  const string key = "key";
  hightlight(str, key, "1;31");
  ASSERT_EQ("test \033[1;31mkey\033[0m test", str);
}

