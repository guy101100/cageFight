package co.nz.splashYay.cagefight.network;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

public class ClientCheckCom extends Thread {
	//String command;
	private Socket clientSocket;
	private String ipAddress;
	BufferedReader inFromServer;
	DataOutputStream outToServer;

	public ClientCheckCom(String ipAddress) {
		this.ipAddress = ipAddress;

	}

	/*
	 * Recieves a String command/request from a client proccess the string and
	 * acts accordingly
	 */
	@Override
	public void run() {

	}

	public boolean checkForServer() {
		boolean serverOnline = false;
		try {
			clientSocket = new Socket(ipAddress, 6787);

			inFromServer = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
			outToServer = new DataOutputStream(clientSocket.getOutputStream());

			outToServer.writeBytes("areYouOnline" + "\n");

			String fromServer = inFromServer.readLine();
			if (fromServer.equalsIgnoreCase("IamOnline")) {
				serverOnline = true;
			}

			clientSocket.close();
			

		} catch (IOException ex) {
			serverOnline = false;
		}
		return serverOnline;
	}

}
