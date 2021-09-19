package junit5;

public class Account {
	private double totalMoney;
	
	public Account() {
		
	}
	
	public Account(double totalMoney) {
		this.totalMoney = totalMoney;
	}
	
	public double getTotalMoney() {
		return this.totalMoney;
	}
	
	public void setTotalMoney(double totalMoney) {
		this.totalMoney = totalMoney;
	}
	

}
