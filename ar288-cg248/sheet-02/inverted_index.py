"""
Copyright 2017, University of Freiburg,
Chair of Algorithms and Data Structures.
Author: Hannah Bast <bast@cs.uni-freiburg.de>
Edited by: Abderrahmen Rakez && Chandran Goodchild
"""

import re
import sys

class InvertedIndex:
    # A simple inverted index that uses BM25 scores.
    def __init__(self):
        """ Create an empty inverted index. """

        self.inverted_lists = {}
        self.records = []  # The records, each in form (title, description).

    # Construct the inverted index from the given file. The expected format of
    # the file is one document per line, in the format <title>TAB<description>.
    # Each entry in the inverted list associated to a word should contain a
    # document id and a BM25 score. Compute the BM25 scores as follows:
    #
    # (1) In a first pass, compute the inverted lists with tf scores (that
    #     is the number of occurrences of the word within the <title> and the
    #     <description> of a document). Further, compute the document length
    #     (DL) for each document (that is the number of words in the <title> and
    #     the <description> of a document). Afterwards, compute the average
    #     document length (AVDL).
    # (2) In a second pass, iterate each inverted list and replace the tf scores
    #     by BM25 scores, defined as:
    #     BM25 = tf * (k + 1) / (k * (1 - b + b * DL / AVDL) + tf) * log2(N/df),
    #     where N is the total number of documents and df is the number of
    #     documents that contains the word.
    #
    # On reading the file, use UTF-8 as the standard encoding. To split the
    # texts into words, use the method introduced in the lecture. Make sure that
    # you ignore empty words.
    def read_from_file(self, file_name, b, k):
        """
        >>> ii = InvertedIndex()
        >>> ii.read_from_file("example.txt", b=0, k=float("inf"))
        >>> sorted(ii.inverted_lists.items())
        [('animated', [(1, 0.415), (2, 0.415), (4, 0.415)]),
        ('animation', [(3, 2.0)]), ('film', [(2, 1.0), (4, 1.0)]),
        ('movie', [(1, 0.0), (2, 0.0), (3, 0.0), (4, 0.0)]), ('non',
        [(2, 2.0)]), ('short', [(3, 1.0), (4, 2.0)])]

        """



        with open(file_name, "r") as file:
            record_id = 1
            for line in file:
                tf = 1
#                print("new line, tf = 1")
                line = line.strip()
                # Store the record as a tuple (title, description).
                self.records.append(tuple(line.split("\t")))

                print(line)

                for word in re.split("[^A-Za-z]+", line):
                    word = word.lower().strip()

                    # Ignore the word if it is empty.
                    if len(word) == 0:
                        continue

                    if word not in self.inverted_lists:
                        # The word is seen for first time, create a new list.
                        tf = 1
#                        print("set record_id, tf = 1", (record_id, tf), "word, ", word)
                        self.inverted_lists[word] = [(record_id, tf)]
                    elif self.inverted_lists[word][-1] == (record_id, tf):
                        tf = tf + 1
#                        print("tf = tf + 1", "word, ", word)
                        self.inverted_lists[word] = [(record_id, tf)]
                        tf = 1
                    elif self.inverted_lists[word][-1] != (record_id, tf):
                        # Make sure that the list contains the id at most once.
#                        print("append, ", (record_id, tf), "word, ", word)
                        self.inverted_lists[word].append((record_id, tf))
                record_id += 1








        # Compute the union of the two given inverted lists in linear time (linear
        # in the total number of entries in the two lists), where the entries in
        # the inverted lists are postings of form (doc_id, bm25_score) and are
        # expected to be sorted by doc_id, in ascending order.
    def merge(self, list1, list2):
        """
        >>> ii = InvertedIndex()
        >>> list1 = [(1, 2.1), (5, 3.2)]
        >>> list2 = [(1, 1.7), (2, 1.3), (5, 3.3)]
        >>> ii.merge(list1, list2)
        [(1, 3.8), (2, 1.3), (5, 6.5)]
        """

        merge_list = []

        # iterate over the two list, and check for equality
        list1_iter = 0
        list2_iter = 0
        while list1_iter != len(list1) and list2_iter != list2:
            if list1[list1_iter][0] < list2[list2_iter][0]:
                # Just append without adding up the scores if the item
                # only occurs in list 1
                tmp0 = list1[list1_iter][0]
                tmp1 = list1[list1_iter][1]
                merge_list.append((tmp0, tmp1))
                list1_iter += 1

            elif list1[list1_iter][0] > list2[list2_iter][0]:
                # Just append without adding up the scores if the item
                # only occurs in list 2
                tmp0 = list2[list2_iter][0]
                tmp1 = list2[list2_iter][1]
                merge_list.append((tmp0, tmp1))
                list2_iter += 1

            else:
                # Add up the bm25_score when equal
                tmp0 = list1[list1_iter][0]
                tmp1 = list1[list1_iter][1] + list2[list2_iter][1]
                merge_list.append((tmp0, tmp1))
                list1_iter += 1
                list2_iter += 1
        return merge_list



  # Process the given keyword query as follows: Fetch the inverted list for
  # each of the keywords in the query and compute the union of all lists. Sort
  # the resulting list by BM25 scores in descending order.
  #
  # If you want to implement some ranking refinements, make these refinements
  # optional (their use should be controllable via a 'use_refinements' flag).
    def process_query(self, keywords_query, use_refinements):
        """
        InvertedIndex ii
        ii.inverted_lists = {
        "foo": [(1, 0.2), (3, 0.6)],
        "bar": [(1, 0.4), (2, 0.7), (3, 0.5)]
        "baz": [(2, 0.1)]
        }
        ii.process_query("foo bar", use_refinements=False)

        [(3, 1.1), (2, 0.7), (1, 0.6)]
        """

        merge_list = []
        # first check if the keywords_query not an empty list
        if len(keywords_query) == 0:
            return None

        # initialize the merge_list and check each intersection with
        # each keyword
        if keywords_query[0] in self.inverted_lists:
            merge_list = self.inverted_lists[keywords_query[0]]

        i = 1
        while i < len(keywords_query):
            if keywords_query[i] in self.inverted_lists:
                merge_list = self.merge(merge_list,
                                                self.inverted_lists
                                                [keywords_query[i]])
            else:
                merge_list = []

            i += 1

        return merge_list


