package com.oxyent.carescape.client;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.StringReader;
import java.net.Socket;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.Unmarshaller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@Configuration
@ComponentScan({ "com.oxyent.carescape.client" })
@EnableAsync
@PropertySource({ "classpath:application.properties" })
public class CarescapeClient implements CommandLineRunner {

	private static final Logger logger = LoggerFactory.getLogger(CarescapeClient.class);

	private Socket socket;

	@Autowired
	private Environment env;
	
	@Autowired
	private MessageHandler messageHandler;
	
	@Autowired
	private SessionUpdateServer sessionUpdateServer;
	
	@Autowired
	private NumericConfigStreamServer numConfigStreamServer;
	
	@Autowired
	private WaveformStreamServer waveformStreamServer;

	public static void main(String[] args) throws Exception {
		SpringApplication.run(CarescapeClient.class, args);
	}

	public void run(String... args) throws Exception {
		System.out.println("Inside run ");
		String host = env.getProperty("carescape.host");
		int port = Integer.parseInt(env.getProperty("carescape.port"));
		try {
			socket = new Socket(host, port);
			sendHelloMessage();
			sendGetSessionUpdateRequestMessage();
			sendGetNumConfigStreamRequestMessage();
			sendGetWaveformStreamRequestMessage();
		} catch (Exception e) {
			logger.error("CarescapeClientMain: some exception occured ", e);
		} finally {
			closeSocket();
		}
		logger.info("Shuting down the application...");
	}

	public String sendGetNumConfigStreamRequestMessage() throws IOException, MessagingException {
		String helloMessage = messageHandler.loadMessageFromFile("numeric-config-data.xml");
		String xmlBody = messageHandler.writeAndReadMessage(helloMessage, socket);
		logger.info("GetNumConfigStreamRequest-> response from server message.getContent(): [{}]", xmlBody);
		numConfigStreamServer.startServer();
		return null;
	}
	
	public String sendGetWaveformStreamRequestMessage() throws IOException, MessagingException {
		String helloMessage = messageHandler.loadMessageFromFile("waveform-data.xml");
		String xmlBody = messageHandler.writeAndReadMessage(helloMessage, socket);
		logger.info("GetWaveformStreamRequest-> response from server message.getContent(): [{}]", xmlBody);
		waveformStreamServer.startServer();
		return null;
	}

	public String sendHelloMessage() throws IOException, MessagingException {
		String helloMessage = messageHandler.loadMessageFromFile("hello.xml");
		String xmlBody = messageHandler.writeAndReadMessage(helloMessage, socket);
		logger.info("Hello message response from server message.getContent(): [{}]", xmlBody);
		return null;
	}
	
	private String sendGetSessionUpdateRequestMessage() throws IOException, MessagingException {
		String getSessionUpdateRequestMessage = messageHandler.loadMessageFromFile("get-session-update.xml");
		String xmlBody = messageHandler.writeAndReadMessage(getSessionUpdateRequestMessage, socket);
		logger.info("GetSessionUpdateRequest-> response from server message.getContent(): [{}]", xmlBody);
		checkSessionUpdateMessage();
		return null;
	}

	private void checkSessionUpdateMessage() throws IOException, MessagingException {
		MimeMessage sessionUpdateMessage = messageHandler.readMessageFromSocket(socket);
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		sessionUpdateMessage.writeTo(baos);
		byte[] bytes = baos.toByteArray();
		String sessionUpdateXml = new String(bytes);
		//String sessionUpdateXml = messageHandler.extractXmlBodyFromCotentObject(sessionUpdateMessage);
		logger.info("SessionUpdate-> response from server message.getContent(): [{}]", sessionUpdateXml);
		if (sessionUpdateXml == null || sessionUpdateXml.indexOf("<sessionUpdate") == -1) {
			checkSessionUpdateMessage();
		}
	}

	private Object convertXmlToObject(String xmlBody, Class classType) {
		try {
			JAXBContext jaxbContext = JAXBContext.newInstance(classType);  
			   
	        Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();  
	        StringReader reader = new StringReader(xmlBody);
	        return jaxbUnmarshaller.unmarshal(reader);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
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

}
