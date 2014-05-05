package co.nz.splashYay.cagefight.network;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;


import co.nz.splashYay.cagefight.GameData;
import co.nz.splashYay.cagefight.entities.Base;
import co.nz.splashYay.cagefight.entities.Entity;
import co.nz.splashYay.cagefight.entities.Player;
import co.nz.splashYay.cagefight.scenes.ClientGameScene;



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
					System.out.println("[" + System.currentTimeMillis() + "] recieved data");
					gameData.getTeam1().updateFromOtherTeamData(gameDataIn.getTeam1());
					gameData.getTeam2().updateFromOtherTeamData(gameDataIn.getTeam2());
					
					
					for (Entry<Integer, Entity> entry: gameDataIn.getEntities().entrySet()) {					    
					    Entity entityIn = entry.getValue();
						if (!gameData.getEntities().containsKey(entry.getKey())) {
					        clientGameScene.addEntityToGameDataObj(entityIn);
					    } else {
					    	if (entityIn instanceof Player) {
					    		Player playerIn = (Player) entityIn;
					    		Player actual = (Player)gameData.getEntityWithId(playerIn.getId());
						    	actual.updatePlayerInfoFromOtherPlayerData(playerIn);
					    	
					    	} else if (entityIn instanceof Base) {
					    		//update bases info
					    		
					    	}
					    	
					    }
					}
					
					
					
					
					

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
