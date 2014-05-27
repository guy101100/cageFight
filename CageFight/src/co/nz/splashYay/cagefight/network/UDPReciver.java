package co.nz.splashYay.cagefight.network;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.OptionalDataException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Map.Entry;

import co.nz.splashYay.cagefight.GameData;
import co.nz.splashYay.cagefight.Team.ALL_TEAMS;
import co.nz.splashYay.cagefight.entities.Base;
import co.nz.splashYay.cagefight.entities.Creep;
import co.nz.splashYay.cagefight.entities.Entity;
import co.nz.splashYay.cagefight.entities.Player;
import co.nz.splashYay.cagefight.entities.Tower;
import co.nz.splashYay.cagefight.scenes.ClientGameScene;

public class UDPReciver extends Thread {

	DatagramSocket socket = null;
	DatagramPacket inPacket = null;
	DatagramPacket outPacket = null;
	byte[] inBuf;

	int port = 6787;
	InetAddress address;

	private ClientGameScene clientGameScene;
	private GameData gameData;

	public UDPReciver(ClientGameScene clientGameScene, String ipaddress, GameData gameData) {
		this.clientGameScene = clientGameScene;
		this.gameData = gameData;
		try {
			socket = new DatagramSocket(6790);
			address = InetAddress.getByName(ipaddress);

		} catch (SocketException e) {
			e.printStackTrace();
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void run() {
		try {

			inBuf = new byte[4096];

			while (true) {				
				inPacket = new DatagramPacket(inBuf, inBuf.length);
				socket.receive(inPacket);
				processPacket(inPacket);

			}
		} catch (IOException ioe) {
			System.out.println(ioe);
		}

	}

	private void processPacket(DatagramPacket packet){
		
		
	    try {	    	
	    	ByteArrayInputStream baos = new ByteArrayInputStream(packet.getData());	    	
		    ObjectInputStream oos = new ObjectInputStream(baos);		    
	    	GameData gameDataIn = (GameData)oos.readUnshared();	    	
	    	baos.close();
	    	oos.close();
	    	
	    	
	    	
	    	if (gameDataIn.getSendTime() > gameData.getSendTime()) { // if new send time is greater than gameDatas send time, the the data is new, if not data is old and should be ignored.
	    		//System.out.println("[" + System.currentTimeMillis() + "] recieved data [" + (gameData.getSendTime() - gameDataIn.getSendTime()) + "]");
	    		gameData.setSendTime(gameDataIn.getSendTime());
	    		gameData.getTeam(ALL_TEAMS.GOOD).updateFromOtherTeamData(gameDataIn.getTeam(ALL_TEAMS.GOOD));
				gameData.getTeam(ALL_TEAMS.BAD).updateFromOtherTeamData(gameDataIn.getTeam(ALL_TEAMS.BAD));
				
				
				for (Entry<Integer, Entity> entry: gameDataIn.getEntities().entrySet()) {					    
				    Entity entityIn = entry.getValue();
					if (!gameData.getEntities().containsKey(entry.getKey())) {
				        clientGameScene.addEntityToGameDataObj(entityIn);
				    } else {
				    	if (entityIn instanceof Player) {
				    		Player playerIn = (Player) entityIn;
				    		Player actual = (Player)gameData.getEntityWithId(playerIn.getId());
					    	actual.updateFromServer(playerIn);
				    	
				    	} else if (entityIn instanceof Base) {
				    		Base baseIn = (Base) entityIn;
				    		Base actual = (Base)gameData.getEntityWithId(baseIn.getId());
					    	actual.updateFromServer(baseIn);
				    		
				    	} else if (entityIn instanceof Tower) {
				    		Tower towerIn = (Tower) entityIn;
				    		Tower actual = (Tower)gameData.getEntityWithId(towerIn.getId());
					    	actual.updateFromServer(towerIn);
				    	} else if (entityIn instanceof Creep) {
				    		//update AI info
				    		Creep aiUnitIn = (Creep) entityIn;
				    		Creep actual = (Creep)gameData.getEntityWithId(aiUnitIn.getId());
					    	actual.updateFromServer(aiUnitIn);
				    	}
				    	
				    }
				}
	    	} else {
	    		//System.out.println("[" + System.currentTimeMillis() + "] recieved old data");
	    	}
	    	
	    	
			
	    	
		} catch (OptionalDataException e) {			
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {			
			e.printStackTrace();
		}
		
		
		
	}

}
