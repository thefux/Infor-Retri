// Copyright 2017, University of Freiburg,
// Chair of Algorithms and Data Structures.
// Authors: Patrick Brosi <brosi@cs.uni-freiburg.de>,
//          Claudius Korzen <korzen@cs.uni-freiburg.de>.

#include <gtest/gtest.h>
#include "./PostingList.h"

// _____________________________________________________________________________
TEST(PostingList, readFromFile) {
  PostingList list1;
  list1.readFromFile("example1.txt");
  ASSERT_EQ(3, list1.size());
  ASSERT_EQ(2, list1.getId(0));
  ASSERT_EQ(3, list1.getId(1));
  ASSERT_EQ(6, list1.getId(2));
  ASSERT_EQ(5, list1.getScore(0));
  ASSERT_EQ(1, list1.getScore(1));
  ASSERT_EQ(2, list1.getScore(2));

  PostingList list2;
  list2.readFromFile("example2.txt");
  ASSERT_EQ(4, list2.size());
  ASSERT_EQ(1, list2.getId(0));
  ASSERT_EQ(2, list2.getId(1));
  ASSERT_EQ(4, list2.getId(2));
  ASSERT_EQ(6, list2.getId(3));
  ASSERT_EQ(1, list2.getScore(0));
  ASSERT_EQ(4, list2.getScore(1));
  ASSERT_EQ(3, list2.getScore(2));
  ASSERT_EQ(3, list2.getScore(3));

  PostingList list3;
  list3.readFromFile("example3.txt");
  ASSERT_EQ(2, list3.size());
  ASSERT_EQ(5, list3.getId(0));
  ASSERT_EQ(7, list3.getId(1));
  ASSERT_EQ(1, list3.getScore(0));
  ASSERT_EQ(2, list3.getScore(1));
}

// _____________________________________________________________________________
TEST(PostingList, intersectBaseline) {
  PostingList list1;
  list1.readFromFile("example1.txt");
  PostingList list2;
  list2.readFromFile("example2.txt");
  PostingList list3;
  list3.readFromFile("example3.txt");

  PostingList result1 = PostingList::intersectBaseline(list1, list2);
  ASSERT_EQ(2, result1.size());
  ASSERT_EQ(2, result1.getId(0));
  ASSERT_EQ(6, result1.getId(1));
  ASSERT_EQ(9, result1.getScore(0));
  ASSERT_EQ(5, result1.getScore(1));

  PostingList result2 = PostingList::intersectBaseline(list1, list3);
  ASSERT_EQ(0, result2.size());
}

// _____________________________________________________________________________
TEST(PostingList, intersect) {
  PostingList list1;
  list1.readFromFile("example1.txt");
  PostingList list2;
  list2.readFromFile("example2.txt");
  PostingList list3;
  list3.readFromFile("example3.txt");

  PostingList result1 = PostingList::intersectBaseline(list1, list2);
  ASSERT_EQ(2, result1.size());
  ASSERT_EQ(2, result1.getId(0));
  ASSERT_EQ(6, result1.getId(1));
  ASSERT_EQ(9, result1.getScore(0));
  ASSERT_EQ(5, result1.getScore(1));

  PostingList result2 = PostingList::intersectBaseline(list1, list3);
  ASSERT_EQ(0, result2.size());
}

// _____________________________________________________________________________
TEST(PostingList, intersectGallopSearch) {
  PostingList list1;
  list1.readFromFile("example1.txt");
  PostingList list2;
  list2.readFromFile("example2.txt");
  PostingList list3;
  list3.readFromFile("example3.txt");

  PostingList result1 = PostingList::intersectBaseline(list1, list2);
  ASSERT_EQ(2, result1.size());
  ASSERT_EQ(2, result1.getId(0));
  ASSERT_EQ(6, result1.getId(1));
  ASSERT_EQ(9, result1.getScore(0));
  ASSERT_EQ(5, result1.getScore(1));

  PostingList result2 = PostingList::intersectBaseline(list1, list3);
  ASSERT_EQ(0, result2.size());
}

// _____________________________________________________________________________
TEST(PostingList, intersectSearchRemainder) {
  PostingList list1;
  list1.readFromFile("example1.txt");
  PostingList list2;
  list2.readFromFile("example2.txt");
  PostingList list3;
  list3.readFromFile("example3.txt");

  PostingList result1 = PostingList::intersectBaseline(list1, list2);
  ASSERT_EQ(2, result1.size());
  ASSERT_EQ(2, result1.getId(0));
  ASSERT_EQ(6, result1.getId(1));
  ASSERT_EQ(9, result1.getScore(0));
  ASSERT_EQ(5, result1.getScore(1));

  PostingList result2 = PostingList::intersectBaseline(list1, list3);
  ASSERT_EQ(0, result2.size());
}

// _____________________________________________________________________________
TEST(PostingList, expSearch) {
  PostingList l2;
  l2.readFromFile("example2.txt");
  ASSERT_EQ(0, PostingList::expSearch(1, l2, 0));
  ASSERT_EQ(0, PostingList::expSearch(1, l2, 1));
  ASSERT_EQ(0, PostingList::expSearch(1, l2, 2));
  ASSERT_EQ(1, PostingList::expSearch(2, l2, 0));
  ASSERT_EQ(0, PostingList::expSearch(2, l2, 1));
  ASSERT_EQ(0, PostingList::expSearch(2, l2, 2));
  ASSERT_EQ(2, PostingList::expSearch(4, l2, 0));
  ASSERT_EQ(1, PostingList::expSearch(4, l2, 1));
  ASSERT_EQ(0, PostingList::expSearch(4, l2, 2));
  ASSERT_EQ(4, PostingList::expSearch(6, l2, 0));
  ASSERT_EQ(2, PostingList::expSearch(6, l2, 1));
  ASSERT_EQ(1, PostingList::expSearch(6, l2, 2));
  ASSERT_EQ(0, PostingList::expSearch(6, l2, 3));
  ASSERT_EQ(4, PostingList::expSearch(10, l2, 0));
}

// _____________________________________________________________________________
TEST(PostingList, binSearch) {
  PostingList l2;
  l2.readFromFile("example2.txt");
  ASSERT_EQ(0, PostingList::binSearch(1, l2, 0, l2.size()));
  ASSERT_EQ(1, PostingList::binSearch(2, l2, 0, l2.size()));
  ASSERT_EQ(2, PostingList::binSearch(4, l2, 0, l2.size()));
  ASSERT_EQ(3, PostingList::binSearch(6, l2, 0, l2.size()));
  ASSERT_EQ(0, PostingList::binSearch(1, l2, 0, 0));
  ASSERT_EQ(0, PostingList::binSearch(2, l2, 0, 0));
  ASSERT_EQ(2, PostingList::binSearch(4, l2, 0, 2));
  ASSERT_EQ(2, PostingList::binSearch(6, l2, 0, 2));
}
