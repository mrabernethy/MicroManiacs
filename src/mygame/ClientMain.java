package mygame;

import com.jme3.app.SimpleApplication;
import com.jme3.asset.TextureKey;
import com.jme3.bullet.BulletAppState;
import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.font.BitmapText;
import com.jme3.input.ChaseCamera;
import com.jme3.input.KeyInput;
import com.jme3.input.MouseInput;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.AnalogListener;
import com.jme3.input.controls.KeyTrigger;
import com.jme3.input.controls.MouseAxisTrigger;
import com.jme3.input.controls.Trigger;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.FastMath;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import com.jme3.network.Client;
import com.jme3.network.ClientStateListener.DisconnectInfo;
import com.jme3.network.Network;
import com.jme3.network.serializing.Serializer;
import com.jme3.renderer.RenderManager;
import com.jme3.scene.Geometry;
import com.jme3.scene.shape.Box;
import com.jme3.scene.shape.Sphere;
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
public class ClientMain extends SimpleApplication {
    
    public static void main(String[] args) {
        java.util.logging.Logger.getLogger("").setLevel(Level.SEVERE);
        ClientMain app = new ClientMain();
        app.start(JmeContext.Type.Display);
    }
    
    /** Prepare the Physics Application State (jBullet) */
    private BulletAppState bulletAppState;
  
    /** Prepare Materials */
    Material floor_mat;
    
    /** Prepare geometries and physical nodes*/
    private RigidBodyControl    floor_phy;
    private static final Box    floor;
    
    static {
    /** Initialize the floor geometry */
    floor = new Box(10f, 10f, 0.1f);
    floor.scaleTextureCoordinates(new Vector2f(1, 1));
    }
    
    private Client myClient;
    private Vector3f lastSentPosition;
    private Vector3f topY;
    private Vector3f bottomY;
    private Vector3f leftX;
    private Vector3f rightX;
       
    // Chase camera
    private ChaseCamera chaseCamera;
    
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
    // TODO: add shoot -- on click or space?
    //private final static Trigger TRIGGER_SPACE = new KeyTrigger(KeyInput.KEY_SPACE);
    
    private final static String  MAPPING_UP = "Up";
    private final static String  MAPPING_DOWN = "Down";
    private final static String  MAPPING_LEFT = "Left";
    private final static String  MAPPING_RIGHT = "Right";
    //private final static String  MAPPING_SHOOT = "Shoot";
    
    @Override
    public void simpleInitApp() 
    { 
        try {
            myClient = Network.connectToServer (Globals.NAME,
                    Globals.VERSION, Globals.DEFAULT_SERVER, 
                    Globals.DEFAULT_PORT);
            myClient.start();
        } catch (IOException ex) {}
        
        /** Set up Physics Game */
        bulletAppState = new BulletAppState();
        stateManager.attach(bulletAppState);
        //bulletAppState.getPhysicsSpace().enableDebug(assetManager);
        
        // Init Mappings and Listeners
        inputManager.addMapping(MAPPING_UP, TRIGGER_W, TRIGGER_UP);
        inputManager.addMapping(MAPPING_DOWN, TRIGGER_S, TRIGGER_DOWN);
        inputManager.addMapping(MAPPING_LEFT, TRIGGER_A, TRIGGER_LEFT);
        inputManager.addMapping(MAPPING_RIGHT, TRIGGER_D, TRIGGER_RIGHT);
        // TODO: add mapping for mouse buttons
        //inputManager.addMapping(MAPPING_SHOOT, TRIGGER_SPACE);
        
        // TODO: add space mapping to listener
        // Listener for click events ie. shoot bullet
        inputManager.addListener(actionListener, new String[]{MAPPING_UP, MAPPING_DOWN, 
            MAPPING_LEFT, MAPPING_RIGHT});
        // Listener for push events ie. move up
        inputManager.addListener(analogListener, new String[]{MAPPING_UP, MAPPING_DOWN, 
            MAPPING_LEFT, MAPPING_RIGHT});
        
        // Set cursor visible
        inputManager.setCursorVisible(true);
//        JmeCursor c = new JmeCursor();
//        IntBuffer image = new IntBuffer
//        inputManager.setMouseCursor(JmeCursor.class.);
        
//        spriteManager = new SpriteManager(1024, 1024, SpriteMesh.Strategy.ALLOCATE_NEW_BUFFER, rootNode, assetManager);
//        getStateManager().attach(spriteManager);
        
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
        
        
        
        myClient.send(new ClientMessage(this.cam.getLocation(), this.cam.getRotation(), myClient.getId()));
        lastSentPosition = new Vector3f(this.cam.getLocation());
        
        
        
        // TODO: attach the world
        //attachBackground();
        
        // Add player
        addPlayer(myClient.getId());
        player = (players.get(myClient.getId()));
        
        // Stop the camera moving
        this.flyCam.setEnabled(false);
        
        // Attach the chase cam to the player
        chaseCamera = new ChaseCamera(cam);
        chaseCamera.setDefaultHorizontalRotation(FastMath.PI/2);
        chaseCamera.setDefaultVerticalRotation(0);
        player.getGeometry().addControl(chaseCamera);
        
        /** Initialize the scene, materials, and physics space */
        initMaterials();
        initFloor();
//        initCrossHairs();
    }
    
