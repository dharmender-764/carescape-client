import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;

public class Client {
	
	public static void main1(String[] args) {
		String content = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\\n<s:sapphire xmlns:s=\"urn:ge:sapphire:sapphire_1\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" version=\"2.0.1\">\\n<hello xmlns=\"urn:ge:sapphire:helloMsg_1\">\\n<header xmlns=\"urn:ge:sapphire:base_1\">\\n<sessionID V=\"hhda2222hdhd\" />\\n<msgCHN V=\"0\" />\\n<msgSQN V=\"1\" />\\n<requestID V=\"100\" />\\n<creationDateTime V=\"2016-08-11T08:30:00.445Z\" />\\n</header>\\n</hello>\\n</s:sapphire>";
		String fullContent = "------=_Part_0_1924966548.1470898898114\\r\\nContent-Length: 422\\r\\nContent-Type: application/x-sapphire+xml\\r\\nContent-Transfer-Encoding: binary\\r\\n\\r\\n<?xml version=\"1.0\" encoding=\"UTF-8\"?>\\n<s:sapphire xmlns:s=\"urn:ge:sapphire:sapphire_1\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" version=\"2.0.1\">\\n<hello xmlns=\"urn:ge:sapphire:helloMsg_1\">\\n<header xmlns=\"urn:ge:sapphire:base_1\">\\n<sessionID V=\"hhda2222hdhd\" />\\n<msgCHN V=\"0\" />\\n<msgSQN V=\"1\" />\\n<requestID V=\"100\" />\\n<creationDateTime V=\"2016-08-11T08:30:00.445Z\" />\\n</header>\\n</hello>\\n</s:sapphire>\\n\\r\\n------=_Part_0_1924966548.1470898898114--";
		String textToServer3 = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n<s:sapphire xmlns:s=\"urn:ge:sapphire:sapphire_1\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" version=\"2.0.1\">\n<hello xmlns=\"urn:ge:sapphire:helloMsg_1\">\n<header xmlns=\"urn:ge:sapphire:base_1\">\n<sessionID V=\"AC1B9C4C0000978557A78148\"/>\n<msgCHN V=\"0\"/>\n<msgSQN V=\"1\"/>\n<requestID V=\"10\"/>\n<creationDateTime V=\"2016-08-07T15:19:39.216Z\"/>\n</header>\n</hello>\n</s:sapphire>\n\n\n";
		String textToServer4 = "--gc0y0pkh9ex\nContent-Type: application/x—sapphire+xml\nContent-Transfer-Encoding: binary\nContent-Length: 418\n\n<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n<s:sapphire xmlns:s=\"urn:ge:sapphire:sapphire_1\" xmlns=\"urn:ge:sapphire:helloMsg_1\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema—instance\" version=\"0.0\">\n<hello>\n<header xmlns=\"urn:ge:sapphire:base_1\">\n<sessionID V=\"7E006BOC000007D749CCF9CE\"/>\n<msgCHN V=\"0\"/>\n<msgSQN V=\"1\"/>\n<requestID V=\"1\"/>\n<creationDateTime V=\"2009-03—27T16:07:42.944Z\"/>\n</header>\n</hello>\n</s:sapphire>\n\n--g00y0pkh9ex--\n\n\n";
		System.out.println(content.getBytes().length);
		System.out.println(textToServer3.getBytes().length);
		System.out.println(textToServer4.getBytes().length);
		System.out.println(fullContent.getBytes().length);
	}

