package client.smtp;

import java.io.*;
import java.net.*;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

public class SendMailSMTP {

	Socket smtpSocket = null;
	OutputStream sockOut = null;
	InputStream sockIn = null;

	public void connect(String server, int port) throws Exception {
		smtpSocket = new Socket(server, port);
		sockOut = smtpSocket.getOutputStream();
		sockIn = smtpSocket.getInputStream();
	}

	// Sending e-mail
	public void command() {
		try {
			byte[] readbytes = new byte[1024];
			int num = sockIn.read(readbytes);
			System.out.println(new String(readbytes, 0, num));
			Scanner scan = new Scanner(System.in);
			String response = "";
			System.out.println("Command here : \n");
			do {
				/*
				 * send ccommand
				 */
				String line = scan.nextLine();
				sockOut.write((line + "\n").getBytes("UTF-8"));
				sockOut.flush();
				readbytes = new byte[1000];
				num = sockIn.read(readbytes);
				response = new String(readbytes, 0, num);
				System.out.print(response);
				
				/*
				 * send data mail
				 */
				if (response.trim().startsWith("354")) {
					do {
						line = scan.nextLine();
						sockOut.write((line+"\n").getBytes("UTF-8"));
						sockOut.flush();
					} while (!line.equals("."));
					readbytes = new byte[1000];
					num = sockIn.read(readbytes);
					response = new String(readbytes, 0, num);
					System.out.print(response);
				}
				/*
				 * end send data mail
				 */
				
			} while (!response.toLowerCase().trim().startsWith("251, bye"));

			sockIn.close();
			sockOut.close();
			smtpSocket.close();

		} catch (Exception e) {
			System.err.println(e);
		}
	}

	// Note: we should not use buffering and the method readLine(),
	// as we do not know how many lines the server returns
	// and the invocation of readLine is a blocking one
	// We assume that each response fits into 10000 bytes
	// private void readResponse(int checkCode) throws IOException {
	// byte[] readBytes = new byte[10000];
	// int num = sockIn.read(readBytes);
	// String resp = new String(readBytes, 0, num);
	// System.out.println("Server: " + resp);
	// if (!resp.startsWith(String.valueOf(checkCode))) {
	// throw new IOException("Unexpected response from the server");
	// }
	// }

}
