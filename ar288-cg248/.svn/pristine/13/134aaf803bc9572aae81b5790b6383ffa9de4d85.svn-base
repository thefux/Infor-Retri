"""
Copyright 2017, University of Freiburg,
Chair of Algorithms and Data Structures.
Author: Hannah Bast <bast@cs.uni-freiburg.de>
Author: Abderrahmen Rakez & Chandran Goodchild
"""

import re
import sys


class InvertedIndex:
    """ A very simple inverted index. """

    def __init__(self):
        """ Create an empty inverted index. """

        self.inverted_lists = {}

    def read_from_file(self, file_name):
        """ Construct index from given file.

        >>> ii = InvertedIndex()
        >>> ii.read_from_file("example.txt")
        >>> sorted(ii.inverted_lists.items())
        [('doc', [1, 2, 3]), ('first', [1, 3]), ('sec', [2]), ('third', [3])]
        """

        record_id = 0
        with open(file_name) as file:
            for line in file:
                record_id += 1
                for word in re.split("[^a-zA-Z]+", line):
                    if len(word) > 0:
                        word = word.lower()
                        if word not in self.inverted_lists:
                            self.inverted_lists[word] = []
                        # Note that this way, the record ids will be
                        # automatically in sorted order.

                        # to make sure that each inverted list
                        # contains a particular record id, we should make
                        # sure that it occurs just once in the list
                        if record_id not in self.inverted_lists[word]:
                            self.inverted_lists[word].append(record_id)

    def intersect(self, list1, list2):
        """
        >>> ii = InvertedIndex()
        >>> list1 = [1 ,2 ,4 ,5 ,6]
        >>> list2 = [1 ,3 ,5 ,6 ,9]
        >>> ii.intersect(list1, list2)
        [1, 5, 6]
        """

        intersect_list = []

        # iterate over the two list, and check for equality
        # (example slide 18)
        list1_iter = 0
        list2_iter = 0
        while list1_iter != len(list1) and list2_iter != list2:
            if list1[list1_iter] < list2[list2_iter]:
                list1_iter += 1
            elif list1[list1_iter] > list2[list2_iter]:
                list2_iter += 1
            # the case where the two lists intersect
            else:
                intersect_list.append(list1[list1_iter])
                list1_iter += 1
                list2_iter += 1
        return intersect_list

        # (not linear)
        #  for i in list1:
        #      if i in list2:
        #          intersect_list.append(i)

        #  return intersect_list
    def process_query(self, keywords_query):
        """
        for each given keywords, fetch the inverted lists and compute
        their intersection

        >>> ii = InvertedIndex()
        >>> ii.read_from_file("example.txt")
        >>> ii.process_query(["first", "doc"])
        [1, 3]
        """

        intersect_list = []
        # first check if the keywords_query not an empty list
        if len(keywords_query) == 0:
            return None

        # initialize the intersect_list and check each intersection with
        # each keyword
        if keywords_query[0] in self.inverted_lists:
            intersect_list = self.inverted_lists[keywords_query[0]]

        i = 1
        while i < len(keywords_query):
            if keywords_query[i] in self.inverted_lists:
                intersect_list = self.intersect(intersect_list,
                                                self.inverted_lists
                                                [keywords_query[i]])
            else:
                intersect_list = []

            i += 1

        return intersect_list


if __name__ == "__main__":
    if len(sys.argv) != 2:
        print("Usage: python3 inverted_index.py <file name>")
        sys.exit(1)

    file_name = sys.argv[1]

    ii = InvertedIndex()
    ii.read_from_file(file_name)

    keyword = ""
    keywords_query = []

    while keyword != "end":
        keyword = input("give keyword querys\n")
        if keyword != "end":
            keywords_query.append(keyword)

    keywords_query = ii.process_query(keywords_query)
    # print(keywords_query)
    # for i in range(0, len(keywords_query)):
    #     print(keywords_query[i])
    # for word, inverted_list in ii.inverted_lists.items():
    #     print("%s\t%d" % (word, len(inverted_list)))
    # print(ii.inverted_lists.items())
    # print(ii.intersect(ii.inverted_lists["first"], ii.inverted_lists["doc"]))
    # print(ii.process_query(["doc", "first"]))
