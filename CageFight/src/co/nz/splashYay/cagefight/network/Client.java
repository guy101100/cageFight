package co.nz.splashYay.cagefight.network;

import java.net.InetAddress;

public class Client {
	InetAddress inetAddress;
	int port;
	long lastRecievedFrom;
	public Client(InetAddress inetAddress, int port) {		
		this.inetAddress = inetAddress;
		this.port = port;
		this.lastRecievedFrom = 0;
	}
	public InetAddress getInetAddress() {
		return inetAddress;
	}
	public void setInetAddress(InetAddress inetAddress) {
		this.inetAddress = inetAddress;
	}
	public int getPort() {
		return port;
	}
	public void setPort(int port) {
		this.port = port;
	}
	public long getLastRecievedFrom() {
		return lastRecievedFrom;
	}
	public void setLastRecievedFrom(long lastRecievedFrom) {
		this.lastRecievedFrom = lastRecievedFrom;
	}
	
	
	
	
	
	
	
}
