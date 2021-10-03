package mockito;

public class Messenger {
	private TemplateEngine templateEngine;
	private MailServer mailServer;
	
	public TemplateEngine getTemplateEngine() {
		return templateEngine;
	}
	
	public MailServer getMailServer() {
		return mailServer;
	}
	
	public void setTemplateEngine(TemplateEngine templateEngine) {
		this.templateEngine = templateEngine;
	}
	
	public void setMailServer(MailServer mailServer) {
		this.mailServer = mailServer;
	}
	
	public void sendMessage(Client client, Template template) {
		String content = templateEngine.prepareMessage(client, template);
		Email email = new Email();
		
		email.setContent(content);
		email.setTo(client.getEmail());
		
		mailServer.sendEmail(email);
	}
	
	public void test(Template t) {
		templateEngine.evaluateTemplate(t);
	}

}
