package mygame;

import com.jme3.app.SimpleApplication;
import com.jme3.asset.TextureKey;
import com.jme3.font.BitmapText;
import com.jme3.input.ChaseCamera;
import com.jme3.input.KeyInput;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.AnalogListener;
import com.jme3.input.controls.KeyTrigger;
import com.jme3.input.controls.Trigger;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.FastMath;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import com.jme3.network.Client;
import com.jme3.network.Network;
import com.jme3.network.serializing.Serializer;
import com.jme3.renderer.RenderManager;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.shape.Box;
import com.jme3.scene.shape.Sphere;
import com.jme3.system.AppSettings;
import com.jme3.system.JmeContext;
import com.jme3.texture.Texture;
import entities.Bullet;
import entities.Car;
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
        // Change the default settings
        AppSettings settings = new AppSettings(true);
        settings.setResolution(1024,768);
        settings.setTitle("Micro Maniacs");
        settings.setSettingsDialogImage("Interface/splash.png");
        ClientMain app = new ClientMain();
        app.setSettings(settings);
        app.start();//JmeContext.Type.Display);
    }
  
    /** Prepare geometries and physical nodes*/
    private Node world;
    
    private Client myClient;
 
    private Vector3f topY;
    private Vector3f bottomY;
    private Vector3f leftX;
    private Vector3f rightX;
       
    // Chase camera
    private ChaseCamera chaseCamera;
    
    // HUD elements
    BitmapText lifeText;
    
    private Player player;
    private HashMap<Integer, Player> players = new HashMap();
    private HashMap<Integer, Bullet> bullets = new HashMap();
    private HashMap<Integer, Car> cars = new HashMap();
    
    private final static Trigger TRIGGER_W = new KeyTrigger(KeyInput.KEY_W);
    private final static Trigger TRIGGER_S = new KeyTrigger(KeyInput.KEY_S);
    private final static Trigger TRIGGER_A = new KeyTrigger(KeyInput.KEY_A);
    private final static Trigger TRIGGER_D = new KeyTrigger(KeyInput.KEY_D);
    private final static Trigger TRIGGER_UP = new KeyTrigger(KeyInput.KEY_UP);
    private final static Trigger TRIGGER_DOWN = new KeyTrigger(KeyInput.KEY_DOWN);
    private final static Trigger TRIGGER_LEFT = new KeyTrigger(KeyInput.KEY_LEFT);
    private final static Trigger TRIGGER_RIGHT = new KeyTrigger(KeyInput.KEY_RIGHT);
    // TODO: add shoot -- on click or space?
    private final static Trigger TRIGGER_SPACE = new KeyTrigger(KeyInput.KEY_SPACE);
    private final static Trigger TRIGGER_E = new KeyTrigger(KeyInput.KEY_E);
    
    private final static String  MAPPING_UP = "Up";
    private final static String  MAPPING_DOWN = "Down";
    private final static String  MAPPING_LEFT = "Left";
    private final static String  MAPPING_RIGHT = "Right";
    private final static String  MAPPING_SHOOT = "Shoot";
    private final static String  MAPPING_INTERACT = "Interact";
    
    @Override
    public void simpleInitApp() 
    { 
        Serializer.registerClass(UpdateMessage.class);
        Serializer.registerClass(GreetingMessage.class); 
        Serializer.registerClass(ClientCommandMessage.class); 
        
        try {
            myClient = Network.connectToServer (Globals.NAME,
                    Globals.VERSION, Globals.DEFAULT_SERVER, 
                    Globals.DEFAULT_PORT);
            myClient.start();
        } catch (IOException ex) {}
        
        // Register the message classes
        
        // Add the message listeners
        myClient.addMessageListener(new ClientListener(this, myClient),
                GreetingMessage.class);
        myClient.addMessageListener(new ClientListener(this, myClient),
                UpdateMessage.class);
        myClient.addMessageListener(new ClientListener(this, myClient),
                ClientCommandMessage.class);
        //myClient.addClientStateListener(this);
        
        // Init Mappings and Listeners
        inputManager.addMapping(MAPPING_UP, TRIGGER_W, TRIGGER_UP);
        inputManager.addMapping(MAPPING_DOWN, TRIGGER_S, TRIGGER_DOWN);
        inputManager.addMapping(MAPPING_LEFT, TRIGGER_A, TRIGGER_LEFT);
        inputManager.addMapping(MAPPING_RIGHT, TRIGGER_D, TRIGGER_RIGHT);
        // TODO: add mapping for mouse buttons
        inputManager.addMapping(MAPPING_SHOOT, TRIGGER_SPACE);
        inputManager.addMapping(MAPPING_INTERACT, TRIGGER_E);
        
        // TODO: add space mapping to listener
        // Listener for click events ie. shoot bullet
        inputManager.addListener(actionListener, new String[]{MAPPING_UP, MAPPING_DOWN, 
            MAPPING_LEFT, MAPPING_RIGHT, MAPPING_SHOOT, MAPPING_INTERACT});
        // Listener for push events ie. move up
        inputManager.addListener(analogListener, new String[]{MAPPING_UP, MAPPING_DOWN, 
            MAPPING_LEFT, MAPPING_RIGHT});
        
        // Stop the client pausing the game
        setPauseOnLostFocus(false);
        // Hide FPS value
        setDisplayFps(false);
        // Hide debugging stats
        setDisplayStatView(false);
        
        // Set cursor visible
        inputManager.setCursorVisible(true);
        
        // Message to send to the server.
        myClient.send(new GreetingMessage("Hi Server! Do you hear me?"));
                
        // Add player
        addPlayer(myClient.getId());
        player = (players.get(myClient.getId()));
        myClient.send(new ClientCommandMessage(ClientCommand.ADD_PLAYER, player.getRotation(), myClient.getId()));
        
        // Stop the camera moving
        this.flyCam.setEnabled(false);
        
        // Attach the chase cam to the player
        chaseCamera = new ChaseCamera(cam);
        chaseCamera.setDefaultHorizontalRotation(FastMath.PI/2);
        chaseCamera.setDefaultVerticalRotation(0);
        player.getGeometry().addControl(chaseCamera);
        
        /** Initialize the scene*/
        initHUD();
        initWorld();
//        initCrossHairs();
    }
    
    /**
     * Initialises the HUD components
     * 
     */
    public void initHUD()
    {
        lifeText = new BitmapText(guiFont, false);
        lifeText.setSize(guiFont.getCharSet().getRenderedSize());
        lifeText.setColor(ColorRGBA.Red);
        lifeText.setLocalTranslation(0,768,0);
        lifeText.setText("Life:");
        guiNode.attachChild(lifeText);
    }
    
    /**
     * Initialises the world node with buildings
     * 
     */
    public void initWorld()
    {
        this.world = new Node();
        
        // Add base
        Box floor = new Box(60f, 30f, 0.1f);
        floor.scaleTextureCoordinates(new Vector2f(1, 1));
        Geometry floor_geo = new Geometry("Floor", floor);
        
        Material floor_mat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        TextureKey key3 = new TextureKey("Textures/testWorld.jpg");                                     //Terrain/Pond/Pond.jpg");
        key3.setGenerateMips(true);
        Texture tex3 = assetManager.loadTexture(key3);
        tex3.setWrap(Texture.WrapMode.Repeat);
        floor_mat.setTexture("ColorMap", tex3);
        
        floor_geo.setMaterial(floor_mat);
        floor_geo.setLocalTranslation(0, 0, -0.1f);
        this.world.attachChild(floor_geo);
        
        // Add Buildings
        // Just for testing/will want to generate a more organised world later
        for(int x = -3; x < 3; x++)
            for(int y = -2; y < 2; y++)
            {
                Box building = new Box(0.7f, 0.7f, 0.7f);
                Geometry building_geo = new Geometry("Building:" + x + "/" + y, building);
                
                Material building_mat = new Material(assetManager, "Common/MatDefs/Misc/ShowNormals.j3md");
                building_geo.setMaterial(building_mat);
                building_geo.setLocalTranslation(x*6, y*6, 0);
                this.world.attachChild(building_geo);
            }
        
        // Attach world
        this.rootNode.attachChild(world);
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
    
    /**
     * Creates a new player and puts it in the players hashmap
     * 
     * @param id 
     */
    public void addPlayer(int id)
    {
        String idStr = "Player " + Integer.toString(id);
        
        Box b = new Box(0.5f,0.5f,0.1f);
        Geometry geom = new Geometry(idStr, b);
        Material mat = new Material(assetManager, "Common/MatDefs/Misc/ShowNormals.j3md");
        geom.setMaterial(mat);
        
        Player p = new Player(new Vector3f(2,0,0), geom, id);
        rootNode.attachChild(geom);
        players.put(id, p);
        
    }
    
    /**
     * Checks if a player exists
     * 
     * @param id
     * @return true if player exists
     */
    public boolean playerExists(int id)
    {
        return players.containsKey(id);
    }
    
//    // TODO: update function
//    public void removePlayer(int id)
//    {
//        //players.remove(id).getSprite().delete();
//    }
    
    /**
     * Adds a new bullet owned by a certain player
     * 
     * @param owner_id
     * @param bullet_id 
     */
    public void addBullet(int owner_id, int bullet_id)
    {
        String idStr = "Bullet " + bullet_id +" of player " + owner_id;
        
        Sphere sphere = new Sphere(32,32, 0.2f);
        Geometry geom = new Geometry(idStr, sphere);
        Material mat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        mat.setColor("Color", ColorRGBA.Blue);
        geom.setMaterial(mat);
        
        Player shooter = players.get(owner_id);
        Bullet b = new Bullet(shooter.getPosition(), geom, bullet_id, owner_id);
        rootNode.attachChild(geom);
        bullets.put(bullet_id, b);
    }
    
    /**
     * Adds a car at a certain position
     * 
     * @param id
     * @param position
     */
    public void addCar(int id, Vector3f position)
    {
        String idStr = "Car " + id;
        
        Box b = new Box(0.8f, 0.5f, 0.1f);
        Geometry geom = new Geometry(idStr, b);
        Material mat = new Material(assetManager, "Common/MatDefs/Misc/ShowNormals.j3md");
        geom.setMaterial(mat);
        
        Car c = new Car(position, geom, id);
        rootNode.attachChild(geom);
        cars.put(id, c);
    }
    
    public boolean carExists(int id)
    {
        return cars.containsKey(id);
    }
    
    public boolean bulletExists(int bullet_id)
    {
        return bullets.containsKey(bullet_id);
    }
    
    public void updateBullet(int bullet_id, Vector3f position, boolean alive)
    {
        bullets.get(bullet_id).setPosition(position);
        
        if(!alive)
        {
            rootNode.detachChild(bullets.get(bullet_id).getGeometry());
        }
    }
    
    public void updateCar(int id, Vector3f position, Quaternion rotation, int riderID, boolean alive)
    {
        Car c = cars.get(id);
        
        c.setPosition(position);
        c.setRotation(rotation);
        c.setRiderID(riderID);
        c.setAlive(alive);
    }
    
    public void updateEntity(String updateMessage)
    {   
        String[] split = updateMessage.split("&");
        
        if(split[0].equals("Player"))
        {
            // Retrieve Player info
            int id = Integer.parseInt(split[1]);
            String[] posSplit = split[2].replace("(", "").replace(")", "").split(",");
            Vector3f position = new Vector3f(   Float.parseFloat(posSplit[0].trim()),
                                                Float.parseFloat(posSplit[1].trim()),
                                                Float.parseFloat(posSplit[2].trim()));
            String[] rotSplit = split[3].replace("(", "").replace(")", "").split(",");
            Quaternion rotation = new Quaternion(   Float.parseFloat(rotSplit[0].trim()),
                                                    Float.parseFloat(rotSplit[1].trim()),
                                                    Float.parseFloat(rotSplit[2].trim()),
                                                    Float.parseFloat(rotSplit[3].trim()));
            boolean alive = Boolean.parseBoolean(split[4]);
            int life = Integer.parseInt(split[5]);
            int currentVehicleID = Integer.parseInt(split[6]);
            long lastAttackTime = Long.parseLong(split[7]);
            Weapon weapon = Weapon.valueOf(split[8]);
            
            // Add player if it doesn't exist
            if(!playerExists(id))
            {
                addPlayer(id);
            }
            
            Player p = players.get(id);
        
            p.setPosition(position);
            p.setRotation(rotation);
            p.setAlive(alive);
            p.setLife(life);
            p.setCurrentVehicleID(currentVehicleID);
            p.setLastAttackTime(lastAttackTime);
            p.setWeapon(weapon);
        }
        else if(split[0].equals("Bullet"))
        {
            int id = Integer.parseInt(split[1]);
            int ownerID = Integer.parseInt(split[2]);
            String[] posSplit = split[3].replace("(", "").replace(")", "").split(",");
            Vector3f position = new Vector3f(   Float.parseFloat(posSplit[0].trim()),
                                                Float.parseFloat(posSplit[1].trim()),
                                                Float.parseFloat(posSplit[2].trim()));
            boolean alive = Boolean.parseBoolean(split[4]);
            
            if(!bulletExists(id))
            {
                addBullet(ownerID, id);
            }
            
            updateBullet(id, position, alive);
        }
        else if(split[0].equals("Car"))
        {
            int id = Integer.parseInt(split[1]);
            int riderID = Integer.parseInt(split[2]);
            String[] posSplit = split[3].replace("(", "").replace(")", "").split(",");
            Vector3f position = new Vector3f(   Float.parseFloat(posSplit[0].trim()),
                                                Float.parseFloat(posSplit[1].trim()),
                                                Float.parseFloat(posSplit[2].trim()));
            String[] rotSplit = split[4].replace("(", "").replace(")", "").split(",");
            Quaternion rotation = new Quaternion(   Float.parseFloat(rotSplit[0].trim()),
                                                    Float.parseFloat(rotSplit[1].trim()),
                                                    Float.parseFloat(rotSplit[2].trim()),
                                                    Float.parseFloat(rotSplit[3].trim()));
            boolean alive = Boolean.parseBoolean(split[5]);
            
            if(!carExists(id))
            {
                addCar(id, position);
            }
            
            updateCar(id, position, rotation, riderID, alive);
        }
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
        //myClient.send(new ClientCommandMessage(ClientCommand.ROTATE,player.getRotation(), myClient.getId()));
        // Update HUD
        lifeText.setText("Life: " + player.getLife());
        
        
        // Rotate box to look at mouse cursor
        Vector2f mousePos = inputManager.getCursorPosition();
        Vector3f rotPos = new Vector3f(cam.getScreenCoordinates(player.getGeometry().getLocalTranslation()));
        Vector2f relativePos = new Vector2f(mousePos.x-rotPos.x,mousePos.y-rotPos.y);
        float angleRads = FastMath.atan2(relativePos.y, relativePos.x);
        Quaternion playerRotation = new Quaternion().fromAngles( 0, 0, angleRads );
        player.setRotation(playerRotation);
        
        //collisionWithWall();
        // Send this players position every x movement distance
//        if(players.get(myClient.getId()).getPosition().distance(lastSentPosition) > 0.05)
//        {
//            lastSentPosition = new Vector3f(players.get(myClient.getId()).getPosition());
//            myClient.send(new ClientMessage(player.getPosition(), player.getRotation(), myClient.getId()));
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
            
            System.out.println("Client Closed");
        } catch (Exception ex) {}
        super.destroy();
    }

    
    // use for mouse rotation and maybe vehicle acceleration
    private AnalogListener analogListener = new AnalogListener() {
        public void onAnalog(String name, float intensity, float tpf) {
            //System.out.println("Mapping detected (analog): "+ name + " " + intensity );
            
        }
    };
    
    // use for shoot and click events
    private ActionListener actionListener = new ActionListener() {
        public void onAction(String name, boolean keyPressed, float tpf) {
        /** TODO: test for mapping names and implement actions */
            System.out.println("Mapping detected (discrete): "+ name);
            if(myClient.isConnected())
            {
                // Get key pressed
                // Send command
                // Update rotation each time a command is sent
                
                if (name.equals(MAPPING_UP) && keyPressed)
                {
                    // Set player up velocity
                    //player.setVelocity(player.getVelocity().setY(2));
                    //player.getGeometry().move(0,0.003f,0);
                    myClient.send(new ClientCommandMessage(ClientCommand.MOVE_UP, player.getRotation(), myClient.getId()));
                }
                if(name.equals(MAPPING_DOWN)&& keyPressed)
                {
                    myClient.send(new ClientCommandMessage(ClientCommand.MOVE_DOWN, player.getRotation(), myClient.getId()));
                }
                if(name.equals(MAPPING_LEFT)&& keyPressed)
                {
                    myClient.send(new ClientCommandMessage(ClientCommand.MOVE_LEFT, player.getRotation(), myClient.getId()));
                }
                if(name.equals(MAPPING_RIGHT)&& keyPressed)
                {
                    myClient.send(new ClientCommandMessage(ClientCommand.MOVE_RIGHT, player.getRotation(), myClient.getId()));
                }
                if((name.equals(MAPPING_UP) || name.equals(MAPPING_DOWN)) && !keyPressed)
                {
                    myClient.send(new ClientCommandMessage(ClientCommand.STOP_MOVE_UP_DOWN, player.getRotation(), myClient.getId()));
                }
                if((name.equals(MAPPING_LEFT) || name.equals(MAPPING_RIGHT)) && !keyPressed)
                {
                    myClient.send(new ClientCommandMessage(ClientCommand.STOP_MOVE_LEFT_RIGHT, player.getRotation(), myClient.getId()));
                }
                if(name.equals(MAPPING_SHOOT) && keyPressed)
                {
                    myClient.send(new ClientCommandMessage(ClientCommand.SHOOT, player.getRotation(), myClient.getId()));
                }
                if(name.equals(MAPPING_INTERACT) && keyPressed)
                {
                    myClient.send(new ClientCommandMessage(ClientCommand.INTERACT, player.getRotation(), myClient.getId()));
                }
            }
        }
    };

}