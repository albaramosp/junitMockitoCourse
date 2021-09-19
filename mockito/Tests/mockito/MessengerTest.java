package mockito;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import static org.mockito.ArgumentMatchers.any;

class MessengerTest {

	private static final String RANDOM_EMAIL = "someone@example.com";
	private static final String RANDOM_MESSAGE = "Random message";

	@Mock
	private TemplateEngine templateEngineMock;
	@Mock
	private MailServer mailServerMock;
	private AutoCloseable closeable;
	@InjectMocks
	private Messenger messenger; // Initiailzed with both previous mocks
	@Captor
	private ArgumentCaptor<Email> captor;
	
	@BeforeEach
	void setUp() {
		//templateEngineMock = mock(TemplateEngine.class);
		//MockitoAnnotations.initMocks(this);
		closeable = MockitoAnnotations.openMocks(this);
	}
	
	@AfterEach
	void setOff() throws Exception {
		closeable.close();
	}

	@Test
	void shouldSendMessage() {
		var client = new Client(RANDOM_EMAIL);
		var template = new Template();
		
		// When prepareMessage is invoked with {client, template} specific arguments,
		// RANDOM_MESSAGE will be returned
		// When invoked with other different args, like {new Client()}, then
		// null will be returned
		when(templateEngineMock.prepareMessage(client, template)).thenReturn(RANDOM_MESSAGE);
		
		
		// messenger object contains templateEngineMock and mailServerMock
		// sendMessage invokes prepareMessage and sendEmail
		// prepareMessage uses local client and template of this method
		messenger.sendMessage(client, template);
		
		verify(templateEngineMock).prepareMessage(client, template); // Therefore this is true
		verify(mailServerMock).sendEmail(any(Email.class)); //  And this is true, too
		
		// We've verified that when invoking Messenger class method with {client, template} args,
		// another metod from another class (prepareMessage) is invoked with the same args
		// or just another method was invoked and created an email
	}
	
	@Test
    void shouldSendMessageWithArgumentMatchers() {
    	var client = new Client(RANDOM_EMAIL);
    	var template = new Template();
    	
    	when(templateEngineMock.prepareMessage(any(Client.class), 
    			any(Template.class))).thenReturn(RANDOM_EMAIL);
        
    	messenger.sendMessage(client, template);

        verify(templateEngineMock).prepareMessage(client, template);
        verify(mailServerMock).sendEmail(any(Email.class));
    }
	
	@Test
    public void shouldThrowExceptionWhenTemplateEngineThrowsException() {
    	var client = new Client(RANDOM_EMAIL);
    	var template = new Template();
        
    	// When this method is called, the exception is thrown
    	when(templateEngineMock.prepareMessage(
        		client, template))
        		.thenThrow(new IllegalArgumentException());

    	// This method calls prepareMessage, so exception should be thrown
        assertThrows(IllegalArgumentException.class, () -> 
        	messenger.sendMessage(client, template)
        );
    }
}
