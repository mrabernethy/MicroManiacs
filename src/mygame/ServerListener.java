package mygame;

import com.jme3.math.ColorRGBA;
import com.jme3.network.HostedConnection;
import com.jme3.network.Message;
import com.jme3.network.MessageListener;
import com.jme3.network.Server;

/**
 *
 * @author Mike
 */
public class ServerListener 
implements MessageListener<HostedConnection>
{
    private ServerMain app;
    private Server server;
    
    /* A custom contructor to inform our client listener about the app. */
    public ServerListener(ServerMain app, Server server) {
        this.app = app;
        this.server = server;
    }
    
    /**
     * Identifies the sending client using source.getId(),
     * changes the message, and returns it to the sender
     * as confirmation.
     * @param source
     * @param message 
     */
    public void messageReceived(HostedConnection source,
            Message message) 
    {
        if (message instanceof GreetingMessage)
        {
            GreetingMessage greetingMessage = 
                    (GreetingMessage) message;
            System.out.println("Server received '"
                    + greetingMessage.getGreeting()
                    + "' from client #" + source.getId());
            // Send an answer
            greetingMessage.setGreeting("Welcome client #"
                    + source.getId() + "!");
            source.send(greetingMessage);                                       // returns the message
        }
        
        if(message instanceof ClientMessage)
        {
            ClientMessage clientMessage = (ClientMessage) message;
            System.out.println("Server recieved position " + clientMessage.getPos().toString() 
                    + " and rotation " + clientMessage.getQuat().toString() + " from client #" + clientMessage.getClientID());
            
            for(HostedConnection h : server.getConnections())
            {
                if(h.getId() != clientMessage.getClientID())
                    h.send(clientMessage);
            }
        }
    }
    
}
