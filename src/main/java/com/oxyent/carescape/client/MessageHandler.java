package com.oxyent.carescape.client;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.Socket;

import javax.mail.BodyPart;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Session;
import javax.mail.internet.MimeMessage;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class MessageHandler {
	
	private static final Logger logger = LoggerFactory.getLogger(MessageHandler.class);

	@Value("${mime.message.header}")
	private String mimeMessageHeader;

	public String loadMessageFromFile(String fileName) throws IOException {
		File file = new File(fileName);
		String message = FileUtils.readFileToString(file, "UTF-8");
		int contentLength = message.getBytes().length;
		String fullMessage = mimeMessageHeader.replace("$content-length$", String.valueOf(contentLength)) + message;
		return fullMessage;
	}
	
	public String extractXmlBodyFromCotentObject(Object content) throws IOException, MessagingException {
		System.out.println("content type = " + content);
		if (content instanceof InputStream) {
			InputStream inputStream = (InputStream) content;
			BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
			StringBuilder out = new StringBuilder();
			String line;
			while ((line = reader.readLine()) != null) {
				out.append(line);
			}
			String emailContent = out.toString();
			inputStream.close();
			reader.close();
			return emailContent;
		} else if (content instanceof String) {
			return (String) content;
		} else if (content instanceof Multipart) {
			Multipart multipart = (Multipart) content;
			BodyPart bodyPart = multipart.getBodyPart(0);
			return extractXmlBodyFromCotentObject(bodyPart.getContent());
		}
		return null;
	}
	
	public String writeAndReadMessage(String messageToSend, Socket socket) throws IOException, MessagingException {
		writeMessageOnSocket(messageToSend, socket);
		MimeMessage message = readMessageFromSocket(socket);
		String xmlBody = extractXmlBodyFromCotentObject(message.getContent());
		return xmlBody;
	}
	
	public void writeMessageOnSocket(String message, Socket socket) throws IOException {
		logger.info("Writing message to out stream: " + message);
		java.io.PrintWriter out = new java.io.PrintWriter(socket.getOutputStream(), true);
		out.println(message);
	}

	public MimeMessage readMessageFromSocket(Socket socket) throws IOException, MessagingException {
		try {
			logger.info("Reading message from in stream...");
			MimeMessage msg = new MimeMessage((Session)null, socket.getInputStream());
//			BufferedReader br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
//			char[] cbuf = new char[4096];
//			br.read(cbuf);
//
//			System.out.println("readMessageFromSocket: " + String.valueOf(cbuf));
//			MimeMessage msg = new MimeMessage((Session) null, new ByteArrayInputStream(String.valueOf(cbuf).getBytes()));
			return msg;
		} catch (IOException e) {
			logger.error("IOException while reading message from in stream ", e);
			throw e;
		}
	}
	
}
