package client.connetion;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.Socket;

public class ConnectionSocket {

	private ObjectOutputStream oos = null;
	private ObjectInputStream ois = null;

	private Socket socket;

	public ConnectionSocket(Socket socket) {
		this.socket = socket;
		try {
			oos = new ObjectOutputStream(this.socket.getOutputStream());
			ois = new ObjectInputStream(this.socket.getInputStream());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void sendMsg(String msg) throws IOException {

		oos.writeUTF(msg);
		oos.flush();
	}

	public String receive() throws IOException {
		String receive = "";
		if (!this.socket.isClosed()) {
			String line = ois.readUTF();
			receive += line;
		}
		return receive;
	}

	public Object getObject() throws ClassNotFoundException, IOException {
		Object obj = ois.readObject();
		return obj;
	}

	public boolean closeConnection() throws IOException {
		if (ois != null)
			ois.close();
		if (oos != null)
			oos.close();
		if (ois != null)
			ois.close();
		this.socket.close();
		return this.socket.isClosed();
	}
}
