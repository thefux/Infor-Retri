"""
Copyright 2017, University of Freiburg,
Chair of Algorithms and Data Structures.
Author: Hannah Bast <bast@cs.uni-freiburg.de>
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
        [('doc', [1, 2, 3]), ('first', [1]), ('second', [2]), ('third', [3])]
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
                        # automaticall in sorted order.
                        self.inverted_lists[word].append(record_id)


if __name__ == "__main__":
    if len(sys.argv) != 2:
        print("Usage: python3 inverted_index.py <file name>")
        sys.exit(1)
    file_name = sys.argv[1]

    ii = InvertedIndex()
    ii.read_from_file(file_name)
    for word, inverted_list in ii.inverted_lists.items():
        print("%s\t%d" % (word, len(inverted_list)))
