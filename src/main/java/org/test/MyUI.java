package org.test;

import javax.servlet.annotation.WebServlet;

import com.vaadin.annotations.Push;
import com.vaadin.annotations.Theme;
import com.vaadin.annotations.VaadinServletConfiguration;
import com.vaadin.server.VaadinRequest;
import com.vaadin.server.VaadinServlet;
import com.vaadin.ui.Button;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.TextField;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;

@Theme("mytheme")
// Enable server push. Defaults to "Automatic" mode
@Push
public class MyUI extends UI implements BroadcastListener {

	private final VerticalLayout messages = new VerticalLayout();
	
	@WebServlet(urlPatterns = "/*", name = "MyUIServlet", asyncSupported = true)
    @VaadinServletConfiguration(ui = MyUI.class, productionMode = false)
    public static class MyUIServlet extends VaadinServlet {
    }
	
    @Override
    protected void init(VaadinRequest vaadinRequest) {
        // Register this UI as a listener
    	Broadcaster.register(this);
    	
    	// Create the UI
        final HorizontalLayout sendContainer = new HorizontalLayout();
        TextField message = new TextField();
        Button send = new Button("Send");
        send.addClickListener(e -> {
        	// Broadcast message then clear the TextField when Send pressed
        	Broadcaster.broadcast(message.getValue());
        	message.clear();
        });
        sendContainer.addComponents(message, send);
        
        final VerticalLayout layout = new VerticalLayout();
        layout.addComponents(messages, sendContainer);
        layout.setSpacing(true);
        
        setContent(layout);
    }
    
    /**
     * Unregister this listener when the UI expires
     */
    @Override
    public void detach() {
        Broadcaster.unregister(this);
        super.detach();
    }

	@Override
	public void receiveBroadcast(String message) {
		// Lock the session to prevent data corruption or deadlock
		// As Push mode is automatic, changes will be sent to the browser when access() finishes. No need to call ui.push();
        access(new Runnable() {
            @Override
            public void run() {
                // Show the message
                messages.addComponent(new Label(message));
            }
        });
	}
}
