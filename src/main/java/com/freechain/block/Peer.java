package com.freechain.block;

import java.net.URI;

/**
 * 节点
 * @author aaron.rao
 *
 */
public class Peer {
	
	private URI address;
	
	public Peer() {
	}
	
	public Peer(URI address) {
		super();
		this.address = address;
	}

	public URI getAddress() {
		return address;
	}

	public void setAddress(URI address) {
		this.address = address;
	}

}
