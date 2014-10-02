package mygame;

import com.jme3.app.SimpleApplication;
import com.jme3.network.ConnectionListener;
import com.jme3.network.HostedConnection;
import com.jme3.network.Network;
import com.jme3.network.Server;
import com.jme3.network.serializing.Serializer;
import com.jme3.renderer.RenderManager;
import com.jme3.system.JmeContext;
import java.io.IOException;
import java.util.logging.Level;

/**
 * test
 * @author normenhansen
 */
public class ServerMain extends SimpleApplication
implements ConnectionListener
{
    private Server myServer;
    int connections = 0;
    int connectionsOld = -1;
    
    public static void main(String[] args) 
    {
        java.util.logging.Logger.getLogger("").setLevel
                (Level.WARNING);
        ServerMain app = new ServerMain();
        app.start(JmeContext.Type.Headless);
    }

    @Override
    public void simpleInitApp() 
    {
        try {
            myServer = Network.createServer
                    (Globals.NAME,
                    Globals.VERSION, Globals.DEFAULT_PORT, 
                    Globals.DEFAULT_PORT);
            myServer.start();
        } catch (IOException ex) {
            
        }
        
        Serializer.registerClass(ClientMessage.class);
        Serializer.registerClass(GreetingMessage.class); // register the greeting message class
        Serializer.registerClass(CubeMessage.class); //register the cube message class
        
        myServer.addMessageListener(new ServerListener(this, myServer),
                CubeMessage.class);
        myServer.addMessageListener(new ServerListener(this, myServer),
                GreetingMessage.class);
        myServer.addMessageListener(new ServerListener(this, myServer),
                ClientMessage.class);
        //myServer.addConnectionListener(this);
        
        
        // Extra message sending info...
//        // The server can also send a message to all clients instead of just to one. Create
//        // your message as before, and broadcast it to all clients using the following line:
//        myServer.broadcast(message);
//        // Use com.jme3.network.Filters to broadcast the message to an
//        // explicit list of clients, from client1 to client3.
//        myServer.broadcast( Filters.in( client1, client2,
//            client3 ), message );
//        // Broadcast to all clients except the listed client client4 by using:
//        myServer.broadcast( Filters.notEqualTo( client4 ),
//            message );
        
    }

    @Override
    public void update()
    {
        connections = myServer.getConnections().size();
        if (connectionsOld != connections) {
            System.out.println("Server connections: " + connections);
            connectionsOld = connections;
        }
    }
    
//    @Override
//    public void simpleUpdate(float tpf) {
//        //TODO: add update code
//    }

    @Override
    public void simpleRender(RenderManager rm) {
        //TODO: add render code
    }
    
    @Override
    public void destroy()
    {
        try {
            myServer.close();
        } catch (Exception ex) {}
        super.destroy();
    }
    
    /** Specify what happens when a client connects to this server */
    public void connectionAdded(Server server, HostedConnection client) {
         System.out.println("Server knows that client #"
                + client.getId() + " is ready.");
        client.close("");
    }
    
    /** Specify what happens when a client disconnects from this server */
    public void connectionRemoved(Server server, HostedConnection client) {
        System.out.println("Server knows that client #"
                + client.getId() + " has left.");
    }
}
