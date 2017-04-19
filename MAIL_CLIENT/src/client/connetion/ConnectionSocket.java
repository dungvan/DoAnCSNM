package client.connetion;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;

public class ConnectionSocket {
	private OutputStream obj_out = null;
	private	BufferedReader _in = null;
	
	private Socket socket;
	
	public ConnectionSocket(Socket socket){
		this.socket = socket;
		try {
			obj_out = this.socket.getOutputStream();
			_in = new BufferedReader(new InputStreamReader(this.socket.getInputStream(), "UTF-8"));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public void sendMsg(String msg) throws IOException{
		
		obj_out.write((msg + "\n").getBytes("UTF-8"));
		obj_out.flush();
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
