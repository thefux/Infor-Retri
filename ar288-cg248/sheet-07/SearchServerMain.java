
// Copyright 2017, University of Freiburg,
// Chair of Algorithms and Data Structures.
// Authors: Claudius Korzen <korzen@cs.uni-freiburg.de>,
//          Hannah Bast <bast@cs.uni-freiburg.de>

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * A simple server that handles fuzzy prefix search requests and file requests.
 */
public class SearchServerMain {
  /**
   * The q-gram index to use in this server.
   */
  protected final QGramIndex index;

  /**
   * The template for the HTML representation of an entity.
   */
  protected String entityHtmlPattern;

  /**
   * The base directory of the files to serve.
   */
  protected static final String SERVE_DIR = "../resources/";

  /**
   * The default HTTP response header.
   */
  protected static final String HTTP_OK_HEADER = "HTTP/1.1 200 OK";

  /**
   * The default content type to return in HTTP responses.
   */
  protected static final String DEFAULT_CONTENT_TYPE = "text/plain";

  /**
   * The default line delimiter.
   */
  protected static final String DEFAULT_LINE_DELIMITER = "\r\n";

  /**
   * The available content types, per file extensions.
   */
  protected static final Map<String, String> CONTENT_TYPES;
  
  static {
    CONTENT_TYPES = new HashMap<>();
    CONTENT_TYPES.put(".html", "text/html");
    CONTENT_TYPES.put(".css", "text/css");
    CONTENT_TYPES.put(".js", "application/javascript");
    CONTENT_TYPES.put(".jpg", "image/jpeg");
    CONTENT_TYPES.put(".png", "image/png");
  }

  /**
   * The available HTTP error responses by status codes.
   */
  protected static final Map<Integer, String> HTTP_ERROR_RESPONSES;
  
  static {
    HTTP_ERROR_RESPONSES = new HashMap<>();
    HTTP_ERROR_RESPONSES.put(403,
        "HTTP/1.1 403 Forbidden\r\n"
            + "Content-type: text/html\r\n"
            + "Content-length: 10\r\n\r\n"
            + "Forbidden.");

    HTTP_ERROR_RESPONSES.put(404,
        "HTTP/1.1 404 Not found\r\n"
            + "Content-type: text/html\r\n"
            + "Content-length: 10\r\n\r\n"
            + "Not found.");

    HTTP_ERROR_RESPONSES.put(405,
        "HTTP/1.1 405 Method Not Allowed\r\n"
            + "Content-type: text/html\r\n"
            + "Content-length: 12\r\n\r\n"
            + "Not allowed.");
  }

  /**
   * The regex pattern of the first line from a valid HTTP request header.
   */
  protected static final Pattern HTTP_REQUEST_HEADER_PATTERN =
      Pattern.compile("([A-Z]+) /([a-zA-z0-9.]*)(\\?\\S*)? HTTP/1.1");

  /**
   * The default encoding.
   */
  protected static final Charset DEFAULT_ENCODING = StandardCharsets.UTF_8;

  /**
   * The default file to serve if no fileName is given in the HTTP request.
   */
  protected static final String DEFAULT_FILE_NAME = "search.html";

  /**
   * The URL to the default image of an entity, if no image url is given.
   */
  protected static final String DEFAULT_ENTITY_IMAGE_URL = "noimage.png";

  /**
   * The path to the HTML pattern file for an entity.
   */
  protected static final String ENTITY_TEMPLATE_FILE = "entity.pattern.html";
  
  /**
   * The number of search results to show per default.
   */
  protected static final int NUM_SEARCH_RESULTS_TO_SHOW = 5;
  
  // ==========================================================================

  /**
   * Creates a new search server.
   *
   * @param index
   *        The q-gram index to use.
   */
  public SearchServerMain(QGramIndex index) {
    this.index = index;
    // Read the HTML template for the entities of the q-gram index.
    try {
      Path templateFile = Paths.get(SERVE_DIR, ENTITY_TEMPLATE_FILE)
          .toAbsolutePath();
      byte[] template = Files.readAllBytes(templateFile);
      this.entityHtmlPattern = new String(template, DEFAULT_ENCODING);
    } catch (Exception e) {
      this.entityHtmlPattern = "";
      System.out.println("WARN: Couldn't read the HTML pattern for an entity.");
    }
  }

