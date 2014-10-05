package mygame;

import com.jme3.app.SimpleApplication;
import com.jme3.input.ChaseCamera;
import com.jme3.input.KeyInput;
import com.jme3.input.MouseInput;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.AnalogListener;
import com.jme3.input.controls.KeyTrigger;
import com.jme3.input.controls.MouseAxisTrigger;
import com.jme3.input.controls.MouseButtonTrigger;
import com.jme3.input.controls.Trigger;
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
    
    // Chase camera
    //private ChaseCamera chaseCamera;
    
    private Player player;
    private HashMap<Integer, Player> players = new HashMap();
    
    private final static Trigger TRIGGER_W = new KeyTrigger(KeyInput.KEY_W);
    private final static Trigger TRIGGER_S = new KeyTrigger(KeyInput.KEY_S);
    private final static Trigger TRIGGER_A = new KeyTrigger(KeyInput.KEY_A);
    private final static Trigger TRIGGER_D = new KeyTrigger(KeyInput.KEY_D);
    private final static Trigger TRIGGER_UP = new KeyTrigger(KeyInput.KEY_UP);
    private final static Trigger TRIGGER_DOWN = new KeyTrigger(KeyInput.KEY_DOWN);
    private final static Trigger TRIGGER_LEFT = new KeyTrigger(KeyInput.KEY_LEFT);
    private final static Trigger TRIGGER_RIGHT = new KeyTrigger(KeyInput.KEY_RIGHT);
    // TODO: add shoot 
    //private final static Trigger TRIGGER_SPACE = new KeyTrigger(KeyInput.KEY_SPACE);
    private final static Trigger TRIGGER_ROTATE_X = new MouseAxisTrigger(MouseInput.AXIS_X, true);
    private final static Trigger TRIGGER_ROTATE_Y = new MouseAxisTrigger(MouseInput.AXIS_Y, true);
    
    private final static String  MAPPING_UP = "Up";
    private final static String  MAPPING_DOWN = "Down";
    private final static String  MAPPING_LEFT = "Left";
    private final static String  MAPPING_RIGHT = "Right";
    //private final static String  MAPPING_SHOOT = "Shoot";
    private final static String  MAPPING_ROTATE = "Rotate";
    
    
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
        
        // Add the chase camera
        //chaseCamera = new ChaseCamera(Camera cam);
        
        // Init Mappings and Listeners
        inputManager.addMapping(MAPPING_UP, TRIGGER_W, TRIGGER_UP);
        inputManager.addMapping(MAPPING_DOWN, TRIGGER_S, TRIGGER_DOWN);
        inputManager.addMapping(MAPPING_LEFT, TRIGGER_A, TRIGGER_LEFT);
        inputManager.addMapping(MAPPING_RIGHT, TRIGGER_D, TRIGGER_RIGHT);
        //inputManager.addMapping(MAPPING_SHOOT, TRIGGER_SPACE);
        inputManager.addMapping(MAPPING_ROTATE, TRIGGER_ROTATE_X, TRIGGER_ROTATE_Y);
        // TODO: add space mapping to listener
        inputManager.addListener(actionListener, new String[]{MAPPING_UP, MAPPING_DOWN, 
            MAPPING_LEFT, MAPPING_RIGHT});
        inputManager.addListener(analogListener, new String[]{MAPPING_ROTATE});
        
        spriteManager = new SpriteManager(1024, 1024, SpriteMesh.Strategy.ALLOCATE_NEW_BUFFER, rootNode, assetManager);
        getStateManager().attach(spriteManager);
        
        // Register the message classes
        Serializer.registerClass(ClientMessage.class);
        Serializer.registerClass(GreetingMessage.class); 
        // Add the message listeners
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
        
        // TODO: attach the world
        //attachWorld();
        
        this.flyCam.setEnabled(false);
        
        // Add player
        addPlayer(myClient.getId());
    }
    
    /* Add some demo content */
    // Currently used for the background and set to white.
    public void attachCube() {
        
        Box box = new Box(3,3,0);
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
        
        player = new Player(new Vector3f(0,0,0), spriteImage);
        player.getSprite().setSize(0.4f);
        // TODO: add chase camera to this player
        //player.addControl(chaseCamera);
        players.put(id, player);
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

    
    // use for shoot and click events
    private ActionListener actionListener = new ActionListener() {
        public void onAction(String name, boolean keyPressed, float tpf) {
        /** TODO: test for mapping names and implement actions */
            System.out.println("Mapping detected (discrete): "+ name);
            player = players.get(myClient.getId());                   // made player a class variable
            
            // LEFT
            if(name.equals(MAPPING_LEFT) && keyPressed)
            {
                // Set player left velocity
                player.setVelocity(player.getVelocity().setX(-2));
                
            }
            else if(name.equals(MAPPING_LEFT) && !keyPressed)
            {
                // Stop player left velocity
                player.setVelocity(player.getVelocity().setX(0));
            }
            
            // RIGHT
            if(name.equals(MAPPING_RIGHT) && keyPressed)
            {
                // Set player right velocity
                player.setVelocity(player.getVelocity().setX(2));
                
            }
            else if(name.equals(MAPPING_RIGHT) && !keyPressed)
            {
                // Stop player right velocity
                player.setVelocity(player.getVelocity().setX(0));
            }
            
            // UP
            if(name.equals(MAPPING_UP) && keyPressed)
            {
                // Set player up velocity
                player.setVelocity(player.getVelocity().setY(2));
                
            }
            else if(name.equals(MAPPING_UP) && !keyPressed)
            {
                // Stop player up velocity
                player.setVelocity(player.getVelocity().setY(0));
            }
            
            // DOWN
            if(name.equals(MAPPING_DOWN) && keyPressed)
            {
                // Set player down velocity
                player.setVelocity(player.getVelocity().setY(-2));
            }
            else if(name.equals(MAPPING_DOWN) && !keyPressed)
            {
                // Stop player down velocity
                player.setVelocity(player.getVelocity().setY(0));
            }
            
        }
    };
    
    private AnalogListener analogListener = new AnalogListener() {

        public void onAnalog(String name, float intensity, float tpf) {
            System.out.println("Mapping detected (analog): "+ name + " " + intensity );
            
            if (name.equals(MAPPING_ROTATE)) {
                // TODO: add rotate functin to player, rotate the sprite
                // player.rotate(0, intensity * 10f, 0);
            } 
            // Tried to get it to work with intensity but no good
//            else if (name.equals(MAPPING_UP))
//            {
//                // Set player up velocity
//                player.setVelocity(player.getVelocity().setY(1000f*intensity));
//            }
//            else if (name.equals(MAPPING_DOWN))
//            {
//                // Set player up velocity
//                player.setVelocity(player.getVelocity().setY(-1000f*intensity));
//            }
//            else if (name.equals(MAPPING_LEFT))
//            {
//                // Set player up velocity
//                player.setVelocity(player.getVelocity().setX(-1000f*intensity));
//            }
//            else if (name.equals(MAPPING_RIGHT))
//            {
//                // Set player up velocity
//                player.setVelocity(player.getVelocity().setX(1000f*intensity));
//            }
        }
    };
    
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
