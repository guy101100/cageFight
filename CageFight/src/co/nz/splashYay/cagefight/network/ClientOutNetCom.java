package co.nz.splashYay.cagefight.network;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.UnknownHostException;

import co.nz.splashYay.cagefight.PlayerControlCommands;
import co.nz.splashYay.cagefight.SceneManager;
import co.nz.splashYay.cagefight.entities.Player;

public class ClientOutNetCom extends Thread{

	private String ipAddress;
	private ObjectOutputStream outToServer;	
	private Socket clientSocket;
	private SceneManager sceneManager;
	private ObjectInputStream inFromServer;

	public ClientOutNetCom(String ipAddress, SceneManager sceneManager) {	
		this.ipAddress = ipAddress;
		this.sceneManager = sceneManager;
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
						

		} catch (UnknownHostException ex) {
			System.out.println("Unknown Host");
		} catch (IOException ex) {
			System.out.println("IO Error");
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 

	}
	
	public void sendToServer(PlayerControlCommands cmds){
		if (sceneManager.isGameStarted()) {
			try {
				outToServer.flush();
				outToServer.writeObject(cmds);
				System.out.println("[" + System.currentTimeMillis() + "] Send : " + cmds.getMovementX() + " " + cmds.getMovementY());
				outToServer.reset();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				System.err.println("error sending");
			}
		}
		
		
		
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
}
