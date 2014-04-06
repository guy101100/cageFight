package co.nz.splashYay.cagefight.network;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashSet;
import java.util.logging.Level;
import java.util.logging.Logger;

import scenes.ServerGameScene;

import co.nz.splashYay.cagefight.GameData;


public class OutToClientListener extends Thread {
	
	private HashSet<OutToClientNetCom> clients;
	private GameData gameData;
	private ServerGameScene serverScene;

	public OutToClientListener(GameData gameData, ServerGameScene serverScene) {
		clients = new HashSet<OutToClientNetCom>();
		this.gameData = gameData;
		this.serverScene = serverScene;

	}

	@Override
	public void run() {
		try {
			System.out.println("Client OUT Listener Started");
			ServerSocket welcomeSocket = new ServerSocket(6789);

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

	public void updateClients() {
		//long start = System.currentTimeMillis();
		for (OutToClientNetCom onc : clients) {
			onc.updateClient();
		}
		long fin = System.currentTimeMillis();
		//System.out.println("time : " + (fin - start));
	}

	public void removeClient(OutToClientNetCom netCom) {
		clients.remove(netCom);
	}

	public void addClient(OutToClientNetCom netCom) {
		if (!clients.contains(netCom)) {
			clients.add(netCom);
		}

	}
}