	public static void main(String[] args) throws IOException {

		Socket socket = null;
		PrintWriter out = null;
		BufferedReader in = null;

		try {
			socket = new Socket("172.22.148.164", 2007);
			out = new PrintWriter(socket.getOutputStream(), true);
			in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
		} catch (UnknownHostException e) {
			System.err.println("Don't know about host");
		} catch (IOException e) {
			System.err.println("Couldn't get I/O for the connection");
		}
		System.out.println("Connected");

		String textToServer = null;
		String textToServer1 = "MIME-Version: 1.0\\r\\nContent-Type: multipart/mixed;boundary=\"----=_Part_0_1924966548.1470898898114\"\\r\\nContent-Length: 620\\r\\n\\r\\n------=_Part_0_1924966548.1470898898114\\r\\nContent-Length: 422\\r\\nContent-Type: application/x-sapphire+xml\\r\\nContent-Transfer-Encoding: binary\\r\\n\\r\\n<?xml version=\"1.0\" encoding=\"UTF-8\"?>\\n<s:sapphire xmlns:s=\"urn:ge:sapphire:sapphire_1\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" version=\"2.0.1\">\\n<hello xmlns=\"urn:ge:sapphire:helloMsg_1\">\\n<header xmlns=\"urn:ge:sapphire:base_1\">\\n<sessionID V=\"hhda2222hdhd\" />\\n<msgCHN V=\"0\" />\\n<msgSQN V=\"1\" />\\n<requestID V=\"100\" />\\n<creationDateTime V=\"2016-08-11T08:30:00.445Z\" />\\n</header>\\n</hello>\\n</s:sapphire>\\n\\r\\n------=_Part_0_1924966548.1470898898114--\\r\\n";
		textToServer = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\\n<s:sapphire xmlns:s=\"urn:ge:sapphire:sapphire_1\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" version=\"2.0.1\">\\n<hello xmlns=\"urn:ge:sapphire:helloMsg_1\">\\n<header xmlns=\"urn:ge:sapphire:base_1\">\\n<sessionID V=\"hhda2222hdhd\" />\\n<msgCHN V=\"0\" />\\n<msgSQN V=\"1\" />\\n<requestID V=\"100\" />\\n<creationDateTime V=\"2016-08-11T08:30:00.445Z\" />\\n</header>\\n</hello>\\n</s:sapphire>\\r\\n";
		String textToServer2 = "MIME-Version: 1.0\\r\\nContent-Type: multipart/mixed; boundary=\"gc0y0pkb9ex\"\\r\\nContent—Length: 572\\r\\n\\r\\n--gc0y0pkh9ex\\r\\nContent-Type: application/x—sapphire+xml\\r\\nContent-Transfer-Encoding: binary\\r\\nContent-Length: 501\\r\\n\\r\\n<?xml version=\"1.0\" encoding=\"UTF-8\"?>\\n<s:sapphire xmlns:s=\"urn:ge:sapphire:sapphire_1\" xmlns=\"urn:ge:sapphire:helloMsg_1\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema—instance\" version=\"0.0\">\\n    <hello>\\n        <header xmlns=\"urn:ge:sapphire:base_1\">\\n            <sessionID V=\"7E006BOC000007D749CCF9CE\"/>\\n            <msgCHN V=\"0\"/>\\n            <msgSQN V=\"1\"/>\\n            <requestID V=\"1\"/>\\n            <creationDateTime V=\"2009-03—27T16:07:42.944Z\"/>\\n        </header>\\n    </hello>\\n</s:sapphire>\\n\\r\\n—g00y0pkh9ex—\\r\\n";
		String textToServer3 = "MIME-Version: 1.0\nContent-Type: application/x-sapphire+xml\nContent-Transfer-Encoding: binary\nContent-Length: 420\n\n<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n<s:sapphire xmlns:s=\"urn:ge:sapphire:sapphire_1\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" version=\"2.0.1\">\n<hello xmlns=\"urn:ge:sapphire:helloMsg_1\">\n<header xmlns=\"urn:ge:sapphire:base_1\">\n<sessionID V=\"AC1B9C4C0000978557A78148\"/>\n<msgCHN V=\"0\"/>\n<msgSQN V=\"1\"/>\n<requestID V=\"10\"/>\n<creationDateTime V=\"2016-08-07T15:19:39.216Z\"/>\n</header>\n</hello>\n</s:sapphire>\n\n\n";
		System.out.println(textToServer3);
		out.println(textToServer3);
		out.flush();
		
		char[] cbuf = new char[2048];
		in.read(cbuf);
		System.out.println(cbuf);
		
		 

		out.close();
		in.close();
		socket.close();
	}

}