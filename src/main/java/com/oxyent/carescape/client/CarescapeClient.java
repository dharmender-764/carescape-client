package com.oxyent.carescape.client;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.net.UnknownHostException;

import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.internet.MimeMessage;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;

@SpringBootApplication
@Configuration
@ComponentScan({ "com.oxyent.carescape.client" })
@PropertySource({ "classpath:application.properties" })
public class CarescapeClient implements CommandLineRunner {

	private static final Logger logger = LoggerFactory.getLogger(CarescapeClient.class);

	private Socket socket;
	private InputStream inputStream;
	private OutputStream outputStream;

	@Autowired
	private Environment env;

	@Value("${mime.message.header}")
	private String mimeMessageHeader;

	public static void main(String[] args) throws Exception {
		SpringApplication.run(CarescapeClient.class, args);
	}

	public void run(String... args) throws Exception {
		System.out.println("Inside run ");
		String host = env.getProperty("carescape.host");
		int port = Integer.parseInt(env.getProperty("carescape.port"));
		try {
			boolean connectionOpened = initSocketAlongWithStreams(host, port);
			if (connectionOpened) {
				String helloMessageResponse = sendHelloMessage();
			} else {
				closeSocket();
			}
		} catch (Exception e) {
			logger.error("CarescapeClientMain: some exception occured ", e);
		} finally {
			closeStreams();
			closeSocket();
		}
		logger.info("Shuting down the application...");
	}

	private String sendHelloMessage() throws IOException, MessagingException {
		String helloMessage = loadMessageFromFile("hello.xml");
		writeMessageToOutStream(helloMessage);
		MimeMessage message = readMessageFromInStream();
		logger.info("Hello message response from server message.getContent(): [{}]", message.getContent());
		return null;
	}

	public void closeStreams() {
		logger.info("Closing in and our streams...");
		try {
			if (inputStream != null) {
				inputStream.close();
				inputStream = null;
			}
			if (outputStream != null) {
				outputStream.close();
				outputStream = null;
			}
		} catch (IOException e) {
			logger.error("Exception while closing the in and out streams ", e);
		}
	}

	private void closeSocket() {
		logger.info("Closing socket...");
		try {
			if (socket != null) {
				socket.close();
				socket = null;
			}
		} catch (IOException e) {
			logger.error("Exception while closing the socket ", e);
		}
	}

	public boolean initSocketAlongWithStreams(String host, int port) throws IOException {
		try {
			logger.info("Opening socket along with in and out stream to host {} and port {}", host, port);
			socket = new Socket(host, port);
			outputStream = socket.getOutputStream();
			inputStream = socket.getInputStream();
			if (inputStream != null && outputStream != null) {
				return true;
			}
		} catch (UnknownHostException e) {
			logger.error("Exception while opening socket/in or out stream ", e);
			e.printStackTrace();
			throw e;
		}
		return false;
	}

	public void writeMessageToOutStream(String message) {
		try {
			logger.info("Writing message to out stream...");
			java.io.PrintWriter out = new java.io.PrintWriter(socket.getOutputStream(), true);
			out.println(message);
			out.flush();
		} catch (IOException e) {
			logger.error("IOException while writing message to out stream ", e);
		}
	}

	public MimeMessage readMessageFromInStream() throws IOException, MessagingException {
		try {
			logger.info("Reading message from in stream...");
			BufferedReader br = new BufferedReader(new InputStreamReader(inputStream));
			char[] cbuf = new char[2048];
			br.read(cbuf);
			br.close();
			
			MimeMessage msg = new MimeMessage((Session)null, new ByteArrayInputStream(cbuf.toString().getBytes()));
			return msg;
		} catch (IOException e) {
			logger.error("IOException while reading message from in stream ", e);
			throw e;
		}
	}

	public String loadMessageFromFile(String fileName) throws IOException {
		File file = new File(fileName);
		String message = FileUtils.readFileToString(file, "UTF-8");
		int contentLength = message.getBytes().length;
		String fullMessage = mimeMessageHeader.replace("$content-length$", String.valueOf(contentLength)) + message;
		return fullMessage;
	}

}
