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
 * 
 * @author Mike
 */
public class ServerMain extends SimpleApplication {
    
    public static void main(String[] args) {
        java.util.logging.Logger.getLogger("").setLevel
                (Level.SEVERE);
        ServerMain app = new ServerMain();
        app.start(JmeContext.Type.Headless);
    }
    
    private Server myServer;
    int connections = 0;
    int connectionsOld = -1;
    
    @Override
    public void simpleInitApp() 
    {
        try {
            myServer = Network.createServer
                    (Globals.NAME,
                    Globals.VERSION, Globals.DEFAULT_PORT, 
                    Globals.DEFAULT_PORT);
            myServer.start();
        } catch (IOException ex) {}
        
        Serializer.registerClass(ClientMessage.class);
        Serializer.registerClass(GreetingMessage.class); // register the greeting message class

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
    
    
}

/*
 * Class adapted from Kusterer, R. (2013). JMonkeyEngine 3.0 Beginner's Guide. Packt Publishing Ltd.
 */
