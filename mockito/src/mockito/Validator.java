package mockito;

public class Validator {
	public void validate(Email email) {
		if (email.getTo() == null) {
			throw new IllegalArgumentException();
		}
	}
}