if __name__ == "__main__":
    if len(sys.argv) != 4:
        print("Usage: python3 inverted_index.py <file name> b k")
        sys.exit(1)

    file_name = sys.argv[1]
    b = sys.argv[2]
    k = sys.argv[3]

    ii = InvertedIndex()
    ii.read_from_file(file_name, b, k)

    keyword = ""
    keywords_query = []

    while keyword != "end":
        #        keyword = input("give keyword querys\n")
        keyword = raw_input("give keyword querys\n")
        if keyword != "end":
            keywords_query.append(keyword)

    keywords_query = ii.process_query(keywords_query, False)


# # Class for evaluating the InvertedIndex class against a benchmark.
# class EvaluateInvertedIndex:
#   # Read a benchmark from the given file. The expected format of the file is 
#   # one query per line, with the ids of all documents relevant for that query,
#   # like: <query>TAB<id1>WHITESPACE<id2>WHITESPACE<id3> ...
#   #
#   # TEST CASE:
#   #    read_benchmark("example-benchmark.txt")
#   # RESULT:
#   #    { 'animated film': {1, 3, 4}, 'short film': {3, 4} }
#   #Map<String, Set<int>> read_benchmark(String file_name)
#   def read_benchmark(self, file_name):
#       """
#       >>> read_benchmark("example-benchmark.txt")
#         [('animated film': {1, 3, 4}), ('short film': {3, 4})]
#       """

#   # Evaluate the given inverted index against the given benchmark as follows.
#   # Process each query in the benchmark with the given inverted index and
#   # compare the result list with the groundtruth in the benchmark. For each
#   # query, compute the measure P@3, P@R and AP as explained in the lecture.
#   # Aggregate the values to the three mean measures MP@3, MP@R and MAP and
#   # return them.
#   #
#   # Implement a parameter 'use_refinements' that controls the use of ranking
#   # refinements on calling the method process_query of your inverted index.
#   #
#   # TEST_CASE:
#   #   InvertedIndex ii
#   #   ii.read_from_file("example.txt", b=0.75, k=1.75)
#   #   benchmark = read_benchmark("example-benchmark.txt")
#   #   evaluate(ii, benchmark, use_refinements=False)
#   # RESULT:
#   #   (0.667, 0.833, 0.694)
#   # Triple<float, float, float> evaluate(InvertedIndex ii, Map<String,
#   # Set<int>> benchmark, boolean use_refinements)
#   def evaluate(self, benchmark, use_refinements):
#       """
#       >>> ii = InvertedIndex()
#       >>> ii.read_from_file("example.txt", b=0.75, k=1.75)
#       >>> benchmark = read_benchmark("example-benchmark.txt")
#       >>> evaluate(ii, benchmark, use_refinements=False)
#       [(0.667, 0.833, 0.694)]
#       """

#   # Compute the measure P@k for the given list of result ids as it was
#   # returned by the inverted index for a single query, and the given set of
#   # relevant document ids.
#   #
#   # Note that the relevant document ids are 1-based (as they reflect the line
#   # number in the dataset file).
#   #
#   # TEST CASE:
#   #   precision_at_k([5, 3, 6, 1, 2], {1, 2, 5, 6, 7, 8}, k=2)
#   # RESULT:
#   #   0.5
#   #
#   # TEST CASE:
#   #   precision_at_k([5, 3, 6, 1, 2], {1, 2, 5, 6, 7, 8}, k=4)
#   # RESULT:
#   #   0.75
#   # float precision_at_k(Array<int> result_ids, Set<int> relevant_ids, int k)
#   def precision_at_k(self, result_ids, relevant_ids, k):
#       """
#       >>> precision_at_k([5, 3, 6, 1, 2], {1, 2, 5, 6, 7, 8}, k=2)
#       [0.5]

#       >>> precision_at_k([5, 3, 6, 1, 2], {1, 2, 5, 6, 7, 8}, k=4)
#       [0.75]
#       """

#   # Compute the average precision (AP) for the given list of result ids as it
#   # was returned by the inverted index for a single query, and the given set
#   # of relevant document ids.
#   #
#   # Note that the relevant document ids are 1-based (as they reflect the line
#   # number in the dataset file).
#   #
#   # TEST CASE:
#   #   average_precision([7, 17, 9, 42, 5], {5, 7, 12, 42})
#   # RESULT:
#   #   0.525
#   # float average_precision(Array<int> result_ids, Set<int> relevant_ids)
#   def average_precision(self, result_ids, relevant_ids):
#       """
#       >>> average_precision([7, 17, 9, 42, 5], {5, 7, 12, 42})
#       [0.525]
#       """
      
# # if __name__ == "__main__":
#     # if len(sys.argv) != 2:
#     #     print("Usage: python3 inverted_index.py <file name>")
#     #     sys.exit(1)

#     # file_name = sys.argv[1]

#     # ii = InvertedIndex()
#     # ii.read_from_file(file_name)

#     # keyword = ""
#     # keywords_query = []

#     # while keyword != "end":
#     #     keyword = input("give keyword querys\n")
#     #     if keyword != "end":
#     #         keywords_query.append(keyword)

#     # keywords_query = ii.process_query(keywords_query)
