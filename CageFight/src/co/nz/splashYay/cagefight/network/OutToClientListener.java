package co.nz.splashYay.cagefight.network;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashSet;
import java.util.logging.Level;
import java.util.logging.Logger;


import co.nz.splashYay.cagefight.GameData;
import co.nz.splashYay.cagefight.scenes.ServerGameScene;


public class OutToClientListener extends Thread {
	
	
	private GameData gameData;
	private ServerGameScene serverScene;
	private ClientUpdater clientUpdater;
	
	public OutToClientListener(GameData gameData, ServerGameScene serverScene) {
		
		this.gameData = gameData;
		this.serverScene = serverScene;
		clientUpdater = new ClientUpdater(gameData, serverScene);

	}

	@Override
	public void run() {
		try {
			System.out.println("Client OUT Listener Started");
			ServerSocket welcomeSocket = new ServerSocket(6789);
			clientUpdater.start();

			while (true) {
				Socket connectionSocket = welcomeSocket.accept(); //when information comes in through that socket accept it for processing
				OutToClientNetCom netThread = new OutToClientNetCom(connectionSocket, this, gameData, serverScene); //Create a new Communication Class to thread
				//Thread thread = new Thread(netThread); //Create a new network communication thread to process this information
				netThread.start(); //Start the network communication thread

				//while true/while the server program is running it will continue to loop this thread waiting for more instructions to process from clients

			}
		} catch (IOException ex) {
			Logger.getLogger(OutToClientListener.class.getName()).log(Level.SEVERE, null, ex);
		}
	}

	

	public void removeClient(OutToClientNetCom netCom) {
		clientUpdater.removeClient(netCom);
	}

	public void addClient(OutToClientNetCom netCom) {
		clientUpdater.addClient(netCom);
	}
}
