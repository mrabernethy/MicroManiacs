package mygame;

import com.jme3.app.SimpleApplication;
import com.jme3.input.KeyInput;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.AnalogListener;
import com.jme3.input.controls.KeyTrigger;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.network.Client;
import com.jme3.network.ClientStateListener;
import com.jme3.network.ClientStateListener.DisconnectInfo;
import com.jme3.network.Network;
import com.jme3.network.serializing.Serializer;
import com.jme3.renderer.RenderManager;
import com.jme3.scene.Geometry;
import com.jme3.scene.shape.Box;
import com.jme3.system.JmeContext;
import com.jme3.texture.Texture;
import engine.sprites.Sprite;
import engine.sprites.SpriteImage;
import engine.sprites.SpriteManager;
import engine.sprites.SpriteMesh;
import entities.Player;
import java.io.IOException;
import java.util.HashMap;
import java.util.logging.Level;

/**
 *
 * @author Mike
 */
public class ClientMain extends SimpleApplication
implements ClientStateListener
{
    private SpriteManager spriteManager;
    
    private Client myClient;
    private Vector3f lastSentPosition;
    
    private HashMap<Integer, Player> players = new HashMap();
    
    
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
        
        // Init Mappings and Listeners
        inputManager.addMapping("Left", new KeyTrigger(KeyInput.KEY_A), new KeyTrigger(KeyInput.KEY_LEFT));
        inputManager.addMapping("Right", new KeyTrigger(KeyInput.KEY_D), new KeyTrigger(KeyInput.KEY_RIGHT));
        inputManager.addMapping("Up", new KeyTrigger(KeyInput.KEY_W), new KeyTrigger(KeyInput.KEY_UP));
        inputManager.addMapping("Down", new KeyTrigger(KeyInput.KEY_S), new KeyTrigger(KeyInput.KEY_DOWN));
        
        inputManager.addListener(actionListener, new String[]{"Left", "Right", "Up", "Down"});
        inputManager.addListener(analogListener, "");
        
        spriteManager = new SpriteManager(1024, 1024, SpriteMesh.Strategy.ALLOCATE_NEW_BUFFER, rootNode, assetManager);
        getStateManager().attach(spriteManager);
        
        Serializer.registerClass(ClientMessage.class);
        Serializer.registerClass(GreetingMessage.class); // register the message class

        myClient.addMessageListener(new ClientListener(this, myClient),
                GreetingMessage.class);
        myClient.addMessageListener(new ClientListener(this, myClient),
                ClientMessage.class);
        //myClient.addClientStateListener(this);
        
        // Message to send to the server.
        myClient.send(new GreetingMessage("Hi Server! Do you hear me?"));
        
        myClient.send(new ClientMessage(this.cam.getLocation(), myClient.getId()));
        lastSentPosition = new Vector3f(this.cam.getLocation());
        
        attachCube(); // attaches a cube to the spatial
        //attachWorld();
        
        this.flyCam.setEnabled(false);
        
        // Add player
        addPlayer(myClient.getId());
    }
    
    /* Add some demo content */
    public void attachCube() {
        
        Box box = new Box(3,1,0);
        Geometry geom = new Geometry("Cube", box);
        Material mat = new Material(assetManager,
                "Common/MatDefs/Misc/Unshaded.j3md");
        mat.setColor("Color", ColorRGBA.White);
        geom.setMaterial(mat);
        geom.setLocalScale(3);
        geom.setLocalTranslation(new Vector3f(0,0,-0.5f));
        rootNode.attachChild(geom);
    }
    
    //Doesn't work
    //Testing displaying an image for the world
    public void attachWorld()
    {
        SpriteImage spriteImage = spriteManager.createSpriteImage("testWorld.jpg", false);
        
        Sprite sprite = new Sprite(spriteImage);
        sprite.setPosition(0, 0, 0);
    }
    
    public void addPlayer(int id)
    {
        SpriteImage spriteImage = spriteManager.createSpriteImage("smile.jpg", false);
        
        Player p = new Player(new Vector3f(0,0,0), spriteImage);
        p.getSprite().setSize(0.4f);
        
        players.put(id, p);
    }
    
    public boolean playerExists(int id)
    {
        return players.containsKey(id);
    }
    
    public void movePlayer(int id, Vector3f position)
    {
        players.get(id).setPosition(position);
    }
    
    public void removePlayer(int id)
    {
        players.remove(id).getSprite().delete();
    }

    @Override
    public void simpleUpdate(float tpf) {
        //TODO: add update code
        
        // Update players
        for(Player p : players.values())
        {
            p.update(tpf);
        }
        
        // Update camera
        
        // Send this players position every x movement distance
        if(players.get(myClient.getId()).getPosition().distance(lastSentPosition) > 0.05)
        {
            lastSentPosition = new Vector3f(players.get(myClient.getId()).getPosition());
            myClient.send(new ClientMessage(players.get(myClient.getId()).getPosition(), myClient.getId()));
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
    
    private ActionListener actionListener = new ActionListener() {
        public void onAction(String name, boolean keyPressed, float tpf) {
        /** TODO: test for mapping names and implement actions */
            
            Player player = players.get(myClient.getId());
            
            // LEFT
            if(name.equals("Left") && keyPressed)
            {
                // Set player left velocity
                player.setVelocity(player.getVelocity().setX(-0.5f));
                
            }
            else if(name.equals("Left") && !keyPressed)
            {
                // Stop player left velocity
                player.setVelocity(player.getVelocity().setX(0));
            }
            
            // RIGHT
            if(name.equals("Right") && keyPressed)
            {
                // Set player right velocity
                player.setVelocity(player.getVelocity().setX(0.5f));
                
            }
            else if(name.equals("Right") && !keyPressed)
            {
                // Stop player right velocity
                player.setVelocity(player.getVelocity().setX(0));
            }
            
            // UP
            if(name.equals("Up") && keyPressed)
            {
                // Set player up velocity
                player.setVelocity(player.getVelocity().setY(0.5f));
                
            }
            else if(name.equals("Up") && !keyPressed)
            {
                // Stop player up velocity
                player.setVelocity(player.getVelocity().setY(0));
            }
            
            // DOWN
            if(name.equals("Down") && keyPressed)
            {
                // Set player down velocity
                player.setVelocity(player.getVelocity().setY(-0.5f));
            }
            else if(name.equals("Down") && !keyPressed)
            {
                // Stop player down velocity
                player.setVelocity(player.getVelocity().setY(0));
            }
            
        }
    };
    
    private AnalogListener analogListener = new AnalogListener() {
        public void onAnalog(String name, float keyPressed, float tpf) {
        /** TODO: test for mapping names and implement actions */
            
        }
    };
}
