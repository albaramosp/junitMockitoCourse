package junit5;

public class MoneyTransactionService {
	public boolean transferMoney(Account from, Account to, double money) throws NotEnoughMoneyException {
		if (from == null || to == null) {
			throw new IllegalArgumentException("There is a null account");
		}
		
		if (money <= 0) {
			throw new IllegalArgumentException("Money cannot be negative nor 0");
		}
		
		if (from.getTotalMoney() < money) {
			throw new NotEnoughMoneyException();
		}
		
		from.setTotalMoney(from.getTotalMoney() - money);
		to.setTotalMoney(to.getTotalMoney() + money);
		
		return true;
	}

}
