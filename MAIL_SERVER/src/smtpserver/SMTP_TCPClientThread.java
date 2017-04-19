package smtpserver;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

public class SMTP_TCPClientThread extends Thread {

	public static final int EHLO_STATE = 0;
	public static final int MAIL_FROM_STATE = 1;
	public static final int RCPT_TO_STATE = 2;
	public static final int DATA_STATE = 3;
	public static final int END_STATE = 4;

	public String clientName;
	private Socket socket;
	OutputStream output;
	BufferedReader reader;
	private int state = 0;

	public SMTP_TCPClientThread(Socket socket) {
		this.socket = socket;
		try {
			output = socket.getOutputStream();
		} catch (Exception ex) {
			Logger.getLogger(SMTP_TCPClientThread.class.getName()).log(Level.SEVERE, null, ex);
		}
	}

	public void sendMessage(String message) {
		try {
			output.write((message + "\n").getBytes());
			output.flush();
		} catch (IOException ex) {
			Logger.getLogger(SMTP_TCPClientThread.class.getName()).log(Level.SEVERE, null, ex);
		}
	}

	@Override
	public void run() {
		String response = "", data = "", senderName = "", receiverName = "";
		try {
			reader = new BufferedReader(new InputStreamReader(socket.getInputStream(), "UTF-8"));
			String line = null;
			if (socket.isConnected()) {
				System.out.println("connected to " + socket.getInetAddress().getHostAddress());
				output.write("220 Server access OK \n".getBytes());
				output.flush();
			}
			while (!socket.isClosed()) {
				line = reader.readLine();
				if (line != null) {
					line = line.toLowerCase().trim();
					/*
					 * if received quit command so close connection
					 */
					if (line.equals("quit")) {
						if (state == END_STATE)
							saveEmail(receiverName, senderName, data);
						System.out.println(line);
						sendMessage("251, Bye");
						this.output.close();
						this.reader.close();
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
							line = reader.readLine();
							while (!line.equals(".")) {
								System.out.println(line);
								data += line + "\n";
								line = reader.readLine();
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

	private boolean saveEmail(String receiverName, String senderName, String data) {

		String folderName = receiverName.split("@")[0].trim();
		File receiverFolder = new File("db/" + folderName);
		receiverFolder.mkdir();

		String subject = "no subject";
		int indexSubject_start;
		if ((indexSubject_start = data.toLowerCase().lastIndexOf("subject: ")) >= 0) {
			int indexSubject_end;
			indexSubject_end = data.substring(indexSubject_start, data.length() - 1).indexOf("\n");
			subject = data.substring(indexSubject_start, indexSubject_end).toLowerCase().replaceFirst("subject: ", "")
					.replace(':', '_').replace('\\', '_').replace('/', '_').replace('*', '_').replace('|', '_')
					.replace('>', '_').replace('<', '_').replace('?', '_');
			// file name can't contain \/:*?<>|
		}

		int count = 0;
		for (File file : receiverFolder.listFiles()) {
			if (file.getName().equals(subject + "-" + senderName + ".email")) {
				count++;
			}
		}

		File emailFile = new File("db/" + folderName + "/" + subject + "-" + senderName + ""
				+ (count == 0 ? "" : ("_" + count)) + ".email");

		Date current = new Date();

		String pattern = "EEE, dd MMM yyyy HH:mm:ss Z";
		SimpleDateFormat format = new SimpleDateFormat(pattern);
		String dateStr = format.format(current);

		String writeToFile = dateStr + "\nfrom : " + senderName + "\nto : " + receiverName + "\n\n" + data;
		FileOutputStream output;
		try {
			output = new FileOutputStream(emailFile);
			output.write(writeToFile.getBytes("UTF-8"));
			output.flush();
			output.close();
			return true;
		} catch (Exception ex) {
			Logger.getLogger(SMTP_TCPClientThread.class.getName()).log(Level.SEVERE, null, ex);
			return false;
		}
	}
}
