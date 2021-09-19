package mockito;

public class Email {
	private String content;
	private String to;
	
	public void setContent(String content) {
		this.content = content;
	}
	
	public void setTo(String to) {
		this.to = to;
	}
	
	public String getTo() {
		return to;
	}
	
	public String getContent() {
		return content;
	}
}
