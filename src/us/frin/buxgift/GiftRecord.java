package us.frin.buxgift;

public class GiftRecord {
	public int amount;
	public int duration;
	public String duration_type;
	public boolean isValid;
	
	public GiftRecord(int amount, int duration, String duration_type) {
		this.amount = amount;
		this.duration = duration;
		this.duration_type = duration_type;
		this.isValid = false;
	}
}
