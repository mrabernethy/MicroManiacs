package mygame;

import com.jme3.app.SimpleApplication;
import com.jme3.input.controls.ActionListener;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
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
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Mike
 */
public class ClientMain extends SimpleApplication
implements ClientStateListener
{
    private Client myClient;
    private Vector3f lastSentPosition;
    
    private HashMap<Integer, Geometry> players = new HashMap();
    
    
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
        
        Serializer.registerClass(ClientMessage.class);
        Serializer.registerClass(GreetingMessage.class); // register the message class
        Serializer.registerClass(CubeMessage.class); // register the cube message class

        myClient.addMessageListener(new ClientListener(this, myClient),
                GreetingMessage.class);
        myClient.addMessageListener(new ClientListener(this, myClient),
                CubeMessage.class);
        myClient.addMessageListener(new ClientListener(this, myClient),
                ClientMessage.class);
        //myClient.addClientStateListener(this);
        
        // Message to send to the server.
        myClient.send(new GreetingMessage("Hi Server! Do you hear me?"));
        
        myClient.send(new ClientMessage(this.cam.getLocation(), myClient.getId()));
        lastSentPosition = new Vector3f(this.cam.getLocation());
        
        attachCube("One Cube"); // attaches a cube to the spatial
        
        /* example for client-server communication that changes the scene graph */                                                             // doesn't initialise cubemessage colour variable
        //Message m = new CubeMessage(ColorRGBA.randomColor());                             // trying with colour initialised
        myClient.send(new CubeMessage());
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
    
    public void addPlayer(int id)
    {
        Box box = new Box(0.1f,0.1f,0.1f);
        Geometry geom = new Geometry("" + id, box);
        Material mat = new Material(assetManager,
                "Common/MatDefs/Misc/Unshaded.j3md");
        mat.setColor("Color", ColorRGBA.Green);
        geom.setMaterial(mat);
        rootNode.attachChild(geom);
        players.put(id, geom);
    }
    
    public boolean playerExists(int id)
    {
        return players.containsKey(id);
    }
    
    public void movePlayer(int id, Vector3f position)
    {
        players.get(id).center();
        players.get(id).move(position);
    }
    
    public void removePlayer(int id)
    {
        rootNode.getChild("" + id).removeFromParent();
    }

    @Override
    public void simpleUpdate(float tpf) {
        //TODO: add update code
        
        if(cam.getLocation().distance(lastSentPosition) > 0.2)
        {
            lastSentPosition = new Vector3f(cam.getLocation());
            myClient.send(new ClientMessage(cam.getLocation(), myClient.getId()));
        }
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
    public void clientConnected(Client client) 
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
