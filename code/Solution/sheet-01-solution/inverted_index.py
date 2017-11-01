"""
Copyright 2017, University of Freiburg
Hannah Bast <bast@cs.uni-freiburg.de>
Claudius Korzen <korzen@cs.uni-freiburg.de>
"""

import re
import sys


class InvertedIndex:
    """
    A simple inverted index as explained in lecture 1.
    """

    def __init__(self):
        """
        Creates an empty inverted index.
        """
        self.inverted_lists = {}  # The inverted lists of record ids.
        self.records = []  # The records, each in form (title, description).

    def read_from_file(self, file_name):
        """
        Constructs the inverted index from given file in linear time (linear in
        the number of words in the file). The expected format of the file is
        one record per line, in the format <title>TAB<description>.

        Makes sure that each inverted list contains a particular record id at
        most once, even if the respective word occurs multiple time in the same
        record.

        >>> ii = InvertedIndex()
        >>> ii.read_from_file("example.txt")
        >>> sorted(ii.inverted_lists.items())
        [('a', [0, 1]), ('doc', [0, 1, 2]), ('film', [1]), ('movie', [0, 2])]
        """
        with open(file_name, "r") as file:
            record_id = 0
            for line in file:
                line = line.strip()

                # Store the record as a tuple (title, description).
                self.records.append(tuple(line.split("\t")))

                for word in re.split("[^A-Za-z]+", line):
                    word = word.lower().strip()

                    # Ignore the word if it is empty.
                    if len(word) == 0:
                        continue

                    if word not in self.inverted_lists:
                        # The word is seen for first time, create a new list.
                        self.inverted_lists[word] = [record_id]
                    elif self.inverted_lists[word][-1] != record_id:
                        # Make sure that the list contains the id at most once.
                        self.inverted_lists[word].append(record_id)
                record_id += 1

    def intersect(self, list1, list2):
        """
        Computes the intersection of the two given inverted lists in linear
        time (linear in the total number of elements in the two lists).

        >>> ii = InvertedIndex()
        >>> ii.intersect([1, 5, 7], [2, 4])
        []
        >>> ii.intersect([1, 2, 5, 7], [1, 3, 5, 6, 7, 9])
        [1, 5, 7]
        """
        i = 0  # The pointer in the first list.
        j = 0  # The pointer int the second list.
        result = []

        while i < len(list1) and j < len(list2):
            if list1[i] == list2[j]:
                result.append(list1[i])
                i += 1
                j += 1
            elif list1[i] < list2[j]:
                i += 1
            else:
                j += 1

        return result

    def process_query(self, keywords):
        """
        Processes the given keyword query as follows: Fetches the inverted list
        for each of the keywords in the given query and computes the
        intersection of all inverted lists (which is empty, if there is a
        keyword in the query which has no inverted list in the index).

        >>> ii = InvertedIndex()
        >>> ii.read_from_file("example.txt")
        >>> ii.process_query([])
        []
        >>> ii.process_query(["doc", "movie"])
        [0, 2]
        >>> ii.process_query(["doc", "movie", "comedy"])
        []
        """
        if not keywords:
            return []

        # Fetch the inverted lists for each of the given keywords.
        lists = []
        for keyword in keywords:
            if keyword in self.inverted_lists:
                lists.append(self.inverted_lists[keyword])
            else:
                # We can abort, because the intersection is empty
                # (there is no inverted list for the word).
                return []

        # Compute the intersection of all inverted lists.
        if len(lists) == 0:
            return []

        intersected = lists[0]
        for i in range(1, len(lists)):
            intersected = self.intersect(intersected, lists[i])

        return intersected

    def render_output(self, record_ids, keywords, k=3):
        """
        Renders the output for the top-k of the given record_ids. Fetches the
        the titles and descriptions of the related records and highlights
        the occurences of the given keywords in the output, using ANSI escape
        codes.
        """

        # Compile a pattern to identify the given keywords in a string.
        p = re.compile('\\b(' + '|'.join(keywords) + ')\\b', re.IGNORECASE)

        # Output at most k matching records.
        for i in range(min(len(record_ids), k)):
            title, desc = self.records[record_ids[i]]

            # Highlight the keywords in the title in bold and red.
            title = re.sub(p, "\033[0m\033[1;31m\\1\033[0m\033[1m", title)

            # Print the rest of the title in bold.
            title = "\033[1m%s\033[0m" % title

            # Highlight the keywords in the description in red.
            desc = re.sub(p, "\033[31m\\1\033[0m", desc)

            print("\n%s\n%s" % (title, desc))

        print("\n# total hits: %s." % len(record_ids))


if __name__ == "__main__":
    # Parse the command line arguments.
    if len(sys.argv) != 2:
        print("Usage: python3 inverted_index.py <file>")
        sys.exit()

    file_name = sys.argv[1]
    # Create a new inverted index from the given file.
    print("Reading from file '%s'." % file_name)
    ii = InvertedIndex()
    ii.read_from_file(file_name)

    while True:
        # Ask the user for a keyword query.
        query = input("\nYour keyword query: ")

        # Split the query into keywords.
        keywords = [x.lower().strip() for x in re.split("[^A-Za-z]+", query)]

        # Process the keywords.
        record_ids = ii.process_query(keywords)

        # Render the output (with ANSI codes to highlight the keywords).
        ii.render_output(record_ids, keywords)
