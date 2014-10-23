package mygame;

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
        
        if(message instanceof UpdateMessage)
        {
            final UpdateMessage updateMessage = (UpdateMessage) message;
            
            app.enqueue(new Callable() {
                public Void call()
                {
                    app.updateEntity(updateMessage.getUpdateMessageString());
                    
                    return null;
                }
            });
        }
        
        if (message instanceof GameStateMessage)
        {
            final GameStateMessage gameStateMessage = (GameStateMessage) message;
            
            app.enqueue(new Callable(){
                public Void call()
                {
                    app.setGameState(gameStateMessage.getGameState());
                    
                    return null;
                }
            });
        }
    }
}
