package co.nz.splashYay.cagefight.network;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.HashMap;
import java.util.HashSet;

import co.nz.splashYay.cagefight.GameData;

public class UDPServer extends Thread {

	private DatagramSocket socket = null;
	private DatagramPacket outPacket = null;

	private ByteArrayOutputStream bStream;
	private ObjectOutput oo;

	private byte[] outObj;
	private final int PORT = 6789;
	private GameData gameData;
	
	
	private HashSet<Client> clients;

	public UDPServer(GameData gameData)  {
		this.gameData = gameData;
		clients = new HashSet<Client>();
	}
	
	public void addClient(Client client){
		clients.add(client);
	}
	public void removeClient(Client client){
		clients.remove(client);
	}

	@Override
	public void run() {
		try {
			socket = new DatagramSocket(PORT);
						

			long counter = 0;
			String msg;

			while (true) {
				long start = System.currentTimeMillis();
				for (Object clientObj : clients.toArray().clone()) {
					Client client = (Client) clientObj;
					
					gameData.setSendTime(System.currentTimeMillis());
					bStream = new ByteArrayOutputStream();
					oo = new ObjectOutputStream(bStream);					
					oo.writeObject(gameData);

					outObj = bStream.toByteArray();

					outPacket = new DatagramPacket(outObj, outObj.length, client.inetAddress, client.port);
					socket.send(outPacket);
					//System.out.println("sent to : " + client.getInetAddress().toString());

				}
				
				// System.out.println(System.currentTimeMillis() - start);
				try {
					Thread.sleep(15);
				} catch (InterruptedException ie) {
				}
			}
		} catch (IOException ioe) {
			System.out.println(ioe);
		}

	}

}
