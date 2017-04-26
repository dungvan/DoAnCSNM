package smtpserver;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

import dataserver.MailServerDataOption;

public class SMTP_TCPClientThread extends Thread {

	public static final byte EHLO_STATE = 0;
	public static final byte MAIL_FROM_STATE = 1;
	public static final byte RCPT_TO_STATE = 2;
	public static final byte DATA_STATE = 3;
	public static final byte END_STATE = 4;

	public String clientName;
	private Socket socket;
	private ObjectOutputStream oos;
	private ObjectInputStream ois;
	private int state = 0;

	public SMTP_TCPClientThread(Socket socket) {
		this.socket = socket;
		try {
			oos = new ObjectOutputStream(this.socket.getOutputStream());
			ois = new ObjectInputStream(this.socket.getInputStream());
		} catch (Exception ex) {
			Logger.getLogger(SMTP_TCPClientThread.class.getName()).log(Level.SEVERE, null, ex);
		}
	}

	public void sendMessage(String message) {
		try {
			oos.writeUTF(message);
			oos.flush();
		} catch (IOException ex) {
			Logger.getLogger(SMTP_TCPClientThread.class.getName()).log(Level.SEVERE, null, ex);
		}
	}

	@Override
	public void run() {
		String response = "", data = "", senderName = "", receiverName = "";
		try {
			String line = null;
			if (socket.isConnected()) {
				System.out.println("connected to " + socket.getInetAddress().getHostAddress());
				oos.writeUTF("220 Server access OK");
				oos.flush();
			}
			while (!socket.isClosed()) {
				line = ois.readUTF();
				if (line != null) {
					line = line.toLowerCase().trim();
					/*
					 * if received quit command so close connection
					 */
					if (line.equals("quit")) {
						if (state == END_STATE)
							MailServerDataOption.saveEmail(receiverName, senderName, data);
						System.out.println(line);
						sendMessage("251, Bye");
						this.oos.close();
						this.ois.close();
						this.socket.close();
						return;
					}

					/*
					 * get request and response to client
					 */
					switch (state) {
					case EHLO_STATE:
						if (line.equals("helo") || line.startsWith("ehlo ")) {
							System.out.println(line);
							response = "250 hello " + InetAddress.getLocalHost().getHostName() + " ,OK";
							state++;
						} else
							response = "ERROR HELO/HELO mail.example.com ";
						sendMessage(response);
						break;
					case MAIL_FROM_STATE:
						if (line.startsWith("mail from: <") && line.endsWith(">") && !line.split("<")[1].equals(">")) {
							/*
							 * check sender name is null?
							 */
							System.out.println(line);
							senderName = line.split("<")[1].split(">")[0];
							response = "250 sender <" + senderName + "> ,OK";
							/*
							 * insert code to check validate sender name here
							 */
							state++;
						} else
							response = "ERROR need command : MAIL FROM: <example@example.com>";
						sendMessage(response);
						break;
					case RCPT_TO_STATE:
						if (line.startsWith("rcpt to: <") && line.trim().endsWith(">")
								&& !line.split("<")[1].equals(">")) {
							/*
							 * check receiver name is null?
							 */
							System.out.println(line);
							receiverName = line.split("<")[1].split(">")[0];
							response = "250 receiver <" + receiverName + "> ,OK";
							/*
							 * insert code check validate receiver name here
							 */
							state++;
						} else
							response = "ERROR need command : RCPT TO: <example@example.com>";
						sendMessage(response);
						break;
					case DATA_STATE:
						if (line.equals("data")) {
							System.out.println(line);
							response = "354 Send message, end with a \".\" on a line by itself";
							sendMessage(response);
							/*
							 * start to get DATA here
							 */
							data = "";
							line = ois.readUTF();
							while (!line.equals(".")) {
								System.out.println(line);
								data += line + "\n";
								line = ois.readUTF();
							}
							/*
							 * got DATA
							 */
							response = "250 DATA OK";
							sendMessage(response);
							state++;
						} else {
							data = "ERROR need command DATA";
							sendMessage(data);
						}
						break;
					case END_STATE:
						sendMessage("ERROR send QUIT to disconnect this communication");
						break;
					}
				}
			}
		} catch (IOException ex) {
			Logger.getLogger(SMTP_TCPClientThread.class.getName()).log(Level.SEVERE, null, ex);
		}
	}

}
