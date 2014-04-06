package co.nz.splashYay.cagefight.network;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

import scenes.ServerGameScene;
import co.nz.splashYay.cagefight.GameData;
import co.nz.splashYay.cagefight.Player;
import co.nz.splashYay.cagefight.PlayerControlCommands;


public class InFromClientNetCom extends Thread {

	//String command;
	private Socket welcomeSocket;
	private InFromClientListener iFCL;
	private ObjectInputStream inFromClient;
	private Player player;
	private GameData gameData;
	private ServerGameScene serverScene;

	public InFromClientNetCom(Socket socket, InFromClientListener iFCL, GameData gameData, ServerGameScene serverScene) {
		welcomeSocket = socket;
		System.out.println("infromclientstarted");
		this.iFCL = iFCL;
		this.gameData = gameData;
		this.serverScene = serverScene;

	}

	/*
	 * Recieves a String command/request from a client proccess the string and
	 * acts accordingly
	 */
	@Override
	public void run() {
		try {
			inFromClient = new ObjectInputStream(welcomeSocket.getInputStream());
			welcomeSocket.setTcpNoDelay(true);
			while (!interrupted()) {

				Object obj = inFromClient.readUnshared();
				if (obj instanceof PlayerControlCommands) {
					PlayerControlCommands recieved = (PlayerControlCommands) obj;
					Player temp = gameData.getPlayerWithID(player.getId());
					temp.setMovementX(recieved.getMovementX());
					temp.setMovementY(recieved.getMovementY());
					temp.setDirection(recieved.getDirection());

					System.out.println(" [" + System.currentTimeMillis() + "] Player : " + player.getId() + " " + recieved.getMovementX() + " " + recieved.getMovementY() + " " + recieved.getDirection());
				} else if (obj instanceof Player) {
					Player tempPlayer = (Player) obj;
					if (gameData.getPlayerWithID(tempPlayer.getId()) == null) {
						serverScene.addPlayerToGameDataObj(tempPlayer);
						
					}
					this.player = tempPlayer; //the player this is connection is to
					
				}

			}

		} catch (IOException ex) {
			closeThisConnection();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void closeThisConnection() {
		try {
			welcomeSocket.close();
			this.interrupt();
		} catch (IOException ex1) {
			Logger.getLogger(InFromClientNetCom.class.getName()).log(Level.SEVERE, null, ex1);
		}
	}
}
