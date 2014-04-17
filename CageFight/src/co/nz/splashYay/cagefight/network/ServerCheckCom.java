package co.nz.splashYay.cagefight.network;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

public class ServerCheckCom extends Thread {
	//String command;
		private Socket welcomeSocket;
		
		
		public ServerCheckCom(Socket socket) {
			welcomeSocket = socket;
			

		}

		/*
		 * Recieves a String command/request from a client proccess the string and
		 * acts accordingly
		 */
		@Override
		public void run() {
			try {
				BufferedReader inFromClient = new BufferedReader(new InputStreamReader(welcomeSocket.getInputStream()));
				DataOutputStream outToClient = new DataOutputStream(welcomeSocket.getOutputStream());
				
				String fromClient = inFromClient.readLine();
				if (fromClient.equalsIgnoreCase("areYouOnline")) {
					outToClient.writeBytes("IamOnline" + "\n");
				}
				
				welcomeSocket.close();
				

				

			} catch (IOException ex) {
				
			} 
		}

		
	}
