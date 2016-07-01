package guava;

import org.junit.Test;

import com.google.common.eventbus.DeadEvent;
import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;

public class EventTest {
	@Test
	public void testReceiveEvent() throws Exception {

		EventBus eventBus = new EventBus("test");
		EventListener listener = new EventListener();

		eventBus.register(listener);

		eventBus.post(new TestEvent(200));
		eventBus.post(new TestEvent(300));
		eventBus.post(new TestEvent(400));

		System.out.println("LastMessage:" + listener.getLastMessage());
	}
	
	@Test  
    public void testMultipleEvents() throws Exception {  
       
        EventBus eventBus = new EventBus("test");  
        MultipleListener multiListener = new MultipleListener();  
       
        eventBus.register(multiListener);  
       
        eventBus.post(new Integer(100));
        eventBus.post(new Integer(200));  
        eventBus.post(new Integer(300));  
        eventBus.post(new Long(800)); 
        eventBus.post(new Long(800990));  
        eventBus.post(new Long(800882934));  
       
        System.out.println("LastInteger:"+multiListener.getLastInteger());
        System.out.println("LastLong:"+multiListener.getLastLong());
    }   
	
	@Test  
    public void testDeadEventListeners() throws Exception {  
       
        EventBus eventBus = new EventBus("test");               
        DeadEventListener deadEventListener = new DeadEventListener();  
        eventBus.register(deadEventListener);  

        eventBus.post(new TestEvent(200));         
        eventBus.post(new TestEvent(300));        
       
        System.out.println("deadEvent:"+deadEventListener.isNotDelivered());

    }  
	
	@Test  
    public void testEventsFromSubclass() throws Exception {  
       
        EventBus eventBus = new EventBus("test");  
        IntegerListener integerListener = new IntegerListener();  
        NumberListener numberListener = new NumberListener();  
        eventBus.register(integerListener);  
        eventBus.register(numberListener);  
       
        eventBus.post(new Integer(100));  
       
        System.out.println("integerListener message:"+integerListener.getLastMessage());
        System.out.println("numberListener message:"+numberListener.getLastMessage());
              
        eventBus.post(new Long(200L));  
       
        System.out.println("integerListener message:"+integerListener.getLastMessage());
        System.out.println("numberListener message:"+numberListener.getLastMessage());        
    }  
}

class TestEvent {
	private final int message;

	public TestEvent(int message) {
		this.message = message;
		System.out.println("event message:" + message);
	}

	public int getMessage() {
		return message;
	}
}

class EventListener {
	public int lastMessage = 0;

	@Subscribe
	public void listen(TestEvent event) {
		lastMessage = event.getMessage();
		System.out.println("Message:" + lastMessage);
	}

	public int getLastMessage() {
		return lastMessage;
	}
}

class MultipleListener {
    public Integer lastInteger;  
    public Long lastLong;  
   
    @Subscribe  
    public void listenInteger(Integer event) {  
        lastInteger = event; 
        System.out.println("event Integer:"+lastInteger);
    }  
   
    @Subscribe  
    public void listenLong(Long event) {  
        lastLong = event; 
        System.out.println("event Long:"+lastLong);
    }  
   
    public Integer getLastInteger() {  
        return lastInteger;  
    }  
   
    public Long getLastLong() {  
        return lastLong;  
    }  
}

class DeadEventListener {
    boolean notDelivered = false;  
       
    @Subscribe  
    public void listen(DeadEvent event) {  
        
        notDelivered = true;  
    }  
   
    public boolean isNotDelivered() {  
        return notDelivered;  
    }  
}

class NumberListener {  
    
    private Number lastMessage;  
   
    @Subscribe  
    public void listen(Number integer) {  
        lastMessage = integer; 
        System.out.println("Message:"+lastMessage);
    }  
   
    public Number getLastMessage() {  
        return lastMessage;  
    }  
}  


class IntegerListener {  
    
    private Integer lastMessage;  
   
    @Subscribe  
    public void listen(Integer integer) {  
        lastMessage = integer; 
        System.out.println("Message:"+lastMessage);
    }  
   
    public Integer getLastMessage() {  
        return lastMessage;  
    }  
}  
