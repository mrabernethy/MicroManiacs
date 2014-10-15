package mygame;

import com.jme3.app.SimpleApplication;
import com.jme3.bullet.BulletAppState;
import com.jme3.bullet.collision.shapes.CapsuleCollisionShape;
import com.jme3.bullet.collision.shapes.PlaneCollisionShape;
import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.math.Plane;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import com.jme3.network.Network;
import com.jme3.network.Server;
import com.jme3.network.serializing.Serializer;
import com.jme3.renderer.RenderManager;
import com.jme3.scene.Geometry;
import com.jme3.scene.shape.Box;
import com.jme3.system.JmeContext;
import entities.Player;
import java.io.IOException;
import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;
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
    
    private HashMap<Integer, Player> players = new HashMap();
    
    
    /** Prepare geometries and physical nodes for floor and player*/
    private static final Box    box;
    private RigidBodyControl    floor_phy;
    private static final Box    floor;
    private RigidBodyControl    test_phy;
    private static final Box    test_box;
    
    /** Dimensions used for player box */
    private static final float playerLength = 0.5f;
    private static final float playerWidth  = 0.5f;
    private static final float playerHeight = 0.5f;
    
    static {
    /** Initialize the player geometry */
    box = new Box(playerLength, playerHeight, playerWidth);
    box.scaleTextureCoordinates(new Vector2f(1f, .5f));
    /** Initialize the floor geometry */
    floor = new Box(10,10,0.1f);                                                // TODO: not sure why player doesn't fall off floor
    floor.scaleTextureCoordinates(new Vector2f(1f, 1f));                            // Don't think the texture perfectly covers the floor box
    /** Initialize the test box */
    test_box = new Box(1,1,1);
    test_box.scaleTextureCoordinates(new Vector2f(1f, .5f));
    }
    
    private Server myServer;
    int connections = 0;
    int connectionsOld = -1;
    
    private BulletAppState bulletAppState;
    
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
        
        bulletAppState = new BulletAppState();
        stateManager.attach(bulletAppState);
        //bulletAppState.getPhysicsSpace().enableDebug(assetManager);
        bulletAppState.getPhysicsSpace().setGravity(new Vector3f(0,0,-9.8f));
        
        Serializer.registerClass(ClientMessage.class);
        Serializer.registerClass(GreetingMessage.class); 
        Serializer.registerClass(ClientCommandMessage.class); 

        myServer.addMessageListener(new ServerListener(this, myServer),
                GreetingMessage.class);
        myServer.addMessageListener(new ServerListener(this, myServer),
                ClientMessage.class);
        myServer.addMessageListener(new ServerListener(this, myServer),
                ClientCommandMessage.class);
        
        Timer updateLoop = new Timer(true);
        updateLoop.scheduleAtFixedRate(new UpdateTimer(), 0, 50);

    }
    
//    public void initFloor() {
//        Geometry floor_geo = new Geometry("Floor", floor);
//        floor_geo.setMaterial(floor_mat);
//        floor_geo.setLocalTranslation(0, 0, -1f);
//        floor_geo.setLocalScale(10, 10, 0.1f);
//        
//        /* Make the floor physical with mass 0.0f! */
//        PlaneCollisionShape plane = new PlaneCollisionShape(new Plane(new Vector3f(0,0,1),0));
//        floor_phy = new RigidBodyControl(plane, 0.0f);
//        floor_geo.addControl(floor_phy);
//        
//        this.rootNode.attachChild(floor_geo);
//        bulletAppState.getPhysicsSpace().add(floor_phy);
//    }

    @Override
    public void update()
    {
        connections = myServer.getConnections().size();
        if (connectionsOld != connections) {
            System.out.println("Server connections: " + connections);
            connectionsOld = connections;
        }
        
        //System.out.println(players.size());
        
    }
    
    public void removePlayer(int id)
    {
        // TODO: Remove player from array/rootNode/etc
        
    }
    
    public Player getPlayer(int id)
    {
        return players.get(id);
    }
    
    public boolean playerExists(int id)
    {
        return players.containsKey(id);
    }
    
    public void addPlayer(int id)
    {
        String idStr = "Player " + Integer.toString(id);
        
        /** Create a player geometry and attach to scene graph. */
        Geometry player_geo = new Geometry(idStr, box);
        rootNode.attachChild(player_geo);
        /** Position the player geometry  */                                        // TODO: set the initial or respawned position
        //player_geo.setLocalTranslation(loc);
        /** Make player physical with a mass > 0.0f. */
        //player_phy = new RigidBodyControl();
        CapsuleCollisionShape capsuleShape = new CapsuleCollisionShape(0.5f, 0.5f, 2);
        RigidBodyControl player_phy = new RigidBodyControl(capsuleShape, 50f);
        player_phy.setAngularFactor(0f);
        //player_phy.setKinematic(true);

//        player_phy.setPhysicsLocation(new Vector3f(0, 10, 0));
        /** Add physical player to physics space. */
        player_geo.addControl(player_phy);
        bulletAppState.getPhysicsSpace().add(player_phy);
        
        // Create a new player and add to collection
        Player p = new Player(new Vector3f(0,0,0), player_geo, id);                     // TODO: Cane remove vector from player class?
        players.put(id, p);
    }
    
    public void updatePlayer(int id, Vector3f position, Quaternion rotation)
    {
        players.get(id).setPosition(position);
        players.get(id).setRotation(rotation);
        
    }
    
    // This loop doesn't work for some reason
    @Override
    public void simpleUpdate(float tpf) {
        // TODO: update players?
        // Update players


    }

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
    
    public class UpdateTimer extends TimerTask
    {

        @Override
        public void run() {
            for(Player p : players.values())
            {
               myServer.broadcast(new ClientMessage(p.getPosition(), p.getRotation(), p.getID()));
            }
            
            
        }

    }

}
