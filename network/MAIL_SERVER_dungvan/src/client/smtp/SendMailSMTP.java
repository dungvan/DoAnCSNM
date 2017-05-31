package client.smtp;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.net.Socket;
import client.connection.*;

public class SendMailSMTP extends ConnectionServerOption{

	Socket smtpSocket = null;
	OutputStream sockOut = null;
	InputStream sockIn = null;
	ConnectionSocket conn = null;

	public void connect() throws Exception {
		smtpSocket = new Socket(SERVER_NAME, SERVER_SMTP_PORT);
		conn = new ConnectionSocket(smtpSocket);
	}

	// Sending e-mail
	public boolean sendMail(String mailfrom, String mailto, String subject, String data) throws Exception {
		if(data.trim().startsWith("Subject : ")){
			subject = data.trim().split("\n")[0].split("subject : ")[1];
			data = data.trim().split("\n")[1];
		}
		connect();
		try {
			String response = conn.receive();
			System.out.println(response);
			if (!(response.trim().startsWith("220"))) {
				conn.closeConnection();
				return false;
			}
			/*
			 * send command
			 */
			conn.sendMsg("HELO");
			response = conn.receive();
			System.out.println(response);
			if (!(response.trim().startsWith("250"))) {
				conn.closeConnection();
				return false;
			}

			conn.sendMsg("MAIL FROM: <" + mailfrom + ">");
			response = conn.receive();
			System.out.println(response);
			if (!(response.trim().startsWith("250"))) {
				conn.closeConnection();
				return false;
			}

			conn.sendMsg("RCPT TO: <" + mailto + ">");
			response = conn.receive();
			System.out.println(response);
			if (!(response.trim().startsWith("250"))) {
				conn.closeConnection();
				return false;
			}

			conn.sendMsg("DATA");
			response = conn.receive();
			System.out.println(response);
			if (!(response.trim().startsWith("354"))) {
				conn.closeConnection();
				return false;
			}
			/*
			 * send data mail
			 */
			if(subject.equals("")) subject = "no subject";
			conn.sendMsg("Subject : " + subject);
			conn.sendMsg(data);
			conn.sendMsg(".");
			response = conn.receive();
			System.out.println(response);
			if (!(response.trim().startsWith("250"))) {
				conn.closeConnection();
				return false;
			}
			conn.sendMsg("QUIT");
			response = conn.receive();
			if (response.trim().startsWith("251")) {
				conn.closeConnection();
				return true;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		try {
			conn.closeConnection();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return false;
	}

}
