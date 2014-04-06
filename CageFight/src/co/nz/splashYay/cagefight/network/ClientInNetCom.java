package co.nz.splashYay.cagefight.network;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import scenes.ClientGameScene;

import co.nz.splashYay.cagefight.GameData;
import co.nz.splashYay.cagefight.Player;



public class ClientInNetCom extends Thread {

	private String ipAddress;
	private Socket clientSocket;
	private GameData gameData;
	private ObjectInputStream inFromServer;
	private ClientGameScene clientGameScene;

	public ClientInNetCom(String ipAddress, GameData gameData, ClientGameScene clientGameScene) {
		this.ipAddress = ipAddress;
		this.gameData = gameData;
		this.clientGameScene = clientGameScene;

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
					
					for (Entry<Integer, Player> entry: gameDataIn.getPlayers().entrySet()) {					    
					    if (!gameData.getPlayers().containsKey(entry.getKey())) {
					        clientGameScene.addPlayerToGameDataObj(entry.getValue());
					    }
					}
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
