package co.nz.splashYay.cagefight.network;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.UnknownHostException;

import co.nz.splashYay.cagefight.GameData;



public class ClientInNetCom extends Thread {

	private String ipAddress;
	private Socket clientSocket;
	private GameData gameData;
	private ObjectInputStream inFromServer;

	public ClientInNetCom(String ipAddress, GameData gameData) {
		this.ipAddress = ipAddress;

	}

	@Override
	public void run() {
		try {
			clientSocket = new Socket(ipAddress, 6789);
			inFromServer = new ObjectInputStream(clientSocket.getInputStream());
			clientSocket.setTcpNoDelay(true);

			while (!interrupted()) {

				try {
					GameData gameDataIn = (GameData) inFromServer.readUnshared();
					gameData = gameDataIn;

				} catch (ClassNotFoundException e) {
					// TODO Auto-generated catch block
					System.err.println("error reading objects");
				}

			}

		} catch (UnknownHostException ex) {
			System.err.println("Server connection Lost Error : 1");
		} catch (IOException ex) {
			System.err.println("Server connection Lost Error : 2");
		}
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	

}