  /**
   * Starts this search server.
   *
   * @param port
   *        The port of the server to start.
   *
   * @throws IOException
   *         if something went wrong while running the server.
   */
  public void start(int port) throws IOException {
    // Create the server socket.
    System.out.printf("Starting the server on port %d ... ", port);
    System.out.flush();
    ServerSocket server = new ServerSocket(port);
    System.out.println("Done!");

    // The main server loop.
    while (true) {
      // Wait for the client.
      System.out.printf("Waiting on port %d ... ", server.getLocalPort());
      System.out.flush();

      try (Socket client = server.accept()) {
        System.out.println("client connected from " + client.getInetAddress());

        // Set read timeout for the client.
        client.setSoTimeout(1000);

        try (BufferedReader input = new BufferedReader(
            new InputStreamReader(client.getInputStream()));
            DataOutputStream output = new DataOutputStream(
                client.getOutputStream())) {

          try {
            // Read only the first line of the request from the client.
            String requestLine = input.readLine();

            // Handle the request line and create the response.
            byte[] response = handleRequest(requestLine);

            // Send the response to the client.
            output.write(response);
          } catch (Exception e) {
            System.err.println("WARN: " + e.getMessage());
          }
        }
      }
    }
  }

  // ==========================================================================

  /**
   * Creates the HTTP response for the given HTTP request.
   * 
   * @param request
   *        The HTTP request to process.
   * 
   * @return The HTTP response.
   * @throws IOException
   *         If something went wrong while creating the response.
   */
  protected byte[] handleRequest(String request) throws IOException {
    Matcher headerMatcher = HTTP_REQUEST_HEADER_PATTERN.matcher(request);
    if (!headerMatcher.find()) {
      // The request contains no valid HTTP header.
      System.out.println("RESPONSE: 405 (No valid HTTP request).");
      return HTTP_ERROR_RESPONSES.get(405).getBytes(DEFAULT_ENCODING);
    }

    // Obtain the HTTP method, the request fileName and the parameters.
    String httpMethod = headerMatcher.group(1);
    String fileName = headerMatcher.group(2);
    String parameters = headerMatcher.group(3);

    System.out.printf("REQUEST: HTTP_METHOD: %s, FILE_NAME: %s, "
        + "PARAMETERS: %s\n", httpMethod, fileName, parameters);

    if (!httpMethod.equals("GET")) {
      // Only GET requests are supported.
      System.out.println("RESPONSE: 405 (No GET request).");
      return HTTP_ERROR_RESPONSES.get(405).getBytes(DEFAULT_ENCODING);
    }

    if (fileName == null || fileName.isEmpty()) {
      // No fileName is given, take the default one.
      System.out.printf("Setting fileName to default '%s'.\n",
          DEFAULT_FILE_NAME);
      fileName = DEFAULT_FILE_NAME;
    }

    if (!fileName.matches("[a-zA-Z.]+")) {
      // The fileName contains invalid characters.
      System.out.println("RESPONSE: 403 (fileName contains invalid chars).");
      return HTTP_ERROR_RESPONSES.get(403).getBytes(DEFAULT_ENCODING);
    }

    Path file = Paths.get(SERVE_DIR, fileName);
    if (!Files.isRegularFile(file) || !Files.isReadable(file)) {
      // The requested file does not exist or is not readable.
      System.out.println("RESPONSE: 404 (File does not exist).");
      return HTTP_ERROR_RESPONSES.get(404).getBytes(DEFAULT_ENCODING);
    }

    // Read the requested file.
    byte[] body = Files.readAllBytes(file);

    if (fileName.equals("search.html")) {
      // Handle a fuzzy prefix search request.
      System.out.println("Handling fuzzy prefix search request.");
      body = handleFuzzyPrefixSearchRequest(body, parameters);
    }

    // Create the HTTP response.
    System.out.println("RESPONSE: 200 (OK).");
    String contentType = getContentType(file);
    String headerString = HTTP_OK_HEADER + DEFAULT_LINE_DELIMITER
            + "Content-type: " + contentType + DEFAULT_LINE_DELIMITER
            + "Content-length: " + body.length + DEFAULT_LINE_DELIMITER
            + DEFAULT_LINE_DELIMITER;
    byte[] header = headerString.getBytes(DEFAULT_ENCODING);

    // Merge the header and the body.
    byte[] response = new byte[header.length + body.length];
    System.arraycopy(header, 0, response, 0, header.length);
    System.arraycopy(body, 0, response, header.length, body.length);

    return response;
  }

