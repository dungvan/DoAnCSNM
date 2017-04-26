package authserver;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

import dataserver.Account_Server;
import smtpserver.SMTP_TCPClientThread;

public class AUTH_TCPClientThread extends Thread {

	public String clientName;
	private Socket socket;
	private ObjectOutputStream oos;
	private ObjectInputStream ois;

	public AUTH_TCPClientThread(Socket socket) {
		this.socket = socket;
		try {
			oos = new ObjectOutputStream(this.socket.getOutputStream());
			ois = new ObjectInputStream(socket.getInputStream());
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
		System.out.println("connected to " + socket.getInetAddress().getHostAddress());
		String line = "";
		while (!socket.isClosed()) {
			try {
				line = ois.readUTF().trim();				
				/*
				 * if line.equals("auth login") do check authentication login
				 */
				if (line.equals("auth login")) {

					line = ois.readUTF();

					/*
					 * send true to client if usermane and password match with
					 * db
					 */
					if (Account_Server.Authentication(line)) {
						sendMessage("true");
						
						System.out.println("auth login - accept");
						
						ois.close();
						oos.close();
						socket.close();
					} else {
						/*
						 * send false to client if username and password match
						 * with db
						 */
						sendMessage("false");

						System.out.println("auth login - not accept");
						
						ois.close();
						oos.close();
						socket.close();
					}
				} else if (line.equals("create")) {
					/*
					 * if line.equals("create") do create acount
					 */
					line = ois.readUTF().trim();
					
					if (Account_Server.userIsExist(line.split(" ")[0])) {
						/*
						 * send exist to cllient if username is exist in db
						 */
						sendMessage("exist");

						System.out.println("create - account existed");
						
						ois.close();
						oos.close();
						socket.close();
					} else if (Account_Server.CreateAccount(line)) {
						/*
						 * send true to client if username dose not exist in db
						 * and create new email successfully
						 */
						sendMessage("true");

						System.out.println("create - successfully");
						
						ois.close();
						oos.close();
						socket.close();
					} else {
						/*
						 * send false to client if can not create email
						 */
						sendMessage("false");

						System.out.println("create - not success");
						
						ois.close();
						oos.close();
						socket.close();
					}
				} else {
					ois.close();
					oos.close();
					socket.close();
					return;
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
}
