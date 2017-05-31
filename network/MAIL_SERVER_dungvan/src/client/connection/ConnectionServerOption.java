package client.connection;

public abstract class ConnectionServerOption {
	
	public static String SERVER_NAME = "localhost";
	public static int SERVER_SMTP_PORT = 25;
	public static int SERVER_AUTH_PORT = 3001;
	public static int SERVER_POP3_PORT = 110;
	
	public static void setSERVER_NAME(String sERVER_NAME) {
		SERVER_NAME = sERVER_NAME;
	}
	
}
