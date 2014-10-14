package mygame;

import com.jme3.network.HostedConnection;
import com.jme3.network.Message;
import com.jme3.network.MessageListener;
import com.jme3.network.Server;
import java.util.concurrent.Callable;

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
            
            server.broadcast(clientMessage);
        }
        
        if(message instanceof ClientCommandMessage)
        {
            ClientCommandMessage cmdMessage = (ClientCommandMessage) message;
           // System.out.println("Server recieved command " + cmdMessage.getCommand().toString() + " from client #" + cmdMessage.getClientID());
            
            System.out.println(cmdMessage.getCommand());
            
            //app.enqueue(new Callable() {
                //public Void call()
                //{
                    if(cmdMessage.getCommand().equals(ClientCommand.MOVE_UP))
                    {
                        app.getPlayer(cmdMessage.getClientID()).getVelocity().setY(3);
                    }
                    if(cmdMessage.getCommand().equals(ClientCommand.MOVE_DOWN))
                    {
                        app.getPlayer(cmdMessage.getClientID()).getVelocity().setY(-3);
                    }
                    if(cmdMessage.getCommand().equals(ClientCommand.MOVE_LEFT))
                    {
                        app.getPlayer(cmdMessage.getClientID()).getVelocity().setX(-3);
                    }
                    if(cmdMessage.getCommand().equals(ClientCommand.MOVE_RIGHT))
                    {
                        app.getPlayer(cmdMessage.getClientID()).getVelocity().setX(3);
                    }
                    if(cmdMessage.getCommand().equals(ClientCommand.STOP_MOVE_LEFT_RIGHT))
                    {
                        app.getPlayer(cmdMessage.getClientID()).getVelocity().setX(0);
                    }
                    if(cmdMessage.getCommand().equals(ClientCommand.STOP_MOVE_UP_DOWN))
                    {
                        app.getPlayer(cmdMessage.getClientID()).getVelocity().setY(0);
                    }
                //    return null;
               //}
               // });
            
            // Check what command was sent
            
        }
    }
    
}
