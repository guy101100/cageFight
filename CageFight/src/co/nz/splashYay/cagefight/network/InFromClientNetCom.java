package co.nz.splashYay.cagefight.network;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

import co.nz.splashYay.cagefight.GameData;
import co.nz.splashYay.cagefight.PlayerControlCommands;
import co.nz.splashYay.cagefight.Team.ALL_TEAMS;
import co.nz.splashYay.cagefight.entities.Player;
import co.nz.splashYay.cagefight.scenes.ServerGameScene;


public class InFromClientNetCom extends Thread {

	//String command;
	private Socket welcomeSocket;
	private InFromClientListener iFCL;
	private ObjectInputStream inFromClient;
	private ObjectOutputStream outToClient;
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
			outToClient = new ObjectOutputStream(welcomeSocket.getOutputStream());
			welcomeSocket.setTcpNoDelay(true);
			
			Player newPlayer = new Player("", gameData.getUnusedID(), 100, 1, gameData.getTeam(ALL_TEAMS.EVIL).getSpawnXpos(), gameData.getTeam(ALL_TEAMS.EVIL).getSpawnXpos(), ALL_TEAMS.GOOD);
			this.player = newPlayer; //the player this is connection is to
			serverScene.addEntityToGameDataObj(newPlayer);
			outToClient.writeObject(newPlayer);
			
			
			while (!interrupted()) {

				Object obj = inFromClient.readUnshared();
				if (obj instanceof PlayerControlCommands) {
					PlayerControlCommands receivedCommands = (PlayerControlCommands) obj;
					Player tempPlayer = (Player)gameData.getEntityWithId(player.getId());
					tempPlayer.setMovementX(receivedCommands.getMovementX());
					tempPlayer.setMovementY(receivedCommands.getMovementY());
					tempPlayer.setAttackCommand(receivedCommands.isAttackCommand());
					tempPlayer.setTarget((Player)gameData.getEntityWithId(receivedCommands.getTargetID()));
					
					
					//System.out.println(" [" + System.currentTimeMillis() + "] Player : " + player.getId() + " " + recieved.getMovementX() + " " + recieved.getMovementY() + " " + recieved.getDirection());
				} else {
					System.err.println("error reciveving");
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
