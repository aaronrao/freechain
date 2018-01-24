package com.freechain.block;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import org.eclipse.jetty.util.StringUtil;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

/**
 * 区块链
 * @author aaron.rao
 *
 */
public class BlockChain {
	
    private List<Transaction> currentTransactions = new ArrayList<>();
    private List<Block> chain = new ArrayList<Block>();
    private Set<Peer> peers = new HashSet<Peer>();
    private String peerId;

    public String getPeerId() {
	return peerId;
    }

    public void setPeerId(String peerId) {
	this.peerId = peerId;
    }
	
    public Block getLastBlock() {
	return chain.size() > 0 ? chain.get(chain.size() - 1) : null;
    }

    public BlockChain() {
        peerId = UUID.randomUUID().toString().replace("-", "");
        createNewBlock(100, "1"); //genesis block
    }

    //private functionality
    private void registerPeer(String address)
    {
        try {
			peers.add(new Peer(new URI(address)));
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}
    }

    private boolean isValidChain(List<Block> chain)
    {
        Block block = null;
        Block lastBlock = chain.get(0);
        int currentIndex = 1;
        while (currentIndex < chain.size())
        {
            block = chain.get(currentIndex);
            System.out.println("lastBlock:" + lastBlock);
            System.out.println("block:" + block);
            System.out.println("----------------------------");

            //Check that the hash of the block is correct
            if (block.getPreviousHash() != getHash(lastBlock))
                return false;

            //Check that the Proof of Work is correct
            if (!isValidProof(lastBlock.getProof(), block.getProof(), lastBlock.getPreviousHash()))
                return false;

            lastBlock = block;
            currentIndex++;
        }

        return true;
    }

    private boolean resolveConflicts()
    {
        List<Block> newChain = null;
        int maxLength = chain.size();

        for (Peer peer : peers) {
            JSONObject parseObject = JSON.parseObject(HttpRequest.sendGet(peer.getAddress().toString() + "/chain", ""));
            List<Block> reqChain = (List<Block>)parseObject.get("chain");
            if (reqChain.size() > chain.size() && isValidChain(reqChain)) {
                maxLength = reqChain.size();
                newChain = reqChain;
            }
        }

        if (newChain != null)
        {
            chain = newChain;
            return true;
        }

        return false;
    }

    private Block createNewBlock(int proof, String previousHash)
    {
    	Block block = new Block(chain.size() + 1, System.currentTimeMillis(), new ArrayList(currentTransactions), proof, StringUtil.isNotBlank(previousHash) ? previousHash : getHash(chain.get(chain.size() - 1)));
    	currentTransactions.clear();
        chain.add(block);
        return block;
    }

    private int createProofOfWork(int lastProof, String previousHash)
    {
        int proof = 0;
        while (!isValidProof(lastProof, proof, previousHash))
            proof++;

        return proof;
    }

    private boolean isValidProof(int lastProof, int proof, String previousHash)
    {
        String guess = lastProof + proof + previousHash;
        String result = CryptoUtil.getSHA256(guess);
        return result.startsWith("0000");
    }

    private String getHash(Block block) {
        return CryptoUtil.getSHA256(JSON.toJSONString(block));
    }

    //web server calls
    public String mine() {
    	Block lastBlock = getLastBlock();
        int proof = createProofOfWork(lastBlock.getProof(), lastBlock.getPreviousHash());

        createTransaction("0", peerId, 1);
        Block block = createNewBlock(proof, null);

        JSONObject result = new JSONObject();
        result.put("message", "New Block Forged");
        result.put("index", block.getIndex());
        result.put("transactions", block.getTransactions());
        result.put("proof", block.getProof());
        result.put("previousHash", block.getPreviousHash());
        return result.toJSONString();
    }

    public String getFullChain() {
    	JSONObject result = new JSONObject();
    	result.put("chain", chain);
        result.put("length", chain.size());
        return result.toJSONString();
    }

    public String registerPeers(String[] Peers)
    {
    	StringBuilder builder = new StringBuilder();
        for(String peer : Peers) {
            String url = "http://" + peer;
            registerPeer(url);
            builder.append(url + ", ");
        }

        builder.insert(0, Peers.length + "new Peers have been added: ");
        String result = builder.toString();
        return result.substring(0, result.length() - 2);
    }

    public String consensus()
    {
        boolean replaced = resolveConflicts();
        String message = replaced ? "was replaced" : "is authoritive";
        
        JSONObject result = new JSONObject();
        result.put("message", "Our chain " + message);
    	result.put("chain", chain);
        return result.toJSONString();
    }

    public int createTransaction(String sender, String recipient, int amount)
    {
    	Transaction transaction = new Transaction(sender, recipient, amount);
        currentTransactions.add(transaction);
        Block lastBlock = getLastBlock();
        return lastBlock != null ? lastBlock.getIndex() + 1 : 0;
    }

}
