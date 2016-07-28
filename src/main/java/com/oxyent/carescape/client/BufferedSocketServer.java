package com.oxyent.carescape.client;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

public class BufferedSocketServer {

  public static void main(String args[]) throws Exception {
    int cTosPortNumber = 2007;
    String str;

    ServerSocket servSocket = new ServerSocket(cTosPortNumber);
    System.out.println("Waiting for a connection on " + cTosPortNumber);

    Socket fromClientSocket = servSocket.accept();
    PrintWriter pw = new PrintWriter(fromClientSocket.getOutputStream(), true);

    BufferedReader br = new BufferedReader(new InputStreamReader(fromClientSocket.getInputStream()));

    while ((str = br.readLine()) != null) {
      System.out.println("The message: " + str);

      if (str.equals("bye")) {
        pw.println("bye");
        break;
      } else {
        str = "Server returns " + str;
        pw.println(str);
      }
    }
    pw.close();
    br.close();

    fromClientSocket.close();
  }

}