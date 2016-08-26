package com.oxyent.carescape.client;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.Socket;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

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

	private DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");

	public String loadMessageFromFile(String fileName) throws IOException {
		File file = new File(fileName);
		String message = FileUtils.readFileToString(file, "UTF-8");
		return updateContentLentgh(message);
	}
	
	public String loadMessageFromFileWithoutContentLength(String fileName) throws IOException {
		File file = new File(fileName);
		String message = FileUtils.readFileToString(file, "UTF-8");
		return message;
	}
	
	public String updateContentLentgh(String message) {
		message = message.replace("$creationDateTime$", df.format(new Date()));
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
			BufferedReader br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			
			String boundry = null;
			StringBuilder sb = new StringBuilder();
			String line;
			while ((line = br.readLine()) != null) {
				//System.out.println(line);
				sb.append(line);
				sb.append(System.lineSeparator());
				//if (line.startsWith("Content-Type: multipart/mixed;boundary=")) {
					//boundry = line.substring(line.indexOf("=\"") + 2, line.length() - 1);
					//System.out.println("boundry = " + boundry);
				//} else 
				if (line.startsWith("--") && line.endsWith("--")) {
					break;
				}
			}
			System.out.println("readMessageFromSocket: " + sb.toString());
			MimeMessage msg = new MimeMessage((Session) null, new ByteArrayInputStream(sb.toString().getBytes()));
			return msg;
		} catch (IOException e) {
			logger.error("IOException while reading message from in stream ", e);
			throw e;
		}
	}
	
	public String readPacketsFromSocket(Socket socket) throws IOException, MessagingException {
		try {
			logger.info("Reading message from in stream...");
			InputStream inputStream = socket.getInputStream();
			int nRead;
			byte[] data = new byte[2048];
			StringBuilder sb = new StringBuilder();
			
			while ((nRead = inputStream.read(data, 0, data.length)) != -1) {
				System.out.println("nRead + " + nRead);
				System.out.println("data received + " + new String(data));
				sb.append(new String(data));
			}
			System.out.println("readPacketsFromSocket: " + sb.toString());
			
			return sb.toString();
		} catch (IOException e) {
			logger.error("IOException while reading message from in stream ", e);
			throw e;
		}
	}
	
}
