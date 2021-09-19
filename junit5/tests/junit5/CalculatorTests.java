package junit5;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class CalculatorTests {

	@Test
	void test() {
		var calculator = new Calculator();
		int actual = calculator.add(5, 3);
		
		assertEquals(8, actual);
	}

}