    /** Initialize the materials used in this scene. */
    public void initMaterials() {
        floor_mat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        TextureKey key3 = new TextureKey("Textures/testWorld.jpg");                                     //Terrain/Pond/Pond.jpg");
        key3.setGenerateMips(true);
        Texture tex3 = assetManager.loadTexture(key3);
        tex3.setWrap(Texture.WrapMode.Repeat);
        floor_mat.setTexture("ColorMap", tex3);
    }
    
    /** Make a solid floor and add it to the scene. */
    public void initFloor() {
        Geometry floor_geo = new Geometry("Floor", floor);
        floor_geo.setMaterial(floor_mat);
        floor_geo.setLocalTranslation(0, 0, -0.1f);
        this.rootNode.attachChild(floor_geo);
        /* Make the floor physical with mass 0.0f! */
        floor_phy = new RigidBodyControl(0.0f);
        floor_geo.addControl(floor_phy);
        bulletAppState.getPhysicsSpace().add(floor_phy);
    }
    
//    /** A plus sign used as crosshairs to help the player with aiming.*/
//    protected void initCrossHairs() {
//        guiNode.detachAllChildren();
//        guiFont = assetManager.loadFont("Interface/Fonts/Default.fnt");
//        BitmapText ch = new BitmapText(guiFont, false);
//        ch.setSize(guiFont.getCharSet().getRenderedSize() * 2);
//        ch.setText("+");        // fake crosshairs :)
////        ch.setLocalTranslation( // center
////          settings.getWidth() / 2 - guiFont.getCharSet().getRenderedSize() / 3 * 2,
////          settings.getHeight() / 2 + ch.getLineHeight() / 2, 0);
//        Vector2f mousePos = inputManager.getCursorPosition();
//        //Vector3f rotPos = new Vector3f(cam.getScreenCoordinates(player.getGeometry().getLocalTranslation()));
//        Vector3f relativePos = new Vector3f(mousePos.x,mousePos.y,0);
//        
//        ch.setLocalTranslation(relativePos);
//        guiNode.attachChild(ch);
//    }
       
    public void addPlayer(int id)
    {
        String idStr = "Player " + Integer.toString(id);
        
        Box b = new Box(0.5f,0.5f,0.1f);
        Geometry geom = new Geometry(idStr, b);
        Material mat = new Material(assetManager, "Common/MatDefs/Misc/ShowNormals.j3md");
        geom.setMaterial(mat);
        
        Player p = new Player(new Vector3f(0,0,0), geom);
        rootNode.attachChild(geom);
        players.put(id, p);
    }
    
    public boolean playerExists(int id)
    {
        return players.containsKey(id);
    }
    
    public void updatePlayer(int id, Vector3f position, Quaternion rotation)
    {
        players.get(id).setPosition(position);
        players.get(id).setRotation(rotation);
    }
    
    // TODO: update function
    public void removePlayer(int id)
    {
        //players.remove(id).getSprite().delete();
    }
    
