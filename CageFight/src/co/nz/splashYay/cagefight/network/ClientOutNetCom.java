package co.nz.splashYay.cagefight.network;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.UnknownHostException;

import co.nz.splashYay.cagefight.PlayerControlCommands;
import co.nz.splashYay.cagefight.SceneManager;
import co.nz.splashYay.cagefight.entities.Player;
import co.nz.splashYay.cagefight.scenes.ClientGameScene;

public class ClientOutNetCom extends Thread{

	private String ipAddress;
	private ObjectOutputStream outToServer;	
	private Socket clientSocket;
	private SceneManager sceneManager;
	private ObjectInputStream inFromServer;
	private ClientGameScene cGS;

	public ClientOutNetCom(String ipAddress, SceneManager sceneManager, ClientGameScene cGS) {	
		this.ipAddress = ipAddress;
		this.sceneManager = sceneManager;
		this.cGS = cGS;
	}
	
	
	
	
	
	@Override
	public void run() {
		try {
			clientSocket = new Socket(ipAddress, 6788);			
			outToServer = new ObjectOutputStream(clientSocket.getOutputStream());
			inFromServer = new ObjectInputStream(clientSocket.getInputStream());
			clientSocket.setTcpNoDelay(true);			
			outToServer.flush();			
			//outToServer.writeObject(((SceneManager) sceneManager).getPlayer());
			Player player = (Player) inFromServer.readUnshared();
			sceneManager.setPlayer(player);
			sceneManager.setGameStarted(true);
			
			outToServer.reset();
			
			while (true) {
				sendToServer();
				sleep(30);
			}
						

		} catch (UnknownHostException ex) {
			System.out.println("Unknown Host");
		} catch (IOException ex) {
			System.out.println("ex");
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 

	}
	
	public void sendToServer(){
		if (sceneManager.isGameStarted()) {
			
			try {
				outToServer.flush();
				outToServer.writeObject(cGS.getPlayerCommands());
				//System.out.println("[" + System.currentTimeMillis() + "] Send : " + cmds.getMovementX() + " " + cmds.getMovementY());
				outToServer.reset();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				System.err.println("error sending");
			}
		}
		
		
		
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
}
