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
                    if(updateMessage.getToUpdate().equals("Player"))
                    {
                        System.out.println("Client recieved position:" + updateMessage.getPos() + " and rotation:" + updateMessage.getQuat() + " for player #" + updateMessage.getUpdateID());
                        
                        if(!app.playerExists(updateMessage.getUpdateID()))
                        {
                            app.addPlayer(updateMessage.getUpdateID());
                        }

                        app.updatePlayer(updateMessage.getUpdateID(), updateMessage.getPos(), updateMessage.getQuat());
                    }
                    if(updateMessage.getToUpdate().equals("Bullet"))
                    {
                        System.out.println("Client recieved position:" + updateMessage.getPos() + " for bullet #" + updateMessage.getUpdateID());
                        
                        if(!app.bulletExists(updateMessage.getUpdateID()))
                        {
                            app.addBullet(updateMessage.getClientID(), updateMessage.getUpdateID());
                        }
                        
                        app.updateBullet(updateMessage.getUpdateID(), updateMessage.getPos());
                    }
                    return null;
                }
            });
        }
    }
}
