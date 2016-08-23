package com.oxyent.carescape.client;

import java.net.ServerSocket;
import java.net.Socket;

import javax.mail.internet.MimeMessage;

import org.springframework.beans.factory.annotation.Autowired;

public class WaveformStreamServer {

	@Autowired
	private MessageHandler messageHandler;

	public void startServer() {
		try {
			ServerSocket serverSocket = new ServerSocket(9002);
			System.out.println("WaveformStreamServer-> Server started  at: 9002");

			while (true) {
				System.out.println("WaveformStreamServer-> Waiting for a  connection...");
				final Socket socket = serverSocket.accept();
				System.out.println("WaveformStreamServer-> Received a  connection from  " + socket);
				Runnable runnable = new Runnable() {

					@Override
					public void run() {
						try {
							MimeMessage message = messageHandler.readMessageFromSocket(socket);
							String xml = messageHandler.extractXmlBodyFromCotentObject(message.getContent());
							System.out.println("WaveformStreamServer-> data from server: " + xml);

							//String genericMessage = messageLoader.loadMessageFromFile("generic-response.xml");
							//messageLoader.writeMessageOnSocket(genericMessage, socket);

							socket.close();
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				};
				new Thread(runnable).start();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}