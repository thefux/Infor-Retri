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

    // Create socket.
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
          // sending the request data in several packets (some of which maybe
          // empty).
          String line = input.readLine();
          if (line.equals("")) {
            break;
          }
          requestBuilder.append(line);
        }


        String fileName = requestBuilder.toString();
        System.out.println(fileName);

        int i = 5;

        // When done this way, our server can handle get requests as well as
        // simple requests via telnet...
        if (!(fileName.length() < 1) && fileName.length() >= 5 && fileName
            .substring(0, 5).equals("GET /")) {
          // It's a browser!
          fileName = fileName.substring(5, fileName.indexOf(" HTTP/1.1"));
        } else if (fileName.equals("")) {
          // connecting and not sending anything type 1
          fileName = "annoyedResponse.html";
          System.out.println("empty request");
        } else if (fileName.length() < 5){
          fileName = "areYouABrowser.html";
        }
//        else {
//          throw new IllegalArgumentException();
//        }

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
        StringBuilder headerBuilder = new StringBuilder();

        headerBuilder.append(statusString + "\r\n");
        headerBuilder.append("Content-Length: " + contentBytes.length +
            "\r\n");
        headerBuilder.append("Content-Type: " + contentType + "\r\n");
        headerBuilder.append("\r\n");
        //output.write(contentBytes);
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
//      catch (IllegalArgumentException e) {
//        String out = "Illegal argument! Try writing something longer and " +
//            "adding a file type. The file types \".txt\", \".html\" and \"" +
//            ".css\" are supported.\n";
//        output.write(out.toString().getBytes("UTF-8"));
//        System.out.println(out);
//      }

      output.close();
      input.close();
      client.close();
    }
  }
}
