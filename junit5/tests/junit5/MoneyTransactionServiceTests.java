package junit5;

import static org.junit.Assert.assertThrows;
import static org.junit.jupiter.api.Assertions.*;

import java.time.Duration;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.condition.DisabledOnOs;
import org.junit.runner.RunWith;
import org.junit.platform.runner.JUnitPlatform;
import org.junit.platform.suite.api.SelectPackages;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.condition.OS;


@RunWith(JUnitPlatform.class)
@SelectPackages("junit5")


@TestMethodOrder(OrderAnnotation.class)

// @DisabledOnOs(OS.LINUX) //Applies to class, too
@DisplayName("Money transaction service test")
class MoneyTransactionServiceTests {
	private static final double RANDOM_MONEY_AMOUNT = 100;
	private static final double ZERO_MONEY_AMOUNT = 0;
	private static final Object ILLEGAL_ARG_MSG = "There is a null account";
	private static final Object NEGATIVE_MONEY_MSG = "Money cannot be negative nor 0";
	private static final double NEGATIVE_MONEY_AMOUNT = -10;
	
	private MoneyTransactionService testInstance;
	
	@BeforeEach
	void setUp() {
		testInstance = new MoneyTransactionService();
	}
	
	@DisplayName("Verify transaction between two accounts")
	@Test
	@Order(6)
	void shouldTransferMoney() {
		var from = new Account(RANDOM_MONEY_AMOUNT);
		var to = new Account(ZERO_MONEY_AMOUNT);
		
		try {
			testInstance.transferMoney(from, to, RANDOM_MONEY_AMOUNT);
		} catch (NotEnoughMoneyException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		assertEquals(to.getTotalMoney(), RANDOM_MONEY_AMOUNT);
		assertEquals(from.getTotalMoney(), ZERO_MONEY_AMOUNT);
	}
	
	@DisplayName("Verify no null account can send money")
	@Test
	@Order(1)
	void shouldThrowExceptionIfFromIsNull() {
		Account from = null;
		Account to = new Account(ZERO_MONEY_AMOUNT);
		
		var exception = assertThrows(IllegalArgumentException.class, () -> 
			testInstance.transferMoney(from, to, RANDOM_MONEY_AMOUNT)
		);
		
		assertEquals(exception.getMessage(), ILLEGAL_ARG_MSG);
	}
	
	
	@DisplayName("Verify no null account can receive money")
	@Test
	@Order(2)
	void shouldThrowExceptionIfToIsNull() {
		Account from = new Account(RANDOM_MONEY_AMOUNT);
		Account to = null;
		
		var exception = assertThrows(IllegalArgumentException.class, () -> 
			testInstance.transferMoney(from, to, RANDOM_MONEY_AMOUNT)
		);
		
		assertEquals(exception.getMessage(), ILLEGAL_ARG_MSG);
	}
	
	
	@DisplayName("Verify no negative amount can be send")
	@Test
	@Order(3)
	void shouldThrowExceptionIfMoneyIsNegative() {
		Account to = new Account(ZERO_MONEY_AMOUNT);
		Account from = new Account(RANDOM_MONEY_AMOUNT);
		
		var exception = assertThrows(IllegalArgumentException.class, () -> 
			testInstance.transferMoney(from, to, NEGATIVE_MONEY_AMOUNT)
		);
		
		assertEquals(exception.getMessage(), NEGATIVE_MONEY_MSG);
	}
	
	
	@DisplayName("Verify amount is greater than 0")
	@Test
	@Order(4)
	void shouldThrowExceptionIfMoneyIsZero() {
		Account to = new Account(ZERO_MONEY_AMOUNT);
		Account from = new Account(RANDOM_MONEY_AMOUNT);
		
		var exception = assertThrows(IllegalArgumentException.class, () -> 
			testInstance.transferMoney(from, to, ZERO_MONEY_AMOUNT)
		);
		
		assertEquals(exception.getMessage(), NEGATIVE_MONEY_MSG);
	}
	
	
	@DisplayName("Verify money is less than available")
	@Test
	@Order(5)
	void shouldThrowExceptionIfMoneyIsGreaterThanAvailable() {
		Account to = new Account(ZERO_MONEY_AMOUNT);
		Account from = new Account(ZERO_MONEY_AMOUNT);
		
		assertThrows(NotEnoughMoneyException.class, () -> 
			testInstance.transferMoney(from, to, RANDOM_MONEY_AMOUNT)
		);
	}
	
	
	@Test
	@Order(7)
	void assertAmountsChangeCorrectly() {
		Account to = new Account(ZERO_MONEY_AMOUNT);
		Account from = new Account(RANDOM_MONEY_AMOUNT);
		
		try {
			testInstance.transferMoney(from, to, RANDOM_MONEY_AMOUNT);
		} catch (NotEnoughMoneyException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		assertAll( "money transaction",
				() -> assertEquals(from.getTotalMoney(), ZERO_MONEY_AMOUNT),
				() -> assertEquals(to.getTotalMoney(), RANDOM_MONEY_AMOUNT)
				);
	}
	
	@DisplayName("Assert the duration of a transaction is not longer than one second")
	@Test
	@Order(8)
	@DisabledOnOs(OS.LINUX)
	void assertTransactionTime() {
		Account to = new Account(ZERO_MONEY_AMOUNT);
		Account from = new Account(RANDOM_MONEY_AMOUNT);
		
		try {
			testInstance.transferMoney(from, to, RANDOM_MONEY_AMOUNT);
		} catch (NotEnoughMoneyException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		assertTimeout(Duration.ofSeconds(1), () -> testInstance.transferMoney(from, to, RANDOM_MONEY_AMOUNT));
		
		assertAll( "money transaction",
				() -> assertEquals(from.getTotalMoney(), ZERO_MONEY_AMOUNT),
				() -> assertEquals(to.getTotalMoney(), RANDOM_MONEY_AMOUNT)
				);
	}

}
