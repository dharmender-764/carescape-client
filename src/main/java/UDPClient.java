import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

class UDPClient
{
   public static void main(String args[]) throws Exception
   {
      DatagramSocket clientSocket = new DatagramSocket();
      InetAddress IPAddress = InetAddress.getByName("localhost");
      byte[] sendData = new byte[1024];
      String sentence = "test data";
      sendData = sentence.getBytes();
      DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, IPAddress, 9001);
      clientSocket.send(sendPacket);
//      byte[] receiveData = new byte[1024];
//      DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
//      clientSocket.receive(receivePacket);
//      String modifiedSentence = new String(receivePacket.getData());
//      System.out.println("FROM SERVER:" + modifiedSentence);
      clientSocket.close();
   }
}