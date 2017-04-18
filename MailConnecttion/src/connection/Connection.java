package connection;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class Connection {
	private ObjectOutputStream obj_out = null;
	private	BufferedReader _in = null;
	
	private Socket socket;
	
	public Connection(Socket socket){
		this.socket = socket;
		try {
			obj_out = (ObjectOutputStream) this.socket.getOutputStream();
			_in = new BufferedReader(new InputStreamReader(this.socket.getInputStream()));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public void sendMsg(String msg) throws IOException{
		
		obj_out.writeUTF(msg);
	}
	
	public void sendObject(Object object) throws IOException{
		
		obj_out.writeObject(object);
	}
	public String receive() throws IOException{
		String receive = _in.readLine();
		return receive;
	}
	public boolean closeConnection() throws IOException{
		if(_in!=null)
			_in.close();
		if(obj_out!=null)
			obj_out.close();
		this.socket.close();
		return this.socket.isClosed();
	}
}
