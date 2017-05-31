package pop3server;

import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;

import dataserver.Account_Server;
import dataserver.MailServerDataOption;
import mail.Mail;
import mainserver.MainServer_GUI;
import smtpserver.SMTP_TCPClientThread;

public class POP3_TCPClientThread extends Thread {

	private Socket socket;
	private ObjectOutputStream oos = null;
	private ObjectInputStream ois;
	private static final byte USER_STATE = 0;
	private static final byte PASSWORD_STATE = 1;
	private static final byte TRANSACTION_STATE = 2;
	private static final byte END_STATE = 3;
	private byte state = 0;

	private int[] delete = new int[1000];
	private int delCount = 0;
	private File[] listFile = new File[1000];
	private File folder;

	public POP3_TCPClientThread(Socket socket) {
		this.socket = socket;
		try {
			oos = new ObjectOutputStream(this.socket.getOutputStream());
			ois = new ObjectInputStream(this.socket.getInputStream());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
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
		String response = "", user = "", password = "";
		try {
			if (socket.isConnected()) {
				String s = "connected to " + socket.getInetAddress().getHostAddress();
				System.out.println(s);
				MainServer_GUI.ta_showpop3communication.append("\n--------------------\n"+s+"\n");
				sendMessage("220 Server access OK");
			}
			String line = null;
			while (!socket.isClosed()) {
				line = ois.readUTF();
				System.out.println(line);
				MainServer_GUI.ta_showpop3communication.append(line + "\n");
				if (line != null) {
					line = line.toLowerCase().trim();
					/*
					 * if received quit command so close connection
					 */
					if (line.equals("quit")) {
						if (state == TRANSACTION_STATE) {
							MailServerDataOption.deleteMail(delete, folder);
						}
						state = END_STATE;
						sendMessage("+OK Sayonara");
						MainServer_GUI.ta_showpop3communication.append("+OK Sayonara");
						oos.close();
						this.ois.close();
						this.socket.close();
						MainServer_GUI.ta_showpop3communication.append("close connection\n------------\n");
						return;
					}
					/*
					 * get request and response to client
					 */
					switch (state) {
					case USER_STATE:
						if (line.startsWith("user ")) {
							user = line.replaceFirst("user ", "").trim();
							if (Account_Server.userIsExist(user)) {
								response = "+OK User name accepted, password please";
								state++;
							} else
								response = "-ERR never heard of name";
						} else
							response = "-ERR please start like USER username";

						sendMessage(response);
						MainServer_GUI.ta_showpop3communication.append(response);
						break;

					case PASSWORD_STATE:
						if (line.startsWith("pass ")) {
							password = line.replaceFirst("pass ", "").trim();
							if (Account_Server.Authentication(user + " " + password)) {
								response = "+OK valid logon";

								File file = new File("db/" + user);
								listFile = file.listFiles();

								state++;
							} else
								response = "-ERR invalid password";
						} else {
							response = "-ERR please start login again";
							state--;
						}
						sendMessage(response);
						MainServer_GUI.ta_showpop3communication.append(response);
						break;

					case TRANSACTION_STATE:
						if (line.equals("stat")) {
							folder = new File("db/" + user);
							folder.mkdir();
							listFile = folder.listFiles();

							response = "+OK " + listFile.length + " " + MailServerDataOption.getBytes(folder);

							sendMessage(response);
							MainServer_GUI.ta_showpop3communication.append(response);
						} else if (line.startsWith("list")) {
							String listNumber = line.replaceFirst("list", "").trim();
							if (listNumber.equals("") || listNumber == null) {
								if (listFile.length == 0) {
									response = "+OK Mailbox empty";
								} else {
									response = "+OK Mailbox scan listing follows\n";
									int i = 0;
									while (i < listFile.length) {
										response += (++i) + " " + MailServerDataOption.getBytes(listFile[i - 1]) + "\n";
									}
								}
							} else {
								try {
									response = "+OK " + Integer.parseInt(listNumber) + " "
											+ MailServerDataOption.getBytes(listFile[Integer.parseInt(listNumber) - 1]);
								} catch (NumberFormatException e) {
									response = "-ERR invalid mail number";
								}
							}

							sendMessage(response);
							MainServer_GUI.ta_showpop3communication.append(response+"\n");

						} else if (line.startsWith("retr ")) {
							String number = line.replaceFirst("retr ", "").trim();
							if (number.equals("") || number == null) {
								response = "-ERR invalid mail number";
							} else {
								try {
									int index = Integer.parseInt(number) - 1;
									Mail mail = MailServerDataOption.readMail(listFile[index]);
									oos.writeObject(mail);
									oos.flush();
									response = "+OK " + listFile[index].length() + " bytes";
								} catch (NumberFormatException e) {
									response = "-ERR invalid number";
								}
							}

							sendMessage(response);
							MainServer_GUI.ta_showpop3communication.append(response+"\n");
						} else if (line.startsWith("dele ")) {
							String number = line.replaceFirst("dele ", "").trim();
							if (number.equals("") || number == null) {
								response = "-ERR invalid mail number";
							} else {
								try {
									int i = Integer.parseInt(number);
									delete[delCount] = i;
									delCount++;
									response = "+OK message deleted";
								} catch (NumberFormatException e) {
									response = "-ERR invalid number";
								}
							}

							sendMessage(response);
							MainServer_GUI.ta_showpop3communication.append(response+"\n");

						} else if (line.equals("rset")) {
							Arrays.fill(delete, 0);
							response = "+OK Reset state";
							state = 0;
							sendMessage(response);
							MainServer_GUI.ta_showpop3communication.append(response+"\n");
						}
						break;
					case END_STATE:
						return;
					}
				}
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
