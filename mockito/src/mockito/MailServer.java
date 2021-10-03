package mockito;

public class MailServer {
	private Validator validator;
	
	public void sendEmail(Email email) {
		validator.validate(email);
	}
	
	public Validator getValidator() {
		return validator;
	}
}
