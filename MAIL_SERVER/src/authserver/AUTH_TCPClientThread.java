package authserver;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

import smtpserver.SMTP_TCPClientThread;

public class AUTH_TCPClientThread extends Thread {

	public String clientName;
	private Socket socket;
	OutputStream output;
	BufferedReader reader;

	public AUTH_TCPClientThread(Socket socket) {
		this.socket = socket;
		try {
			output = socket.getOutputStream();
			reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
		} catch (Exception ex) {
			Logger.getLogger(SMTP_TCPClientThread.class.getName()).log(Level.SEVERE, null, ex);
		}
	}

	@Override
	public void run() {
		System.out.println("connected to " + socket.getInetAddress().getHostAddress());
		String line = "";
		while (!socket.isClosed()) {
			try {
				line = reader.readLine();
				System.out.println(line);
				/*
				 * if line.equals("auth login") do check authentication login
				 */
				if (line.toLowerCase().equals("auth login")) {
					line = reader.readLine();
					System.out.println(line);
					/*
					 * send true to client if usermane and password match with db
					 */
					if (Account_Server.Authentication(line)) {
						output.write("true\n".getBytes());

						reader.close();
						output.close();
						socket.close();
					} else {
						/*
						 * send false to client if username and password match with db
						 */
						output.write("false\n".getBytes());

						reader.close();
						output.close();
						socket.close();
					}
				} else if (line.toLowerCase().equals("create")) {
					/*
					 *create acount
					 */
					line = reader.readLine();
					System.out.println(line);
					if (Account_Server.userIsExist(line.trim().split(" ")[0])) {
						/*
						 * send exist to client if username is exist in db
						 */
						output.write("exist\n".getBytes());

						reader.close();
						output.close();
						socket.close();
					} else if(Account_Server.CreateAccount(line)){
						/*
						 * send true to client if username dose not exist in db and create new email successfully
						 */
						output.write("true\n".getBytes());

						reader.close();
						output.close();
						socket.close();
					}else{
						/*
						 * send false to client if can not create email
						 */
						output.write("false\n".getBytes());

						reader.close();
						output.close();
						socket.close();
					}
				} else {
					reader.close();
					output.close();
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
