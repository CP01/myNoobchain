package noobchain;

import java.util.ArrayList;

import com.google.gson.GsonBuilder;

public class NoobChain {

	public static int difficulty = 5;
	public static ArrayList<Block> blockchain = new ArrayList<>();
	
	public static void main(String[] args) {
/*
		Block genesisBlock = new Block("First Block", "0");
		System.out.println("Hash for block 1 : " + genesisBlock.hash);
		
		Block secondBlock = new Block("Second Block", genesisBlock.hash);
		System.out.println("Hash for block 2 : " + secondBlock.hash);
		
		Block thirdBlock = new Block("Third Block", secondBlock.hash);
		System.out.println("Hash for block 3 : " + thirdBlock.hash);
*/
/*		
		blockchain.add(new Block("First Block", "0"));
		blockchain.add(new Block("Second Block", blockchain.get(blockchain.size()-1).hash));
		blockchain.add(new Block("Third Block", blockchain.get(blockchain.size()-1).hash));
*/		
		
		blockchain.add(new Block("First Block", "0"));
		System.out.println("Trying to Mine block 1");
		blockchain.get(0).mineBlock(difficulty);
		
		blockchain.add(new Block("Second Block", blockchain.get(blockchain.size()-1).hash));
		System.out.println("Trying to Mine block 2");
		blockchain.get(1).mineBlock(difficulty);
		
		blockchain.add(new Block("Third Block", blockchain.get(blockchain.size()-1).hash));
		System.out.println("Trying to Mine block 3");
		blockchain.get(2).mineBlock(difficulty);

		System.out.println("\nBlockchain is Valid : "+isValidChain());
		
		String blockchainJSON = new GsonBuilder().setPrettyPrinting().create().toJson(blockchain);
		System.out.println("\nThe block chain: ");
		System.out.println(blockchainJSON);
	}
	
	public static Boolean isValidChain() {
		Block currentBlock;
		Block previousBlock;
		String hashTarget = new String(new char[difficulty]).replace('\0', '0');
		
		//loop through blockchain to check hashes:
		for(int i=1; i<blockchain.size(); i++)
		{
			currentBlock = blockchain.get(i);
			previousBlock = blockchain.get(i-1);
			
			//compare registered hash and calculated hash:
			if(!currentBlock.hash.equals(currentBlock.calculateHash())) {
				System.out.println("Current Hashes not equal");
				return false;
			}
			
			//compare previous hash and registered previous hash
			if(!previousBlock.hash.equals(currentBlock.previousHash)) {
				System.out.println("Previous Hashes not equal");
				return false;
			}
			
			//check if hash is solved
			if(!currentBlock.hash.substring(0, difficulty).equals(hashTarget)) {
				System.out.println("This block hasn't been mined");
				return false;
			}
		}
		return true;
	}

}
