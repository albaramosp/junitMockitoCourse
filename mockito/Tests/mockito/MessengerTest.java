package mockito;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Answers;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;

import static org.mockito.ArgumentMatchers.any;

class MessengerTest {

	private static final String RANDOM_EMAIL = "someone@example.com";
	private static final String RANDOM_MESSAGE = "Random message";

	@Mock
	private TemplateEngine templateEngineMock;
	
	@Mock(answer = Answers.RETURNS_DEEP_STUBS)
	private MailServer mailServerMock;
	
	private AutoCloseable closeable;
	
	@InjectMocks
	private Messenger messenger; // Initiailzed with both previous mocks
	
	@Captor
	private ArgumentCaptor<Email> captor;
	
	@Spy //Spy not for abstract classes nor interfaces
	private ArrayList<Integer> myList;
	
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
    void shouldThrowExceptionWhenTemplateEngineThrowsException() {
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

	@Test
	void shouldSendEmailOnlyOnce() {
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

		verify(templateEngineMock, times(1)).prepareMessage(client, template);
		verify(mailServerMock, times(1)).sendEmail(any(Email.class));
	}
	
	@Test
	void sholdPrepareMessageBeforeSending() {
		var client = new Client(RANDOM_EMAIL);
		var template = new Template();
		var inOrder = Mockito.inOrder(templateEngineMock, mailServerMock);
		
		when(templateEngineMock.prepareMessage(client, template)).thenReturn(RANDOM_MESSAGE);
		
		messenger.sendMessage(client, template);

		inOrder.verify(templateEngineMock).prepareMessage(client, template);
		inOrder.verify(mailServerMock).sendEmail(any(Email.class));
	}
	
	@Test
    void shouldSendMessageAndThereIsNoMoreInteractionsWithTemplateEngine() {
    	var inOrder = Mockito.inOrder(templateEngineMock, mailServerMock);
    	var client = new Client(RANDOM_EMAIL);
    	var template = new Template();
    	
    	when(templateEngineMock.prepareMessage(client, template))
    			.thenReturn(RANDOM_MESSAGE);
    	
    	// Interaction between the mocks
    	// sendMessage calls prepareMessage and sendEmail on the mocks
    	// Other calls in sendMessage are not over the mocks
        messenger.sendMessage(client, template);

        // Verification of expected behaviour
        inOrder.verify(templateEngineMock).prepareMessage(client, template);
        inOrder.verify(mailServerMock).sendEmail(any(Email.class));
        
        // Verify no unexpected behaviour took place
        verifyNoMoreInteractions(templateEngineMock, mailServerMock);
        
		// In this case verifyNoMoreInteractions will throw an exception because that was unexpected
		// templateEngineMock.evaluateTemplate(null);
		// verifyNoMoreInteractions(templateEngineMock);
    }
	
	@Test
    void understandingVerifyNoMoreInteractions() {
    	var client = new Client(RANDOM_EMAIL);
    	var template = new Template();
    	
    	when(templateEngineMock.prepareMessage(client, template))
    			.thenReturn(RANDOM_MESSAGE);
    	
    	// Interaction with the mock
    	// test method calls evaluateTemplate and nothing more on the mock
        messenger.test(template);

        // Verification of expected behaviour
        // This will be ok because this method was called
        verify(templateEngineMock).evaluateTemplate(template);
        
        // Verify no unexpected behaviour took place
        // This will be ok because no other method was called during interaction (messenger.test)
        verifyNoMoreInteractions(templateEngineMock);
        
		// In this case verifyNoMoreInteractions will throw an exception because that was unexpected
        // Because messenger.test just calls evaluateTemplate once.
		// templateEngineMock.evaluateTemplate(null);
		// verifyNoMoreInteractions(templateEngineMock);
    }

    @Test
    void shouldCallRealMethodOnMockExample() {
    	Client client = new Client("");
		Template template = new Template();
		
		// null is returned
		System.out.println(templateEngineMock.prepareMessage(client, template));
		
		when(templateEngineMock.prepareMessage(client, template)).thenCallRealMethod();
		// 'Some template' will be returned - because that is the result of prepare message 
		System.out.println(templateEngineMock.prepareMessage(client, template));
    }

    @Test
    void shouldNotThrowExceptionWithDeepStub() {
    	var client = new Client(RANDOM_EMAIL);
    	var template = new Template();
        var validator = mailServerMock.getValidator();
        
        // this line will throw NPE without DEEP STUBS
        validator.validate(new Email());
    }
    
    @Test
    void spyExample() {
    	List<Integer> lst1 = new ArrayList<>();
    	List<Integer> spy = Mockito.spy(lst1);
    	
    	// Method invocation on the spy is delegated to the real object
    	// The real object is lst1 that is empty
    	// So this will throw IndexOutOfBoundsException
    	// when(spy.get(0)).thenReturn(0);
    	
    }
    
    
    // LIMITATIONS
    // - Cannot mock final classes
    // - Cannot pass static methods, equals or hashcode

}









