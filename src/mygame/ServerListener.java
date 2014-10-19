package mygame;

import com.jme3.network.HostedConnection;
import com.jme3.network.Message;
import com.jme3.network.MessageListener;
import com.jme3.network.Server;
import entities.Player;

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
        
        if(message instanceof UpdateMessage)
        {
            final UpdateMessage clientMessage = (UpdateMessage) message;
            //System.out.println("Server recieved rotation " + clientMessage.getQuat().toString() + " from client #" + clientMessage.getClientID());
            
        }
        
        if(message instanceof ClientCommandMessage)
        {
            final ClientCommandMessage cmdMessage = (ClientCommandMessage) message;
           System.out.println("Server recieved command " + cmdMessage.getCommand().toString() + " from client #" + cmdMessage.getClientID());
            

            if(cmdMessage.getCommand().equals(ClientCommand.ADD_PLAYER))
            {
                app.addPlayer(cmdMessage.getClientID());
            }
            
            Player p = app.getPlayer(cmdMessage.getClientID());
            
            
            p.setRotation(cmdMessage.getRotation());
            
            if(cmdMessage.getCommand().equals(ClientCommand.MOVE_UP))
            {
                if(p.getCurrentVehicleID() >= 0)
                    app.getCar(p.getCurrentVehicleID()).getAcceleration().setY(3);
                else
                    p.getAcceleration().setY(2);
            }
            if(cmdMessage.getCommand().equals(ClientCommand.MOVE_DOWN))
            {
                if(p.getCurrentVehicleID() >= 0)
                    app.getCar(p.getCurrentVehicleID()).getAcceleration().setY(-3);
                else
                    p.getAcceleration().setY(-2);
            }
            if(cmdMessage.getCommand().equals(ClientCommand.MOVE_LEFT))
            {
                if(p.getCurrentVehicleID() >= 0)
                    app.getCar(p.getCurrentVehicleID()).getAcceleration().setX(-3);
                else
                    p.getAcceleration().setX(-2);
            }
            if(cmdMessage.getCommand().equals(ClientCommand.MOVE_RIGHT))
            {
                if(p.getCurrentVehicleID() >= 0)
                    app.getCar(p.getCurrentVehicleID()).getAcceleration().setX(3);
                else
                    p.getAcceleration().setX(2);
            }
            if(cmdMessage.getCommand().equals(ClientCommand.STOP_MOVE_LEFT_RIGHT))
            {
                if(p.getCurrentVehicleID() >= 0)
                    app.getCar(p.getCurrentVehicleID()).getAcceleration().setX(0);
                else
                    p.getAcceleration().setX(0);
            }
            if(cmdMessage.getCommand().equals(ClientCommand.STOP_MOVE_UP_DOWN))
            {
                if(p.getCurrentVehicleID() >= 0)
                    app.getCar(p.getCurrentVehicleID()).getAcceleration().setY(0);
                else
                    p.getAcceleration().setY(0);
            }
            if(cmdMessage.getCommand().equals(ClientCommand.SHOOT))
            {
                app.addBullet(p.getID());
            }
            if(cmdMessage.getCommand().equals(ClientCommand.INTERACT))
            {
                if(p.getCurrentVehicleID() >= 0)
                {
                    int current = p.getCurrentVehicleID();
                    p.setCurrentVehicleID(-1);
                    app.getCar(current).setRiderID(-1);
                    p.getAcceleration().setY(0);
                    p.getAcceleration().setX(0);
                }
            }
        }
    }
    
}