    public void collisionWithWall()
    {
        topY = new Vector3f(player.getPosition());
        topY.setY(3.9f);
        bottomY = new Vector3f(player.getPosition());
        bottomY.setY(-3.9f);
        rightX = new Vector3f(player.getPosition());
        rightX.setX(5.24f);
        leftX = new Vector3f(player.getPosition());
        leftX.setX(-5.24f);
        if (player.getPosition().getY() > 3.9) {
            player.setPosition(topY);
        } else if (player.getPosition().getY() < -3.9) {
            player.setPosition(bottomY);
        } else if (player.getPosition().getX() > 5.24) {
            player.setPosition(rightX);
        } else if (player.getPosition().getX() < -5.24) {
            player.setPosition(leftX);
        }
        
    }

    @Override
    public void simpleUpdate(float tpf) {
        //TODO: add update code
        
        // TODO: update players?
        // Update players
//        for(Player p : players.values())
//        {
//            p.update(tpf);
//        }
        
        // Update camera
        
        // Rotate box to look at mouse cursor
        Vector2f mousePos = inputManager.getCursorPosition();
        Vector3f rotPos = new Vector3f(cam.getScreenCoordinates(player.getGeometry().getLocalTranslation()));
        Vector2f relativePos = new Vector2f(mousePos.x-rotPos.x,mousePos.y-rotPos.y);
        float angleRads = FastMath.atan2(relativePos.y, relativePos.x);
        Quaternion playerRotation = new Quaternion().fromAngles( 0, 0, angleRads );
        player.setRotation(playerRotation);
        
        collisionWithWall();
        // Send this players position every x movement distance
//        if(players.get(myClient.getId()).getPosition().distance(lastSentPosition) > 0.05)
//        {
//            lastSentPosition = new Vector3f(players.get(myClient.getId()).getPosition());
            myClient.send(new ClientMessage(player.getPosition(), player.getRotation(), myClient.getId()));
//        }
            
//            initCrossHairs();
      
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

    
    // use for mouse rotation and maybe vehicle acceleration
    private AnalogListener analogListener = new AnalogListener() {
        public void onAnalog(String name, float intensity, float tpf) {
            System.out.println("Mapping detected (analog): "+ name + " " + intensity );
            
            if (name.equals(MAPPING_UP))
            {
                // Set player up velocity
                //player.setVelocity(player.getVelocity().setY(2));
                player.getGeometry().move(0,0.003f,0);
                
            }
            
            
            if (name.equals(MAPPING_DOWN))
            {
                // Set player down velocity
                //player.setVelocity(player.getVelocity().setY(-2));
                player.getGeometry().move(0,-0.003f,0);
                
            }
            
            
            if (name.equals(MAPPING_LEFT))
            {
                // Set player left velocity
                //player.setVelocity(player.getVelocity().setX(-2));
                player.getGeometry().move(-0.003f,0,0);
            }
            
            
            if (name.equals(MAPPING_RIGHT))
            {
                // Set player right velocity
                //player.setVelocity(player.getVelocity().setX(2));
                player.getGeometry().move(0.003f,0,0);
            }
            
        }
    };
    
    // use for shoot and click events
    private ActionListener actionListener = new ActionListener() {
        public void onAction(String name, boolean keyPressed, float tpf) {
        /** TODO: test for mapping names and implement actions */
            System.out.println("Mapping detected (discrete): "+ name);
            player = players.get(myClient.getId());                   // made player a class variable
            
            // UP
            if(name.equals(MAPPING_UP) && !keyPressed)
            {
                // Set player left velocity
                player.setVelocity(player.getVelocity().setY(0));
            }
            // DOWN
            if(name.equals(MAPPING_DOWN) && !keyPressed)
            {
                // Set player right velocity
                player.setVelocity(player.getVelocity().setY(0));
            }
            // LEFT
            if(name.equals(MAPPING_LEFT) && !keyPressed)
            {
                // Set player up velocity
                player.setVelocity(player.getVelocity().setX(0));
            }
            // RIGHT
            if(name.equals(MAPPING_RIGHT) && !keyPressed)
            {
                // Set player down velocity
                player.setVelocity(player.getVelocity().setX(0));
            }
        }
    };

}