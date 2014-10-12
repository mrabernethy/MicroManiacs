package mygame;

import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.network.Client;
import com.jme3.network.Message;
import com.jme3.network.MessageListener;
import java.util.concurrent.Callable;

/**
 *
 * @author Mike
 */
public class ClientListener 
implements MessageListener<Client>
{
    private ClientMain app;
    private Client client;
    
    public ClientListener(ClientMain app, Client client)
    {
        this.app = app;
        this.client = client;
    }
    
    /**
     * Identifies itself to the ServerListener using source.getID()
     * which returns a unique ID for each client, and prints the message.
     * @param source
     * @param message 
     */
    public void messageReceived(Client source, Message message) 
    {
        if (message instanceof GreetingMessage)
        {
            GreetingMessage greetingMessage = 
                    (GreetingMessage) message;
            System.out.println("Client #" + source.getId()
                    + " recieved the message: '" 
                    + greetingMessage.getGreeting() + "'");
        }
        
        if(message instanceof ClientMessage)
        {
            final ClientMessage clientMessage = (ClientMessage) message;
            
            app.enqueue(new Callable() {
                public Void call()
                {
                    if(!app.playerExists(clientMessage.getClientID()))
                    {
                        app.addPlayer(clientMessage.getClientID());
                    }
            
                    app.updatePlayer(clientMessage.getClientID(), clientMessage.getPos(), clientMessage.getQuat());
                    
                    return null;
                }
            });
           
            
            System.out.println("Client #" + clientMessage.getClientID() + " sent the position " + clientMessage.getPos().toString());
        }
    }
}
