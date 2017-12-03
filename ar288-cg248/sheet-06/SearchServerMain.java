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
      try {

        fileName = input.readLine();
        int i = 5;

        while (fileName.charAt(i) != " ".charAt(0)) {
          i++;
        }

        fileName = fileName.substring(5, i);

        System.out.println(fileName);

        // Read contents of file.
        String contentString = "File not found\n";
        byte[] contentBytes = contentString.getBytes("UTF-8");
        Path file = Paths.get(fileName);
        if (Files.isRegularFile(file) && Files.isReadable(file)) {
          contentBytes = Files.readAllBytes(file);
        }

        // Send the response.
        DataOutputStream output = new DataOutputStream(client.getOutputStream());
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
