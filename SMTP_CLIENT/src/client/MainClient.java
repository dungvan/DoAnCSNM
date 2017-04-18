package client;

import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author Mr_MangCau
 */
public class MainClient {

	private static String serverAddress;
	private static int port;
	private static Scanner sc;

	public static void main(String[] args) {
		if (args.length < 2) {
			System.out.println("You must provide server address and socket port");
			return;
		}
		serverAddress = args[0];
		try {
			port = Integer.parseInt(args[1]);
		} catch (Exception e) {
			System.out.println("Error! Socket port is a number!");
			return;
		}
		sc = new Scanner(System.in);
		start();
	}

	private static OutputStream output;
	private static BufferedReader reader;

	public static void handleResponse(String response) {

	}

	public static void register() {

	}

	public static void start() {
		try {

			// TCP
			EmailClient email = new EmailClient();
			email.connect(serverAddress, port);
			email.command();
		} catch (Exception ex) {

			Logger.getLogger(MainClient.class.getName()).log(Level.SEVERE, null, ex);

		}
	}
}
