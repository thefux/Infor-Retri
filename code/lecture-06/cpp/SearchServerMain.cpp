// Copyright 2017, University of Freiburg,
// Chair of Algorithms and Data Structures.
// Authors: Patrick Brosi <brosi@cs.uni-freiburg.de>,
//          Claudius Korzen <korzen@cs.uni-freiburg.de>.

#include <boost/asio.hpp>
#include <iostream>
#include <fstream>
#include <string>


// Basic server code that returns the contents of a requested file.
int main(int argc, char** argv) {
  // Parse the command line arguments.
  if (argc < 2) {
    std::cerr << "Usage: " << argv[0] << " <port>\n";
    exit(1);
  }
  uint16_t port = atoi(argv[1]);

  boost::asio::io_service ioService;
  boost::asio::ip::tcp::endpoint server(boost::asio::ip::tcp::v4(), port);
  boost::asio::ip::tcp::acceptor acceptor(ioService, server);

  // Server loop.
  while (true) {
    // Wait for client.
    std::cout << "Waiting for query on port " << port << " ... " << std::flush;
    boost::asio::ip::tcp::socket client(ioService);
    acceptor.accept(client);
    std::cout << "client connected from "
        << client.remote_endpoint().address().to_string() << std::endl;

    // Read first line from the request (not enough for ES6).
    boost::asio::streambuf requestBuffer;
    boost::asio::read_until(client, requestBuffer, "\r\n");
    std::istream istream(&requestBuffer);
    std::string fileName;
    std::getline(istream, fileName);

    // IMPORTANT NOTE: read_until() seems to actually read more than up to the
    // delimiter '\r\n'. So check if the fileName still contains the delimiter
    // and remove it.
    size_t pos1 = fileName.find("\r\n");
    if (pos1 != std::string::npos) {
       fileName = fileName.replace(pos1, 2, "");
    }
    size_t pos2 = fileName.find("\r");
    if (pos2 != std::string::npos) {
       fileName = fileName.replace(pos2, 1, "");
    }

    // Read contents of file.
    std::stringstream contentStream;
    std::ifstream file(fileName);
    if (file) {
      contentStream << file.rdbuf();
      file.close();
    }

    // Send the response.
    boost::asio::write(client, boost::asio::buffer(contentStream.str()),
        boost::asio::transfer_all());
  }
}
