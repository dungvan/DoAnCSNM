package pop3server;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

public class POP3_TCPClientThread extends Thread {
	public static final int USER_STATE = 0;
	public static final int PASS_STATE = 1;
	public static final int TRAN_STATE = 2;
	public static final int END_STATE = 3;
	public String clientName;
	private Socket socket;
	OutputStream output;
	BufferedReader reader;
	private int state = 0;

	public POP3_TCPClientThread(Socket socket) {
		this.socket = socket;
		try {
			output = socket.getOutputStream();
		} catch (Exception ex) {
			Logger.getLogger(POP3_TCPClientThread.class.getName()).log(Level.SEVERE, null, ex);
		}

	}

	public void sendMessage(String message) {
		try {
			output.write((message + "\n").getBytes());
			output.flush();
		} catch (IOException ex) {
			Logger.getLogger(POP3_TCPClientThread.class.getName()).log(Level.SEVERE, null, ex);
		}
	}

	@Override
	public void run() {
		String response = "", user = "";
		try {
			reader = new BufferedReader(new InputStreamReader(socket.getInputStream(), "UTF-8"));
			String line_from_client = null;
			if (socket.isConnected()) {
				System.out.println("POP3 connected to " + socket.getInetAddress().getHostAddress());
				output.write("+OK POP3 server ready! \n".getBytes());
				output.flush();
			}
			while (!socket.isClosed()) {
				line_from_client = reader.readLine();
				if (line_from_client != null) {
					line_from_client = line_from_client.toLowerCase().trim();
					/*
					 * if received quit command so close connection
					 */
					if (line_from_client.equals("quit")) {
						if (state == END_STATE)
							;
						System.out.println(line_from_client);
						sendMessage("+OK " + user);
						this.output.close();
						this.reader.close();
						this.socket.close();
						return;

					}

					/*
					 * get request and response to client
					 */
					switch (state) {
					case USER_STATE:
						if (line_from_client.startsWith("user")) {
							System.out.println(line_from_client);
							user = line_from_client.substring(5, line_from_client.length()).trim();
							// take user from "USER user"
							response = "+OK User name accepted, password please";
							state++;
						} else
							response = "-ERR user error ";
						sendMessage(response);
						break;
					case PASS_STATE:
						if (line_from_client.startsWith("pass")) {
							System.out.println(line_from_client);
							// neednt take pass because if can login so user
							// and pass is true
							response = "+OK Mailbox open, " + Integer.toString(InfoMessageOfUser.numberMailOfUser(user))
									+ " messages";
							state++;
						} else
							response = "-ERR password error ";
						sendMessage(response);
						break;
					case TRAN_STATE:
						if (line_from_client.equals("stat")) {
							// response number of mail and sum size
							System.out.println(line_from_client);
							response = "+OK " + Integer.toString(InfoMessageOfUser.numberMailOfUser(user)) + " "
									+ Long.toString(InfoMessageOfUser.sumSizeMailOfUser(user));
							sendMessage(response);
							break;
						}
						if (line_from_client.equals("list")) {
							try{
							System.out.println(line_from_client);
							response = "+OK Mailbox scan listing follows.";
							for (int i = 0; i < InfoMessageOfUser.numberMailOfUser(user); i++) {
								response += Integer.toString(i + 1) + " "
										+ Long.toString(InfoMessageOfUser.getEmailSize(user, i)) + ".";
							}
							sendMessage(response);
							if(InfoMessageOfUser.numberMailOfUser(user)==0){
								response = "-EER No such message";
								sendMessage(response);
							}
							break;
						}
						catch (Exception e) {
							Logger.getLogger(POP3_TCPClientThread.class.getName()).log(Level.SEVERE, null, e);
						}
					}
						if (line_from_client.startsWith("retr")) {
							System.out.println(line_from_client);
							try {
								if(InfoMessageOfUser.getEmailString(user, 0)==null)
								{
									response = "-EER No such message";
									sendMessage(response);
								}
								else{
									response = "+OK " + InfoMessageOfUser.getEmailString(user, 0);
									sendMessage(response);
								}
								break;
							} catch (Exception e) {
								Logger.getLogger(POP3_TCPClientThread.class.getName()).log(Level.SEVERE, null, e);
							}
						}
	
						if (line_from_client.startsWith("dele")) {
							System.out.println(line_from_client);
							try {
								int ID = Integer.parseInt(line_from_client.substring(5, line_from_client.length()));
								if (InfoMessageOfUser.deleteEmail(user, 0)){
									response = "+OK Message deleted";
									sendMessage(response);
									if (InfoMessageOfUser.numberMailOfUser(user)==0)
										state++;
									// if client has taken all mail already, state++ to END_STATE 
								}
								else {
									response = "-EER No such message";
									sendMessage(response);
								}	
							} catch (Exception e) {
								Logger.getLogger(POP3_TCPClientThread.class.getName()).log(Level.SEVERE, null, e);
							}
						} 
						break;
					case END_STATE:
						return;
					}
				}
			}
		} catch (Exception e1) {
			Logger.getLogger(POP3_TCPClientThread.class.getName()).log(Level.SEVERE, null, e1);
		}
	}

}

class InfoMessageOfUser {
	public static int numberMailOfUser(String user) {
		File file = new File("db/" + user.split("@")[0].trim());
		file.mkdir();
		if (file.listFiles() == null)
			return 0;
		return file.listFiles().length;
	}

	public static long getEmailSize(String user, int ID) throws Exception {
		File f;
		File file = new File("db/" + user.split("@")[0].trim());
		file.mkdir();
		System.out.println(file.getName());
		if (file.listFiles() == null)
			return 0;
		else {
			f = file.listFiles()[ID];
			return f.length();
		}
	}

	public static String getEmailString(String user, int ID) throws Exception {
		File f;
		File file = new File("db/" + user.split("@")[0].trim());
		file.mkdir();
		System.out.println(file.getName());
		String arrl = "";
		if (file.listFiles() == null)
			return null;
		else {
			f = file.listFiles()[ID];
			FileInputStream fis = new FileInputStream(f.getPath());
			DataInputStream dis = new DataInputStream(fis);

			// Đọc dữ liệu
			try {
				String line;
				while ((line = dis.readLine()) != null) {
					arrl += line + ".";
				}
				dis.close();
			} catch (Exception ex) {
				Logger.getLogger(POP3_TCPClientThread.class.getName()).log(Level.SEVERE, null, ex);
				return null;
			}
		}
		return arrl;
	}

	public static long sumSizeMailOfUser(String user) {
		long sum = 0;
		File file = new File("db/" + user.split("@")[0].trim());
		file.mkdir();
		String arrl = "";
		if (file.listFiles() == null)
			System.out.println("null");
		else {
			for (File f : file.listFiles())
				sum += f.length();
		}
		return sum;
	}

	public static boolean deleteEmail(String user, int ID) {
		File file = new File("db/" + user.split("@")[0].trim());
		file.mkdir();
		if (file.listFiles() == null)
			System.out.println("null");
		else {
			return file.listFiles()[ID].delete();
		}
		return false;
	}

}