// Edited by ar288 and cg248

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.DataOutputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.Path;
import java.util.Scanner;
import java.util.Collections;
import java.util.List;

/**
 * Basic server code that returns the contents of a requested file.
 */
public class SearchServerMain {
  /**
   * The main method.
   */
  public static void main(String[] args) throws IOException {
    // Parse the command line arguments.
    if (args.length < 2) {
      System.out.println("Usage: java -jar SearchServerMain.jar <port> "
          + "<entity-file> [--with-synonyms]");
      System.exit(1);
    }

    String fle = args[1];

    boolean withSynonyms = args.length > 2 && args[2].equals("--with-synonyms");

    System.out.print("Building index from '" + fle + "' ... ");
    System.out.flush();

    // Build a 3-gram index from the given file.
    long start = System.currentTimeMillis();
    QGramIndex index = new QGramIndex(3, withSynonyms);
    index.buildFromFile(fle);
    long end = System.currentTimeMillis();

    System.out.println("done in " + (end - start) + "ms.");


    // Create socket.
    int port = Integer.parseInt(args[0]);
    ServerSocket server = new ServerSocket(port);

    // Server loop.
    while (true) {
      // Wait for client.
      System.out.print("Waiting for query on port " + port + " ... ");
      System.out.flush();

      Socket client = server.accept();
      System.out.println("client connected from " + client.getInetAddress());

      // Client connected; set read timeout.
      client.setSoTimeout(5000);


      // Read first line from the request (not enough for ES6).
      BufferedReader input = new BufferedReader(new InputStreamReader(
          client.getInputStream()));
      StringBuilder requestBuilder = new StringBuilder();
      DataOutputStream output = new DataOutputStream(client.getOutputStream());

      try {
        while (true) {
          // Handle sending of the request data in several packets (some of
          // which maybe empty).
          String line = input.readLine();
          if (line.equals("")) {
            break;
          }
          requestBuilder.append(line);
        }

        String fileName = requestBuilder.toString();

        int i = 5;

        String query = "";
        // When done this way, our server can handle get requests as well as
        // simple requests via telnet...
        if (!(fileName.length() < 1) && fileName.length() >= 5 && fileName
            .substring(0, 5).equals("GET /")) {
          // It's a browser!
          fileName = fileName.substring(5, fileName.indexOf(" HTTP/1.1"));

          int search = fileName.indexOf("?query=") + 7;
          if (search != 6) {
            query = fileName.substring(search);
            fileName = fileName.substring(0, fileName.indexOf("?query="));
          }

        } else if (fileName.equals("")) {
          // Connecting and not sending anything type 1
          fileName = "annoyedResponse.html";
          System.out.println("empty request");
        } else if (fileName.length() < 5) {
          fileName = "areYouABrowser.html";
        }

        // Read contents of file.
        String contentString = "File not found\n";
        byte[] contentBytes = contentString.getBytes("UTF-8");
        Path file = Paths.get(fileName);
        String contentType = "";
        String statusString = "HTTP/1.1 200 OK";

        if (Files.isRegularFile(file) && Files.isReadable(file)) {
          contentBytes = Files.readAllBytes(file);
          contentString = new String(contentBytes, "UTF-8");

          if (fileName.substring(fileName.indexOf("."), fileName.length())
              .equals(".txt")) {
            contentType = "text/plain";
          } else if (fileName.substring(fileName.indexOf("."), fileName
              .length()).equals(".html")) {
            contentType = "text/html";
          } else if (fileName.substring(fileName.indexOf("."), fileName
              .length()).equals(".css")) {
            contentType = "text/plain";
          } else if (fileName.substring(fileName.indexOf("."), fileName
              .length()).equals(".jpg")) {
            contentType = "image/jpg";
          } else if (file.getNameCount() != 1 || !fileName.matches("[a-zA-Z"
              + ".]+")) {
            // Tested with "t2st.html" or "/bin/<file>.class"
            statusString = "HTTP/1.1 403 Forbidden";
          }
        } else {
          // File not found => wikidata!
          statusString = "HTTP/1.1 404 Not found";
        }

        if (!query.equals("")) {
          // Normalize the query.
          final String queryPlain = query;
          query = QGramIndex.normalize(query);
          int delta = query.length() / 4;

          start = System.currentTimeMillis();
          ObjectIntPair<List<Entity>> result = index.findMatches(query, delta);
          end = System.currentTimeMillis();

          List<Entity> matches = result.first;

          String line = "\nFound " + Integer.toString(matches.size())
              + " matches." + "\n <br />" + "\n <br />";
          System.out.printf(line);
          String insert = line;

          int numResults = Math.min(5, matches.size());
          if (numResults > 0) {
            line = "The top-" + Integer.toString(numResults) + " results "
                + "are:\n";
            System.out.printf(line);
            insert = insert + "\n <br />" + line;

            for (int n = 0; n < numResults; n++) {
              Entity e = matches.get(n);

              line = "\n(" + e.name + ")    Rank: " + Integer.toString(n + 1)
                  + "    Score:" + e.score;
              System.out.printf("\033[1m" + line + "\033[0m");
              //insert = insert + "\n <br />" + line;

              if (e.matchedSynonym != null) {
                line = "(Matched Synonym: '" + e.matchedSynonym + "')\n";
                System.out.printf(line);
                insert = insert + "\n <br />" + line;
              } else {
                System.out.println();
                insert = insert + "\n <br />" + line;
              }

              line = "Description:   " + e.desc + "\n";
              System.out.printf(line);

              if (e.wikipediaUrl != null) {
                System.out.printf("Wikipedia-URL: %s\n", e.wikipediaUrl);
                String url = e.wikipediaUrl;
                insert = insert + "\n <br />"
                    + "<a href=\"" + url + "\">" + line + "</a>" + "\n <br />";
              }
              if (e.wikidataId != null) {
                System.out.printf("Wikidata-URL:  "
                    + "http://www.wikidata.org/wiki/%s\n", e.wikidataId);
              }
              System.out.printf("PED:           %d\n", e.ped);
              System.out.printf("Score:         %d\n", e.score);
            }
          }

          System.out.println();
          System.out.printf("Time needed to find matches: %dms: ",
              (end - start));
          System.out.printf(" #PED computations: %d.\n", result.second);

          String[] contentStringParts = contentString.split("%%%%%");
          contentString = contentStringParts[0] + insert
              + contentStringParts[1];

          contentString = contentString.replace("%QUERY%",
              queryPlain.replace("+", " "));
          contentBytes = contentString.getBytes("UTF-8");
          contentType = "text/html";
        } else {
          contentString = contentString.replace("%%%%%", "");
          contentString = contentString.replace("%QUERY%", "");
          contentBytes = contentString.getBytes("UTF-8");
          contentType = "text/html";
        }

        // Send the response.
        StringBuilder headerBuilder = new StringBuilder();

        headerBuilder.append(statusString + "\r\n");
        headerBuilder.append("Content-Length: " + contentBytes.length
            + "\r\n");
        headerBuilder.append("Content-Type: " + contentType + "\r\n");
        headerBuilder.append("\r\n");
        output.write(headerBuilder.toString().getBytes("UTF-8"));
        output.write(contentBytes);

      } catch (java.net.SocketTimeoutException e) {
        // connecting and not sending anything type 2
        // connecting and then aborting type 2
        String out = "Read TIMEOUT\n";
        output.write(out.toString().getBytes("UTF-8"));
        System.out.println(out);

      } catch (NullPointerException e) {
        // connecting and then aborting type 1
        String out = "Hey client where are you?!?\n";
        output.write(out.toString().getBytes("UTF-8"));
        System.out.println(out);
      }

      output.close();
      input.close();
      client.close();
    }
  }
}
