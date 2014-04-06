package co.nz.splashYay.cagefight.network;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import scenes.ServerGameScene;

import co.nz.splashYay.cagefight.GameData;
import co.nz.splashYay.cagefight.MainActivity;


public class InFromClientListener extends Thread {

	private GameData gameData;
	private ServerGameScene serverScene;

	public InFromClientListener(GameData gameData, ServerGameScene serverScene) {
		this.gameData = gameData;
		this.serverScene = serverScene;

	}

	@Override
	public void run() {
		System.out.println("Client IN Listener Started");
		try {
			ServerSocket welcomeSocket = new ServerSocket(6788);

			while (!interrupted()) {
				Socket connectionSocket = welcomeSocket.accept(); //when information comes in through that socket accept it for processing

				InFromClientNetCom netThread = new InFromClientNetCom(connectionSocket, this, gameData, serverScene ); //Create a new Communication Class to thread
				//Thread thread = new Thread(netThread); //Create a new network communication thread to process this information
				netThread.start(); //Start the network communication thread 
				//while true/while the server program is running it will continue to loop this thread waiting for more instructions to process from clients

			}
		} catch (IOException ex) {
			System.err.println("socket not created");
		}
	}
}
