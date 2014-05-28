package co.nz.splashYay.cagefight.network;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class ServerCheckListener extends Thread {

	
	public ServerCheckListener() {

	}

	@Override
	public void run() {
		System.out.println("Online Checker Started");
		try {
			ServerSocket welcomeSocket = new ServerSocket(6786);

			while (!interrupted()) {
				Socket connectionSocket = welcomeSocket.accept(); //when information comes in through that socket accept it for processing

				ServerCheckCom netThread = new ServerCheckCom(connectionSocket); //Create a new Communication Class to thread
				//Thread thread = new Thread(netThread); //Create a new network communication thread to process this information
				netThread.start(); //Start the network communication thread 
				//while true/while the server program is running it will continue to loop this thread waiting for more instructions to process from clients

			}
		} catch (IOException ex) {
			System.err.println("socket not created");
		}
	}
}
