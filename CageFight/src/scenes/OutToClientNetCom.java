package scenes;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

import co.nz.splashYay.cagefight.GameData;

public class OutToClientNetCom extends Thread {
	private Socket welcomeSocket;
	private ObjectOutputStream outToClient;
	private ObjectInputStream inFromCLient;
	private OutToClientListener oTCL;
	private long timeConnected;
	private GameData gameData;
	private ServerGameScene serverScene;

	boolean sendUpdates = false;

	public OutToClientNetCom(Socket connectionSocket, OutToClientListener oTCL, GameData gameData, ServerGameScene serverScene) {
		this.oTCL = oTCL;
		this.gameData = gameData;
		this.serverScene = serverScene;
		welcomeSocket = connectionSocket;
		System.out.println("client connected");

	}

	@Override
	public void run() {

		try {
			welcomeSocket.setTcpNoDelay(true);
			String returnSentance;
			String clientSentenceArray[];
			outToClient = new ObjectOutputStream(welcomeSocket.getOutputStream());
			inFromCLient = new ObjectInputStream(welcomeSocket.getInputStream());
			oTCL.addClient(this);

		} catch (IOException ex) {
			System.err.println("==================== OutToClient Error");

		}

	}

	public long getTimeConnected() {
		return timeConnected;
	}

	public void updateClient() {

		try {
			GameData temp = gameData;

			outToClient.writeObject(temp);
			outToClient.reset();
			System.out.println(" [" + System.currentTimeMillis() + "] Sent : data..... ??");
		} catch (IOException ex) {
			oTCL.removeClient(this);
			try {
				welcomeSocket.close();
				System.out.println("SOCKET CLOSED");
			} catch (IOException ex1) {

				System.out.println("I DONT KNOW IF THIS WILL EVER HAPPEN");
			}
		}

	}
}
