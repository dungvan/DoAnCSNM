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
				if (line.toLowerCase().equals("auth login")) {
					line = reader.readLine();
					System.out.println(line);
					if (Account_Server.Authentication(line)) {
						output.write("true\n".getBytes());

						reader.close();
						output.close();
						socket.close();
					} else {
						output.write("false\n".getBytes());

						reader.close();
						output.close();
						socket.close();
					}
				} else if (line.toLowerCase().equals("create")) {
					if (Account_Server.userIsExist(reader.readLine().trim().split(" ")[0])) {
						output.write("false\n".getBytes());

						reader.close();
						output.close();
						socket.close();
					} else {
						output.write("true\n".getBytes());

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
