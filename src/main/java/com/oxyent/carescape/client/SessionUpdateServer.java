package com.oxyent.carescape.client;

import java.net.ServerSocket;
import java.net.Socket;

import javax.mail.internet.MimeMessage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class SessionUpdateServer {

	@Autowired
	private MessageHandler messageLoader;

	public void startServer() {
		try {
			ServerSocket serverSocket = new ServerSocket(9000);
			System.out.println("SessionUpdateServer-> Server started  at: 9000");

			while (true) {
				System.out.println("SessionUpdateServer-> Waiting for a  connection...");
				final Socket socket = serverSocket.accept();
				System.out.println("SessionUpdateServer-> Received a  connection from  " + socket);
				Runnable runnable = new Runnable() {

					@Override
					public void run() {
						try {
							MimeMessage message = messageLoader.readMessageFromSocket(socket);
							String xml = messageLoader.extractXmlBodyFromCotentObject(message.getContent());
							System.out.println("SessionUpdateServer-> data from server: " + xml);

							String genericMessage = messageLoader.loadMessageFromFile("generic-response.xml");
							messageLoader.writeMessageOnSocket(genericMessage, socket);

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