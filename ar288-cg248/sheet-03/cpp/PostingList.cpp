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
  size_t numPosts;
  std::stringstream s(curLine);
  s >> numPosts;

  // Reserve enough space for the postings.
  reserve(numPosts + 1);

  // Read the file line by line, each in the format <id>WHITESPACE<score>.
  size_t i = 0;
  while (std::getline(fstream, curLine) && i < numPosts) {
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
  addPosting(std::numeric_limits<std::size_t>::max(), 0);
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
// Implement a new method for intersecting two posting lists that
// uses at least three non-trivial ideas presented in the
// lecture. The goal is to beat the baseline implementation for all
// scenarios of the exercise sheet. Note that you can also implement
// several algorithms and switch between them depending on the sizes
// of the input lists (or depending on any information that you find
// to be useful). Note: Each implemented method must pass the test
// case provided for the baseline implementation.
//
// Modifications:
// 1. Sentinels
// 2. Binary search
// 3. Binary search only in the remainder
// 4. 

PostingList PostingList::intersect(const PostingList& l1,
                                   const PostingList& l2) {
  // if(l1.size() < l2.size()) {
  //   size_t k = l1.size();
  //   size_t n = l2.size();
  // } else {
  //   size_t k = l2.size();
  //   size_t n = l1.size();
  // }

  // Zipper: O(n)
  bool zip = 0;

  // Binary search: O(k * log(n))
  bool bin = 1;

  PostingList result;
  result.reserve(std::min(l1.size(), l2.size()));

  //////////////////////////////////////////////////////
  // Begin zip search
  //////////////////////////////////////////////////////

  if (zip == 1) {
    size_t i1 = 0;
    size_t i2 = 0;

    while (true) {
      while (l1.getId(i1) < l2.getId(i2)) {
        i1++;
      }

      while (l2.getId(i2) < l1.getId(i1)) {
        i2++;
      }

      if (l1.getId(i1) == l2.getId(i2)) {
        if (l1.getId(i1) == std::numeric_limits<std::size_t>::max()) {
          break;
        }

        result.addPosting(l1.getId(i1), l1.getScore(i1) + l2.getScore(i2));

        i1++;
        i2++;

        if (i1 == l1.size() || i2 == l2.size()) {
          break;
        }
      }

      if (i1 == l1.size()) {
        break;
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

  //////////////////////////////////////////////////////
  // End zip search
  //////////////////////////////////////////////////////

  //////////////////////////////////////////////////////
  // Begin binary search
  //////////////////////////////////////////////////////

  if (bin == 1) {
    // Use case statement, because I had issues with std::swap().
    if(l1.size() < l2.size()) {
      size_t i1 = 0;
      size_t lb = 0;
      size_t ub = l2.size();
      size_t mb;

      while (l1.getId(i1) < std::numeric_limits<std::size_t>::max()) {
	mb = (ub + lb) / 2;
        while ((mb != lb + 1) && (l1.getId(i1) != l2.getId(mb))) {
          while (l1.getId(i1) < l2.getId(mb)) {
            mb = (lb + mb) / 2;
          }

          while (l1.getId(i1) > l2.getId(mb)) {
            mb = (ub + mb) / 2;
          }
        }
        i1++;

	if (l1.getId(i1) == l2.getId(mb)) {
	  result.addPosting(l1.getId(i1), l1.getScore(i1) + l2.getScore(mb));
	  // Only search the remainder of the list in the next
	  // iteration.
	  lb = mb;
	}
      }
    
      return result;
    } else {
      size_t i2 = 0;
      size_t lb = 0;
      size_t ub = l1.size();
      size_t mb = (ub + lb) / 2;

      while (l2.getId(i2) < std::numeric_limits<std::size_t>::max()) {
	mb = (ub + lb) / 2;
        while ((mb != lb + 1) && (l2.getId(i2) != l1.getId(mb))) {
          while (l2.getId(i2) < l1.getId(mb)) {
            mb = (lb + mb) / 2;
          }

          while (l2.getId(i2) > l1.getId(mb)) {
            mb = (ub + mb) / 2;
          }
        }
        i2++;

	if (l1.getId(i2) == l2.getId(mb)) {
	  result.addPosting(l2.getId(i2), l2.getScore(i2) + l1.getScore(mb));
	  // Only search the remainder of the list in the next
	  // iteration.
	  lb = mb;
	}
      }
    
      return result;
    }
  }

  //////////////////////////////////////////////////////
  // End binary search
  //////////////////////////////////////////////////////

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
