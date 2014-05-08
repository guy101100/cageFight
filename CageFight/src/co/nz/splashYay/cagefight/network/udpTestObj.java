package co.nz.splashYay.cagefight.network;

import java.io.Serializable;

public class udpTestObj implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	String lol;
	int num;
	public udpTestObj(String lol, int num) {
		super();
		this.lol = lol;
		this.num = num;
	}
	
	
}
