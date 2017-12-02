// Copyright 2017, University of Freiburg,
// Chair of Algorithms and Data Structures.
// Authors: Patrick Brosi <brosi@cs.uni-freiburg.de>,
//          Claudius Korzen <korzen@cs.uni-freiburg.de>.

#include "./PostingList.h"
#include <algorithm>
#include <fstream>
#include <vector>
#include <string>
#include <sstream>
#include <iostream>

// _____________________________________________________________________________
void PostingList::readFromFile(const std::string& fileName) {
  std::ifstream fstream(fileName.c_str());
  std::string curLine;

  // Read the number of postings in the file from the first line.
  std::getline(fstream, curLine);
  size_t n;
  std::stringstream s(curLine);
  s >> n;

  // Reserve enough space for the postings (+1 for the sentinel).
  reserve(n + 1);

  // Read the file line by line, each in the format <id>WHITESPACE<score>.
  size_t i = 0;
  while (std::getline(fstream, curLine) && i < n) {
    i++;
    std::stringstream s(curLine);
    size_t docId;
    size_t score;

    // Split the line into <id> and <score>.
    s >> docId;
    s >> score;

    // Add the posting (id, score) to this list.
    addPosting(docId, score);
  }

  // Add a sentinel to the end of the list.
  addPosting(PostingList::SENTINEL, PostingList::SENTINEL);
  numPostings--;
}

// _____________________________________________________________________________
PostingList PostingList::intersectBaseline(const PostingList& l1,
    const PostingList& l2) {
  PostingList result;
  result.reserve(std::min(l1.size(), l2.size()));

  size_t i1 = 0;
  size_t i2 = 0;

  while (i1 < l1.size() && i2 < l2.size()) {
    while (i1 < l1.size() && l1.getId(i1) < l2.getId(i2)) {
      i1++;
    }

    if (i1 == l1.size()) {
      break;
    }

    while (i2 < l2.size() && l2.getId(i2) < l1.getId(i1)) {
      i2++;
    }

    if (i2 == l2.size()) {
      break;
    }

    if (l1.getId(i1) == l2.getId(i2)) {
      result.addPosting(l1.getId(i1), l1.getScore(i1) + l2.getScore(i2));
      i1++;
      i2++;
    }
  }
  return result;
}

// _____________________________________________________________________________
PostingList PostingList::intersect(const PostingList& l1,
    const PostingList& l2) {
  // Make sure that the first list is the smaller list.
  if (l1.size() > l2.size()) {
    return intersect(l2, l1);
  }

  // If the difference in the sizes is small, use the basic zipper algorithm.
  if (static_cast<float>(l1.size()) / static_cast<float>(l2.size()) < 0.05f) {
    return intersectGallopSearch(l1, l2);
  }

  // Intersect the lists using the galloping search algorithm.
  return intersectBaseline(l1, l2);
}

// _____________________________________________________________________________
PostingList PostingList::intersectGallopSearch(const PostingList& l1,
    const PostingList& l2) {
  // Create the result list with enough space (the max size of the result is
  // equal to the size of the smaller (= first) list).
  PostingList result;
  result.reserve(l1.size());

  size_t i1 = 0;
  size_t i2 = 0;

  while (i1 < l1.size() && i2 < l2.size()) {
    while (l1.getId(i1) == l2.getId(i2) && l1.getId(i1) != SENTINEL) {
      result.addPosting(l1.getId(i1), l1.getScore(i1) + l2.getScore(i2));
      i1++;
      i2++;
    }

    while (l1.getId(i1) > l2.getId(i2)) {
      // Find an upper bound (the first index of the element in l2 that is
      // larger than the current element in l1) using exponential search.
      size_t upper = expSearch(l1.getId(i1), l2, i2);

      // Find the current id of l1 in the interval l2[lower:upper].
      i2 = binSearch(l1.getId(i1), l2, i2 + (upper >> 1), i2 + upper);
    }

    while (l1.getId(i1) < l2.getId(i2)) {
      i1++;
    }
  }

  return result;
}

// _____________________________________________________________________________
size_t PostingList::expSearch(size_t value, const PostingList& list,
    size_t start) {
  if (start >= list.size()) {
    return 0;
  }

  if (list.getId(start) >= value) {
    return 0;
  }

  size_t i = 1;
  while (start + i < list.size() && list.getId(start + i) < value) {
    i = i << 1;
  }
  return i;
}

// _____________________________________________________________________________
size_t PostingList::binSearch(size_t value, const PostingList& list,
    size_t start, size_t end) {
  // Make sure that 'start' and 'end' doesn't exceed the list boundaries.
  if (start < 0) start = 0;
  if (end > list.size()) end = list.size();

  while (start < end) {
    size_t half = start + ((end - start) / 2);

    if (list.getId(half) < value) {
      // Proceed in the right half.
      start = half + 1;
      continue;
    }

    if (list.getId(half) > value) {
      // Proceed in the left half.
      end = half;
      continue;
    }
    return half;
  }
  // Return list.size() if the list doesn't contain the value in the interval.
  return end;
}

// _____________________________________________________________________________
PostingList PostingList::intersectSearchRemainder(const PostingList& l1,
    const PostingList& l2) {
  // Create the result list with enough space (the max size of the result is
  // equal to the size of the smaller (= first) list).
  PostingList result;
  result.reserve(l1.size());

  size_t i1 = 0;
  size_t i2 = 0;

  while (i1 < l1.size() && i2 < l2.size()) {
    while (l1.getId(i1) == l2.getId(i2)
        && l1.getId(i1) != PostingList::SENTINEL) {
      result.addPosting(l1.getId(i1), l1.getScore(i1) + l2.getScore(i2));
      i1++;
      i2++;
    }

    while (l1.getId(i1) > l2.getId(i2)) {
      // Find the current id of l1 in the interval l2[i2:].
      i2 = binSearch(l1.getId(i1), l2, i2, l2.size());
    }

    while (l1.getId(i1) < l2.getId(i2)) {
      i1++;
    }
  }

  return result;
}

// _____________________________________________________________________________
void PostingList::reserve(size_t n) {
  ids.reserve(n);
  scores.reserve(n);
  capacity = n;
  numPostings = 0;
}

// _____________________________________________________________________________
void PostingList::addPosting(size_t id, size_t score) {
  ids.push_back(id);
  scores.push_back(score);
  numPostings++;
}

// _____________________________________________________________________________
size_t PostingList::getId(size_t i) const {
  return ids[i];
}

// _____________________________________________________________________________
size_t PostingList::getScore(size_t i) const {
  return scores[i];
}

// _____________________________________________________________________________
size_t PostingList::size() const {
  return numPostings;
}
