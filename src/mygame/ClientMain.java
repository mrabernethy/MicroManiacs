package mygame;

import com.jme3.app.SimpleApplication;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.network.Client;
import com.jme3.network.ClientStateListener;
import com.jme3.network.ClientStateListener.DisconnectInfo;
import com.jme3.network.Message;
import com.jme3.network.Network;
import com.jme3.network.serializing.Serializer;
import com.jme3.renderer.RenderManager;
import com.jme3.scene.Geometry;
import com.jme3.scene.shape.Box;
import com.jme3.system.JmeContext;
import java.io.IOException;
import java.util.logging.Level;

/**
 *
 * @author Mike
 */
public class ClientMain extends SimpleApplication
implements ClientStateListener
{
    private Client myClient;
    
    public static void main(String[] args) 
    {
        java.util.logging.Logger.getLogger("").setLevel(Level.SEVERE);
        ClientMain app = new ClientMain();
        app.start(JmeContext.Type.Display);
    }

    @Override
    public void simpleInitApp() 
    {
        try {
            myClient = Network.connectToServer (Globals.NAME,
                    Globals.VERSION, Globals.DEFAULT_SERVER, 
                    Globals.DEFAULT_PORT);
            myClient.start();
        } catch (IOException ex) {
            
        }
        
        Serializer.registerClass(GreetingMessage.class); // register the message class
        Serializer.registerClass(CubeMessage.class); // register the cube message class

        myClient.addMessageListener(new ClientListener(this, myClient),
                GreetingMessage.class);
        myClient.addMessageListener(new ClientListener(this, myClient),
                CubeMessage.class);
        
        // Message to send to the server.
        Message message = new GreetingMessage("Hi Server! "
                + "Do you hear me?");
        myClient.send(message);
        
        attachCube("One Cube"); // attaches a cube to the spatial
        
        /* example for client-server communication that changes the scene graph */          
        Message m = new CubeMessage();                                                      // doesn't initialise cubemessage colour variable
        //Message m = new CubeMessage(ColorRGBA.randomColor());                             // trying with colour initialised
        myClient.send(m);
    }
    
    /* Add some demo content */
    public void attachCube(String name) {
        Box box = new Box(1,1,1);
        Geometry geom = new Geometry(name, box);
        Material mat = new Material(assetManager,
                "Common/MatDefs/Misc/Unshaded.j3md");
        mat.setColor("Color", ColorRGBA.White);
        geom.setMaterial(mat);
        rootNode.attachChild(geom);
    }

    @Override
    public void simpleUpdate(float tpf) {
        //TODO: add update code
    }

    @Override
    public void simpleRender(RenderManager rm) {
        //TODO: add render code
    }
    
    @Override
    public void destroy()
    {
        try {
            myClient.close();
        } catch (Exception ex) {}
        super.destroy();
    }
    
    /** Specify what happens when this client connects to server */
    public void clientConnected(Client client) // not firing
    {
        System.out.println("Client #" + client.getId() + " is ready.");
        
//        /* example for client-server communication that changes the scene graph */          // not working
//        Message m = new CubeMessage();                                                      // doesn't initialise cubemessage colour variable
//        //Message m = new CubeMessage(ColorRGBA.randomColor());                               // trying with colour initialised
//        myClient.send(m);        
    }
    
    /** Specify what happens when this client disconnects from server */
    public void clientDisconnected(Client client, DisconnectInfo info) // not firing
    {
        System.out.println("Client #" + client.getId() + " has left.");
    }
}
