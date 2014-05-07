package co.nz.splashYay.cagefight.network;

import java.util.HashSet;

import co.nz.splashYay.cagefight.GameData;
import co.nz.splashYay.cagefight.scenes.ServerGameScene;

public class ClientUpdater extends Thread {
	private HashSet<OutToClientNetCom> clients;
	private GameData gameData;
	private ServerGameScene serverScene;
	
	
	public ClientUpdater(GameData gameData, ServerGameScene serverScene) {
		
		this.clients = new HashSet<OutToClientNetCom>();
		this.gameData = gameData;
		this.serverScene = serverScene;
	}
	
	@Override
	public void run() {
		while(true){
			for (OutToClientNetCom onc : clients) {
				onc.updateClient();
			}	
			try {
				sleep(50);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
	}
	
	public void removeClient(OutToClientNetCom netCom) {
		clients.remove(netCom);
	}

	public void addClient(OutToClientNetCom netCom) {
		if (!clients.contains(netCom)) {
			clients.add(netCom);
		}

	}
	
	public void updateClients() {		
			
	}

	
	
	
	
	
}
