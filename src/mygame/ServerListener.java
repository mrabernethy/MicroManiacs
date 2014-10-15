package mygame;

import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
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
            //System.out.println("Server recieved rotation " + clientMessage.getQuat().toString() + " from client #" + clientMessage.getClientID());
 
            System.out.println("Server recieved position " + clientMessage.getPosition().toString() 
                    + " and rotation " + clientMessage.getRotation().toString() + " from client #" + clientMessage.getClientID());
            if(!app.playerExists(clientMessage.getClientID()))
            {
                System.out.println("Client # " + clientMessage.getClientID() + " player doesn't exist, adding it");
                app.addPlayer(clientMessage.getClientID());
            }
            app.updatePlayer(clientMessage.getClientID(), clientMessage.getPosition(), clientMessage.getRotation());
        }
        
        if(message instanceof ClientCommandMessage)
        {
            final ClientCommandMessage cmdMessage = (ClientCommandMessage) message;
           System.out.println("Server recieved command " + cmdMessage.getCommand().toString() + " from client #" + cmdMessage.getClientID());
              
            if(!app.playerExists(cmdMessage.getClientID()))
            {
                System.out.println("Client # " + cmdMessage.getClientID() + " player doesn't exist, adding it");
                app.addPlayer(cmdMessage.getClientID());
            }

            if(cmdMessage.getCommand().equals(ClientCommand.MOVE_UP))
            {
                app.getPlayer(cmdMessage.getClientID()).getVelocity().setY(10);
            }
            if(cmdMessage.getCommand().equals(ClientCommand.MOVE_DOWN))
            {
                app.getPlayer(cmdMessage.getClientID()).getVelocity().setY(-10);
            }
            if(cmdMessage.getCommand().equals(ClientCommand.MOVE_LEFT))
            {
                app.getPlayer(cmdMessage.getClientID()).getVelocity().setX(-10);
            }
            if(cmdMessage.getCommand().equals(ClientCommand.MOVE_RIGHT))
            {
                app.getPlayer(cmdMessage.getClientID()).getVelocity().setX(10);
            }
            if(cmdMessage.getCommand().equals(ClientCommand.STOP_MOVE_LEFT_RIGHT))
            {
                app.getPlayer(cmdMessage.getClientID()).getVelocity().setX(0);
            }
            if(cmdMessage.getCommand().equals(ClientCommand.STOP_MOVE_UP_DOWN))
            {
                app.getPlayer(cmdMessage.getClientID()).getVelocity().setY(0);
            }
            
            app.getPlayer(cmdMessage.getClientID()).setRotation(cmdMessage.getRotation());
            System.out.println(app.getPlayer(cmdMessage.getClientID()).getRigidBodyControl().getPhysicsLocation());
            //System.out.println(app.getPlayer(cmdMessage.getClientID()).get)
            
        }
    }
}

/*
 * Class adapted from Kusterer, R. (2013). JMonkeyEngine 3.0 Beginner's Guide. Packt Publishing Ltd.
 */
