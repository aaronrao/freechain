package com.freechain.block;

import java.util.List;

/**
 * 区块
 * @author aaron.rao
 *
 */
public class Block {
	
	private int index;
	private long timestamp;
	/**
	 * 交易
	 */
	private List<Transaction> transactions;
	/**
	 * 工作量证明111
	 */
	private int proof;
	private String previousHash;
	
	public Block(int index, long timestamp, List<Transaction> transactions,
			int proof, String previousHash) {
		super();
		this.index = index;
		this.timestamp = timestamp;
		this.transactions = transactions;
		this.proof = proof;
		this.previousHash = previousHash;
	}
	public int getIndex() {
		return index;
	}
	public void setIndex(int index) {
		this.index = index;
	}
	public long getTimestamp() {
		return timestamp;
	}
	public void setTimestamp(long timestamp) {
		this.timestamp = timestamp;
	}
	public List<Transaction> getTransactions() {
		return transactions;
	}
	public void setTransactions(List<Transaction> transactions) {
		this.transactions = transactions;
	}
	public int getProof() {
		return proof;
	}
	public void setProof(int proof) {
		this.proof = proof;
	}
	public String getPreviousHash() {
		return previousHash;
	}
	public void setPreviousHash(String previousHash) {
		this.previousHash = previousHash;
	}
	
}
