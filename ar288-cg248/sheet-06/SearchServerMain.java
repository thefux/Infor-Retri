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

/**
 * Basic server code that returns the contents of a requested file.
 */
public class SearchServerMain {
  /**
   * The main method.
   */
  public static void main(String[] args) throws IOException {
    // Parse the command line arguments.
    if (args.length < 1) {
      System.out.println("java -jar SearchServerMain <port>");
      System.exit(1);
    }
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
      String fileName;
      String line = "";
//      StringBuilder in = new StringBuilder();
      try {
        // Redo this as in the lecture!
        fileName = input.readLine();

//        line = input.readLine();
//        System.out.println(line);
//        in.append(input.readLine());
//        while (!line.equals("") && line != null) {
//          in.append(input.readLine());
//          line = input.readLine();
//        }
//
//        fileName = in.toString();
//        System.out.println(fileName);

        int i = 5;

        if (fileName.equals("")) {
          fileName = "angryResponse.html";
          System.out.println("empty string");
        }
        // When done this way, our server can handle get requests as well as
        // simple requests via telnet...
        if (fileName.substring(0, 5).equals("GET /")) {
          // It's a browser!
          fileName = fileName.substring(5, fileName.indexOf(" HTTP/1.1"));
        }

        System.out.println(fileName);

        // Read contents of file.
        String contentString = "File not found\n";
        byte[] contentBytes = contentString.getBytes("UTF-8");
        Path file = Paths.get(fileName);
        String contentType = "";
        String statusString = "";

        if (Files.isRegularFile(file) && Files.isReadable(file)) {
          contentBytes = Files.readAllBytes(file);

          if (fileName.substring(fileName.indexOf("."), fileName.length())
              .equals(".txt")) {
            contentType = "text/plain";
            statusString = "HTTP/1.1 200 OK";
          }

          if (fileName.substring(fileName.indexOf("."), fileName.length())
              .equals(".html")) {
            contentType = "text/html";
            statusString = "HTTP/1.1 200 OK";
          }

          if (fileName.substring(fileName.indexOf("."), fileName.length())
              .equals(".css")) {
            contentType = "text/plain";
            statusString = "HTTP/1.1 200 OK";
          }

          if (file.getNameCount() != 1) {
            statusString = "HTTP/1.1 403 Not found";
          }
        } else {
          //if (!Files.isReadable(file)) {
          statusString = "HTTP/1.1 404 Not found";
          //}
        }

          // Send the response.
          DataOutputStream output = new DataOutputStream(client.getOutputStream());
          StringBuilder headerBuilder = new StringBuilder();

          headerBuilder.append(statusString + "\r\n");
          headerBuilder.append("Content-Length: " + contentBytes.length +
              "\r\n");
          headerBuilder.append("Content-Type: " + contentType + "\r\n");
          headerBuilder.append("\r\n");
          //output.write(contentBytes);
          output.write(headerBuilder.toString().getBytes("UTF-8"));
          output.write(contentBytes);

        output.close();
        input.close();
        client.close();

      } catch (java.net.SocketTimeoutException e) {
        System.out.println("Read TIMEOUT");
        input.close();
        client.close();
      }
    }
  }
}
