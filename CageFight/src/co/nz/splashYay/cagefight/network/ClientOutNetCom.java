package co.nz.splashYay.cagefight.network;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.UnknownHostException;

import co.nz.splashYay.cagefight.Player;
import co.nz.splashYay.cagefight.PlayerControlCommands;
import co.nz.splashYay.cagefight.SceneManager;

public class ClientOutNetCom extends Thread{

	private String ipAddress;
	private ObjectOutputStream outToServer;	
	private Socket clientSocket;
	private Object sceneManager;

	public ClientOutNetCom(String ipAddress, SceneManager sceneManager) {	
		this.ipAddress = ipAddress;
		this.sceneManager = sceneManager;
	}
	
	
	
	
	
	@Override
	public void run() {
		try {
			clientSocket = new Socket(ipAddress, 6788);			
			outToServer = new ObjectOutputStream(clientSocket.getOutputStream());
			clientSocket.setTcpNoDelay(true);			
			outToServer.flush();			
			outToServer.writeObject(((SceneManager) sceneManager).getPlayer());
			outToServer.reset();
			
			//sleep(1000);

			

		} catch (UnknownHostException ex) {
			System.out.println("Unknown Host");
		} catch (IOException ex) {
			System.out.println("IO Error");
		} 

	}
	
	public void sendToServer(PlayerControlCommands cmds){
		try {
			outToServer.flush();
			outToServer.writeObject(cmds);
			outToServer.reset();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			System.err.println("error sending");
		}
		
		
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
}
