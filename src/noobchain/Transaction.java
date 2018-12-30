package noobchain;

import java.security.*;
import java.util.ArrayList;

public class Transaction {

	public String transactionId; //this is also the hash of the transaction
	public PublicKey sender; //Sender public key / address
	public PublicKey recipient; //Recipient public key / address
	public float value;
	public byte[] signature; // this is to prevent anybody else from spending funds in our wallet
	
	public ArrayList<TransactionInput> inputs = new ArrayList<>();
	public ArrayList<TransactionOutput> outputs = new ArrayList<>();
	
	private static int sequence = 0; //a rough count of how many transactions have been generated
	
	// Constructor:
	public Transaction(PublicKey from, PublicKey to, float value, ArrayList<TransactionInput> inputs) {
		this.sender = from;
		this.recipient = to;
		this.value = value;
		this.inputs = inputs;
	}
	
	//This Calculates the transaction hash (which will be used as its Id)
	private String calculateHash() {
		sequence++; //increase the sequence to avoid 2 identical transactions having the same hash
		return StringUtil.applySHA256(
				StringUtil.getStringFromKey(sender)
				+ StringUtil.getStringFromKey(recipient)
				+ Float.toString(value)
				+ sequence
				);
	}
	
	//Signs all the data we dont wish to be tampered with
	public void generateSignature(PrivateKey privateKey) {
		String data = StringUtil.getStringFromKey(sender)
				+ StringUtil.getStringFromKey(recipient)
				+ Float.toString(value);
		signature = StringUtil.applyECDSASignature(privateKey, data);
	}
	
	//Verifies the data we signed hasnt been tampered with
	public boolean verifySignature() {
		String data = StringUtil.getStringFromKey(sender)
				+ StringUtil.getStringFromKey(recipient)
				+ Float.toString(value);
		return StringUtil.verifyECDSASig(sender, data, signature);
	}
		
	public boolean processTransaction() {
		if(!verifySignature()) {
			System.out.println("#Transaction Signature failed to verify");
			return false;
		}
		
		//gather transaction inputs (Make sure they are unspent):
		for(TransactionInput i  : inputs)
		{
			i.UTXO = NoobChain2.UTXOs.get(i.transactionOutputId);
		}
		
		//check if transaction is valid:
		if(getInputValues() < NoobChain2.minimumTransaction) {
			System.out.println("#Transaction Inputs to small: " + getInputValues());
			return false;
		}
		
		//generate transaction outputs:
		float leftOver = getInputValues() - value; //get value  of inputs then the left over change:
		transactionId = calculateHash();
		outputs.add(new TransactionOutput(this.recipient, value, transactionId));
		outputs.add(new TransactionOutput(this.sender, leftOver, transactionId));
		
		//add outputs to Unspent list
		for(TransactionOutput o : outputs) {
			NoobChain2.UTXOs.put(o.id, o);
		}
		
		//remove transactions inputs from UTXO lists as spent:
		for(TransactionInput i : inputs) {
			if(i.UTXO == null) continue; //If Transaction can't be found skip it
			NoobChain2.UTXOs.remove(i.UTXO.id);
		}
		return true;
	}
	
	//returns sum of inputs(UTXOs) values
	public float getInputValues() {
		float total = 0;
		for(TransactionInput i : inputs) {
			if(i.UTXO == null) continue; //If Transaction can't be found skip it
			total += i.UTXO.value;
		}
		return total;
	}
	
	//returns sum of outputs:
	public float getOutputsValue() {
		float total = 0;
		for(TransactionOutput o : outputs) {
			total += o.value;
		}
		return total;
	}
}
