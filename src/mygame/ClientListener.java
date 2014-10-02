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
        
        // Change the colour of the attached cube.
        if (message instanceof CubeMessage) 
        {
            final CubeMessage cubeMessage = (CubeMessage) message;
            
            // Modifications to the scene graph should be wrapped in a Callable()
            // and enqueued. Enqueuing the Callable ensures that the desired modification 
            // is performed in sync with other threads. 
            app.enqueue(new Callable() {
                public Void call() {
                    /* change something in the scene graph from here */
                    Material mat = new Material(app.getAssetManager(),
                        "Common/MatDefs/Misc/Unshaded.j3md");
                    mat.setColor("Color", cubeMessage.getColour());
                    // only single spacial, should call by name when more
                    app.getRootNode().getChild(0).setMaterial(mat);
                    return null;
                }
            });
        }
        
        if(message instanceof ClientMessage)
        {
            ClientMessage clientMessage = (ClientMessage) message;
            
            System.out.println("Client #" + clientMessage.getClientID() + " sent the position " + clientMessage.getPos().toString());
        }
    }
}
