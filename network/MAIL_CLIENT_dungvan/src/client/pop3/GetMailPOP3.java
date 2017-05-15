package client.pop3;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.logging.Level;
import java.util.logging.Logger;

import client.connetion.ConnectionServerOption;
import client.connetion.ConnectionSocket;
import mail.Mail;

public class GetMailPOP3 extends ConnectionServerOption {
	ConnectionSocket conn = null;
	public GetMailPOP3() {
		Socket socket;
		try {
			socket = new Socket(SERVER_NAME, SERVER_POP3_PORT);
			conn = new ConnectionSocket(socket);
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public boolean getMail(String user, String pass) {
		try {

			String response = conn.receive();
			System.out.println(response);
			if (!(response.trim().endsWith("OK"))) {
				conn.closeConnection();
				return false;
			} /*
				 * send command
				 */

			user = user.split("@")[0];
			conn.sendMsg("USER " + user);
			response = conn.receive();
			System.out.println(response);
			if (!(response.trim().startsWith("+OK"))) {
				conn.sendMsg("QUIT");
				conn.closeConnection();
				return false;
			}
			conn.sendMsg("PASS " + pass);
			response = conn.receive();
			System.out.println(response);
			if (!(response.trim().startsWith("+OK"))) {
				conn.sendMsg("QUIT");
				conn.closeConnection();
				return false;
			}
			conn.sendMsg("STAT");
			response = conn.receive();
			System.out.println(response);
			if (!(response.trim().startsWith("+OK"))) {
				conn.sendMsg("QUIT");
				conn.closeConnection();
				return false;
			}
			int numberOfMail = Integer.parseInt(response.split(" ")[1].split(" ")[0].trim());
			if (numberOfMail == 0) {
				conn.sendMsg("QUIT");
				response = conn.receive();
				System.out.println(response);
				conn.closeConnection();
				return false;
			}
			int i = 1;
			while(numberOfMail-- > 0){
				conn.sendMsg("RETR " + i);
				try {
					Mail mail = (Mail) conn.getObject();
					response = conn.receive();
					System.out.println(response);
					if (mail == null){
						conn.sendMsg("QUIT");
						conn.closeConnection();
						return false;
					}else saveEmail(mail);
				} catch (ClassNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					continue;
				}							
				conn.sendMsg("DELE " + i++);
				response = conn.receive();
				System.out.println(response);
			}
			System.out.println("send QUIT");
			conn.sendMsg("QUIT");
			response = conn.receive();
			System.out.println(response);
			conn.closeConnection();
			return true;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return false;
	}

	private static boolean saveEmail(Mail mail) {
		File dbFolder = new File("db");
		if (dbFolder.isFile()) {
			dbFolder.delete();
		}
		if (!dbFolder.exists()) {
			dbFolder.mkdir();
		}
		
		String folderName = mail.getTo().split("@")[0].trim();
		File receiverFolder = new File("db/" + folderName);
		receiverFolder.mkdir();

		String subject = mail.getSubject().replace(':', '_').replace('\\', '_')
				.replace('/', '_').replace('*', '_').replace('|', '_').replace('>', '_').replace('<', '_')
				.replace('?', '_');
		// file name can't contain \/:*?<>|

		int count = 0;
		for (File file : receiverFolder.listFiles()) {
			if (file.getName().equals(subject + "-" + mail.getFrom() + ".email")) {
				count++;
			}
		}

		File emailFile = new File("db/" + folderName + "/" + subject + "-" + mail.getFrom() + ""
				+ (count == 0 ? "" : ("_" + count)) + ".email");

		String writeToFile = mail.getDate() + "\nfrom : " + mail.getFrom() + "\nto : " + mail.getTo() + "\nSubject : "
				+ mail.getSubject() + "\n" + mail.getContent();
		FileOutputStream output;
		try {
			output = new FileOutputStream(emailFile);
			output.write(writeToFile.getBytes("UTF-8"));
			output.flush();
			output.close();
			return true;
		} catch (Exception ex) {
			Logger.getLogger(GetMailPOP3.class.getName()).log(Level.SEVERE, null, ex);
			return false;
		}
	}

}
