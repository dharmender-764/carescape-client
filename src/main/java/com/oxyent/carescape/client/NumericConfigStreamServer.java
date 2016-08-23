package com.oxyent.carescape.client;

import java.net.ServerSocket;
import java.net.Socket;

import javax.mail.internet.MimeMessage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Component
public class NumericConfigStreamServer {

	@Autowired
	private MessageHandler messageHandler;

	@Async
	public void startServer() {
		try {
			ServerSocket serverSocket = new ServerSocket(9001);
			System.out.println("NumericConfigStreamServer-> Server started  at: 9001");

			while (true) {
				System.out.println("NumericConfigStreamServer-> Waiting for a  connection...");
				final Socket socket = serverSocket.accept();
				System.out.println("NumericConfigStreamServer-> Received a  connection from  " + socket);
				Runnable runnable = new Runnable() {

					@Override
					public void run() {
						try {
							MimeMessage message = messageHandler.readMessageFromSocket(socket);
							String xml = messageHandler.extractXmlBodyFromCotentObject(message.getContent());
							System.out.println("NumericConfigStreamServer-> data from server: " + xml);

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
				new Thread(runnable).start();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}