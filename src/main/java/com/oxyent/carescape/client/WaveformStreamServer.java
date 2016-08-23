package com.oxyent.carescape.client;

import java.net.DatagramPacket;
import java.net.DatagramSocket;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Component
public class WaveformStreamServer {

	@Autowired
	private MessageHandler messageHandler;

	@Async
	public void startServer() {
		try {
			DatagramSocket serverSocket = new DatagramSocket(9002);
            byte[] receiveData = new byte[2048];
			System.out.println("WaveformStreamServer-> Server started  at: 9002");

			while (true) {
				System.out.println("WaveformStreamServer-> Waiting for a  connection...");
				final DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
                serverSocket.receive(receivePacket);
                String sentence = new String( receivePacket.getData());
                System.out.println("WaveformStreamServer-> data RECEIVED: " + sentence);
                
				/*Runnable runnable = new Runnable() {

					@Override
					public void run() {
						try {
							MimeMessage message = messageHandler.readMessageFromSocket(socket);
							String xml = messageHandler.extractXmlBodyFromCotentObject(message.getContent());
							System.out.println("WaveformStreamServer-> data from server: " + xml);

							// String genericMessage =
							// messageLoader.loadMessageFromFile("generic-response.xml");
							// messageLoader.writeMessageOnSocket(genericMessage,
							// socket);

							socket.close();
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				};
				new Thread(runnable).start();*/
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}