// Copyright 2017, University of Freiburg,
// Chair of Algorithms and Data Structures.
// Authors: Patrick Brosi <brosi@cs.uni-freiburg.de>,
//          Claudius Korzen <korzen@cs.uni-freiburg.de>.

#ifndef POSTINGLIST_H_
#define POSTINGLIST_H_

#include <stddef.h>
#include <vector>
#include <string>
#include <limits>

/**
 * A list of postings of form (docId, score).
 */
class PostingList {
 public:
  PostingList(const std::vector<size_t>& ids, const std::vector<size_t>& scores)
  : ids(ids), scores(scores) {}

  PostingList() : ids(), scores() {}

  /**
   * Reads a posting list from the given file.
   */
  void readFromFile(const std::string& fileName);

  /**
   * Intersects the two given posting lists using the basic "zipper" algorithm.
   */
  static PostingList intersectBaseline(const PostingList& list1,
      const PostingList& list2);

  /**
   * Intersects the two given posting lists using an improved algorithm that
   * uses at least three non-trivial ideas presented in the lecture.
   */
  static PostingList intersect(const PostingList& list1,
      const PostingList& list2);

  /**
   * Intersects the two given posting lists using galloping search. Expects that
   * the first given list is the smaller list.
   */
  static PostingList intersectGallopSearch(const PostingList& list1,
      const PostingList& list2);

  /**
   * Intersects the two given posting lists using binary search in the remainder
   * of the longer list. Expects that the first given list is the smaller list.
   */
  static PostingList intersectSearchRemainder(const PostingList& list1,
      const PostingList& list2);

  /**
   * Does an exponential search on the given posting list in order to find the
   * first index of the element that is larger than the given value, with
   * exponential steps, starting at the given index.
   */
  static size_t expSearch(size_t value, const PostingList& list, size_t start);

  /**
   * Does a binary search in order to find the index of the given value in the
   * interval list[start:end]. Returns the given end index if the list doesn't
   * contain the given value in the given interval.
   */
  static size_t binSearch(size_t value, const PostingList& list, size_t start,
        size_t end);

  // ==========================================================================

  /**
   * Reserves space for n postings in this list.
   */
  void reserve(size_t n);

  /**
   * Adds the given posting to this list.
   */
  void addPosting(size_t id, size_t score);

  /**
   * Returns the id of the i-th posting.
   */
  size_t getId(size_t i) const;

  /**
   * Returns the score of the i-th posting.
   */
  size_t getScore(size_t i) const;

  /**
   * Returns the number of postings in this list.
   */
  size_t size() const;

  // ==========================================================================

  /**
   * The docIds of the postings in this list.
   */
  std::vector<size_t> ids;

  /**
   * The scores of the postings in this list.
   */
  std::vector<size_t> scores;

  /**
   * The capacity of this list.
   */
  size_t capacity;

  /**
   * The number of postings in this list.
   */
  size_t numPostings;

  /**
   * The value of the sentinel.
   */
  static const size_t SENTINEL = std::numeric_limits<int>::max();
};

#endif  // POSTINGLIST_H_
