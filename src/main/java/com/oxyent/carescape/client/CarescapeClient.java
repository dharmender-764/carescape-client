package com.oxyent.carescape.client;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.StringReader;
import java.net.Socket;
import java.util.concurrent.Executor;

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
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.core.task.SimpleAsyncTaskExecutor;
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
	
	@Bean
    public Executor taskExecutor() {
        return new SimpleAsyncTaskExecutor();
    }

	public void run(String... args) throws Exception {
		System.out.println("Inside run ");
		String host = env.getProperty("carescape.host");
		int port = Integer.parseInt(env.getProperty("carescape.port"));
		try {
			socket = new Socket(host, port);
			sendHelloMessage();
			sendGetSessionUpdateRequestMessage();
			//sendGetNumConfigStreamRequestMessage();
			sendGetWaveformStreamRequestMessage();
		} catch (Exception e) {
			logger.error("CarescapeClientMain: some exception occured ", e);
		} finally {
			closeSocket();
		}
		logger.info("Shuting down the application...");
	}

	public String sendGetNumConfigStreamRequestMessage() throws IOException, MessagingException {
		String numericConfigMessage = messageHandler.loadMessageFromFile("numeric-config-data.xml");
		messageHandler.writeMessageOnSocket(numericConfigMessage, socket);
		numConfigStreamServer.startServer();
		
		waitAndReplyForBinHeaderMsg();
		waitAndReplyForBinDescMsg();
		return null;
	}
	
	private void waitAndReplyForBinDescMsg() throws IOException, MessagingException {
		String binDescMessage = waitForBinDescMessage();
		String binDescGenericMessage = messageHandler.loadMessageFromFileWithoutContentLength("bindesc-generic-response.xml");
		binDescGenericMessage = updateMsgSQNNoInMessage(binDescMessage, binDescGenericMessage);
		binDescGenericMessage = messageHandler.updateContentLentgh(binDescGenericMessage);
		messageHandler.writeMessageOnSocket(binDescGenericMessage, socket);
	}

	private void waitAndReplyForBinHeaderMsg() throws IOException, MessagingException {
		String binHeaderMessage = waitForBinHeaderMessage();
		String binHeaderGenericMessage = messageHandler.loadMessageFromFileWithoutContentLength("binheader-generic-response.xml");
		binHeaderGenericMessage = updateMsgSQNNoInMessage(binHeaderMessage, binHeaderGenericMessage);
		binHeaderGenericMessage = messageHandler.updateContentLentgh(binHeaderGenericMessage);
		messageHandler.writeMessageOnSocket(binHeaderGenericMessage, socket);
	}

	private String waitForBinHeaderMessage() throws IOException, MessagingException {
		MimeMessage sessionUpdateMessage = messageHandler.readMessageFromSocket(socket);
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		sessionUpdateMessage.writeTo(baos);
		byte[] bytes = baos.toByteArray();
		String binHeaderXml = new String(bytes);
		//String sessionUpdateXml = messageHandler.extractXmlBodyFromCotentObject(sessionUpdateMessage);
		logger.info("BinHeader-> response from server message.getContent(): [{}]", binHeaderXml);
		if (binHeaderXml == null || binHeaderXml.indexOf("<binHeader") == -1) {
			return waitForBinHeaderMessage();
		}
		return binHeaderXml;
	}
	
	private String waitForBinDescMessage() throws IOException, MessagingException {
		MimeMessage sessionUpdateMessage = messageHandler.readMessageFromSocket(socket);
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		sessionUpdateMessage.writeTo(baos);
		byte[] bytes = baos.toByteArray();
		String binDescXml = new String(bytes);
		//String sessionUpdateXml = messageHandler.extractXmlBodyFromCotentObject(sessionUpdateMessage);
		logger.info("BinDesc-> response from server message.getContent(): [{}]", binDescXml);
		if (binDescXml == null || binDescXml.indexOf("<binDescriptor") == -1) {
			return waitForBinDescMessage();
		}
		return binDescXml;
	}
	
	public String sendGetWaveformStreamRequestMessage() throws IOException, MessagingException {
		String waveformStreamMessage = messageHandler.loadMessageFromFile("waveform-data.xml");
		messageHandler.writeMessageOnSocket(waveformStreamMessage, socket);
		waveformStreamServer.startServer();
		
		waitAndReplyForBinHeaderMsg();
		waitAndReplyForBinDescMsg();
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
		String sessionUpdateMessage = checkSessionUpdateMessage();
		
		String sessionUpdateGenericMessage = messageHandler.loadMessageFromFileWithoutContentLength("generic-response.xml");
		sessionUpdateGenericMessage = updateMsgSQNNoInMessage(sessionUpdateMessage, sessionUpdateGenericMessage);
		sessionUpdateGenericMessage = messageHandler.updateContentLentgh(sessionUpdateGenericMessage);
		messageHandler.writeMessageOnSocket(sessionUpdateGenericMessage, socket);
		return null;
	}

	public String updateMsgSQNNoInMessage(String sessionUpdateMessage, String sessionUpdateGenericMessage) {
		String substring = sessionUpdateMessage.substring(sessionUpdateMessage.indexOf("<msgSQN V=\"") + 11);
		String msgSQNNo = substring.substring(0, substring.indexOf("\""));
		return sessionUpdateGenericMessage.replace("$msgSQNNo$", msgSQNNo);
	}

	private String checkSessionUpdateMessage() throws IOException, MessagingException {
		MimeMessage sessionUpdateMessage = messageHandler.readMessageFromSocket(socket);
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		sessionUpdateMessage.writeTo(baos);
		byte[] bytes = baos.toByteArray();
		String sessionUpdateXml = new String(bytes);
		//String sessionUpdateXml = messageHandler.extractXmlBodyFromCotentObject(sessionUpdateMessage);
		logger.info("SessionUpdate-> response from server message.getContent(): [{}]", sessionUpdateXml);
		if (sessionUpdateXml == null || sessionUpdateXml.indexOf("<sessionUpdate") == -1) {
			return checkSessionUpdateMessage();
		}
		return sessionUpdateXml;
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