  /**
   * Handles a fuzzy prefix search request.
   * 
   * @param fileBytes
   *        The bytes of the search.html (where to insert the results).
   * @param parameters
   *        The parameters string from the HTTP request.
   * @return The search.html containing the results of the fuzzy prefix search.
   * @throws IOException
   *         If something went wrong on handling the fuzzy prefix search
   *         request.
   */
  protected byte[] handleFuzzyPrefixSearchRequest(byte[] fileBytes,
      String parameters) throws IOException {
    // Check if there is a query given in the parameters.
    String query = "";
    if (parameters != null) {
      int pos = parameters.indexOf("?q=");
      if (pos != -1) {
        query = parameters.substring(pos + 3);
      }
    }

    // Decode all url-encoded whitespaces.
    query = query.replaceAll("\\+", " ");

    int numResultsTotal = 0;
    int numResultsToShow = 0;

    String searchHtml = new String(fileBytes, DEFAULT_ENCODING);

    // Pass the query to the q-gram index and create the related HTML fragment.
    StringBuilder htmlBuilder = new StringBuilder();
    if (!query.isEmpty()) {
      ObjectIntPair<List<Entity>> result = this.index.findMatches(query);
      List<Entity> matches = result.first;
      numResultsTotal = matches.size();
      numResultsToShow = Math.min(matches.size(), NUM_SEARCH_RESULTS_TO_SHOW);
      for (int i = 0; i < numResultsToShow; i++) {
        String html = translateToHtml(matches.get(i));
        htmlBuilder.append(html + DEFAULT_LINE_DELIMITER);
      }
    }

    // Plug in the query into the search.html.
    searchHtml = searchHtml.replaceAll("%QUERY%", query);

    // Plug in the result header. 
    String resultHeader = "";
    if (numResultsToShow == 0) {
      resultHeader = "Nothing to show.";
    } else {
      resultHeader = String.format("Found %d results for query '%s'.",
          numResultsTotal, query);
    }
    searchHtml = searchHtml.replaceAll("%RESULT_HEADER%", resultHeader);

    // Plug in the HTML fragment containing the search results.
    searchHtml = searchHtml.replaceAll("%RESULT%", htmlBuilder.toString());

    return searchHtml.getBytes(DEFAULT_ENCODING);
  }

  // ==========================================================================
  // Some utility methods.

  /**
   * Translates the given entity to HTML.
   * 
   * @param entity
   *        The entity to translate.
   * @return The HTML representation of the entity.
   */
  protected String translateToHtml(Entity entity) {
    // Plug in the name of the entity into the HTML pattern.
    String name = entity.name;
    String html = this.entityHtmlPattern.replaceAll("%ENTITY_NAME%", name);

    // Plug in the matched synonym (if any).
    String synonym = "";
    if (entity.matchedSynonym != null) {
      synonym = "synonym: " + entity.matchedSynonym;
    }
    html = html.replaceAll("%ENTITY_SYNONYM%", synonym);

    // Plug in the description of the entity.
    String description = entity.desc;
    html = html.replaceAll("%ENTITY_DESCRIPTION%", description);

    // Plug in the image url of the entity.
    String imageUrl = entity.imageUrl;
    if (imageUrl == null || imageUrl.isEmpty()) {
      // No image url is given, take the default one.
      imageUrl = DEFAULT_ENTITY_IMAGE_URL;
    }
    html = html.replaceAll("%ENTITY_IMAGE%", imageUrl);

    // Plug in the URL to the related Wikidata page (if any).
    String wikidataUrl = "";
    if (entity.wikidataId != null) {
      wikidataUrl = "https://www.wikidata.org/wiki/" + entity.wikidataId;
    }
    html = html.replaceAll("%ENTITY_WIKIDATA_URL%", wikidataUrl);

    // Plug in the URL to the related Wikipedia page (if any).
    String wikipediaUrl = "";
    if (entity.wikipediaUrl != null) {
      wikipediaUrl = entity.wikipediaUrl;
    }
    html = html.replaceAll("%ENTITY_WIKIPEDIA_URL%", wikipediaUrl);

    return html;
  }

  /**
   * Returns the content type of the given file.
   * 
   * @param file
   *        The file to process.
   * @return The content type for the given file.
   */
  protected static String getContentType(Path file) {
    String contentType = DEFAULT_CONTENT_TYPE;

    // Obtain the file extension of the given file.
    String fileExtension = "";
    String fileName = file.getFileName().toString();
    int pos = fileName.lastIndexOf('.');
    if (pos != -1) {
      fileExtension = fileName.substring(pos);
    }

    // Look up the content type for the file extension.
    if (CONTENT_TYPES.containsKey(fileExtension)) {
      contentType = CONTENT_TYPES.get(fileExtension);
    }

    return contentType;
  }

  // ==========================================================================

  /**
   * The main method.
   * 
   * @param args
   *        The command line arguments.
   * @throws IOException
   *         if something went wrong on running the server.
   */
  public static void main(String[] args) throws IOException {
    // Parse the command line arguments.
    if (args.length < 2) {
      System.err.print("Usage: ");
      System.err.println("SearchServerMain <file> <port> [--with-synonyms]");
      System.exit(1);
    }
    final String fileName = args[0];
    final int port = Integer.parseInt(args[1]);
    boolean withSynonyms = args.length > 2 && args[2].equals("--with-synonyms");

    // Build the q-gram index.
    System.out.printf("Building the q-gram index from '%s' ... ", fileName);
    System.out.flush();
    QGramIndex index = new QGramIndex(3, withSynonyms);
    index.buildFromFile(fileName);
    System.out.println("Done!");

    // Start the server loop.
    new SearchServerMain(index).start(port);
  }
}
