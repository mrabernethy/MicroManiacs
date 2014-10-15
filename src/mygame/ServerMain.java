package mygame;

import com.jme3.app.SimpleApplication;
import com.jme3.collision.CollisionResults;
import com.jme3.material.Material;
import com.jme3.math.FastMath;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import com.jme3.network.Network;
import com.jme3.network.Server;
import com.jme3.network.serializing.Serializer;
import com.jme3.renderer.RenderManager;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.shape.Box;
import com.jme3.scene.shape.Sphere;
import com.jme3.system.JmeContext;
import entities.Bullet;
import entities.Player;
import java.io.IOException;
import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Level;

/**
 * test
 * @author normenhansen
 */
public class ServerMain extends SimpleApplication {
    
    public static void main(String[] args) {
        java.util.logging.Logger.getLogger("").setLevel
                (Level.SEVERE);
        ServerMain app = new ServerMain();
        app.start(JmeContext.Type.Headless);
    }
    
    private HashMap<Integer, Player> players = new HashMap();
    private HashMap<Integer, Bullet> bullets = new HashMap();
    private int bulletIDCounter = 0;
    
    private Node world;
    
    private Server myServer;
    int connections = 0;
    int connectionsOld = -1;
    
    @Override
    public void simpleInitApp() 
    {
        Serializer.registerClass(UpdateMessage.class);
        Serializer.registerClass(GreetingMessage.class); 
        Serializer.registerClass(ClientCommandMessage.class); 
        
        try {
            myServer = Network.createServer
                    (Globals.NAME,
                    Globals.VERSION, Globals.DEFAULT_PORT, 
                    Globals.DEFAULT_PORT);
            myServer.start();
        } catch (IOException ex) {}
        

        myServer.addMessageListener(new ServerListener(this, myServer),
                GreetingMessage.class);
        myServer.addMessageListener(new ServerListener(this, myServer),
                UpdateMessage.class);
        myServer.addMessageListener(new ServerListener(this, myServer),
                ClientCommandMessage.class);
        
        initWorld();
        
        Timer updateLoop = new Timer(true);
        updateLoop.scheduleAtFixedRate(new UpdateTimer(), 0, 30);
    }
    
        public void initWorld()
    {
        this.world = new Node();
        
        // Add base
        Box floor = new Box(60f, 30f, 0.1f);
        floor.scaleTextureCoordinates(new Vector2f(1, 1));
        Geometry floor_geo = new Geometry("Floor", floor);

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
        
        Box b = new Box(0.5f,0.5f,0.1f);
        Geometry geom = new Geometry(idStr, b);
        //Material mat = new Material(assetManager, "Common/MatDefs/Misc/ShowNormals.j3md");
        //geom.setMaterial(mat);
        
        Player p = new Player(new Vector3f(2,0,0), geom, id);
        rootNode.attachChild(geom);
        players.put(id, p);
    }
    
    public void addBullet(int owner_id)
    {
        int bullet_id = bulletIDCounter++;
        
        String idStr = "Bullet " + bullet_id + " of player " + owner_id;
        
        Sphere sphere = new Sphere(36, 36, 0.3f);
        Geometry geom = new Geometry(idStr, sphere);
        
        Player shooter = players.get(owner_id);
        Bullet b = new Bullet(shooter.getPosition(), geom, bullet_id,owner_id);

        Quaternion quat = new Quaternion();
        quat.fromAngleAxis(shooter.getRotation().getZ() * FastMath.PI, Vector3f.UNIT_Z);
        Vector3f bulletVelocity = new Vector3f(3, 0 , 0);
        quat.mult(bulletVelocity, bulletVelocity);
      
        b.setVelocity(bulletVelocity);
        rootNode.attachChild(geom);
        bullets.put(bullet_id, b);
    }
    
    @Override
    public void simpleUpdate(float tpf) {
        // TODO: update players?
        // Update players
        System.out.println(players.size());
        
        // Update Bullets
        
        
        // Check collisions

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
            
            // Player collisions/updates
            for(int i = 0; i < players.size(); i++)
            {
                Player p = players.get(i);
                
                boolean collision = false;
                
                // Player - Player Collisions
                for(Player p2 : players.values())
                {
                    CollisionResults results = new CollisionResults();
                    
                    if(!p.equals(p2))
                    {
                        p.getGeometry().collideWith(p2.getGeometry().getWorldBound(), results);
                    }
                    
                    if(results.size() > 0)
                    {
                        //collision = true;
                        p.setVelocity(p.getVelocity().negate());
                    }
                }
                
                // Player - World Objects collisions
                for(Spatial spatial : world.getChildren())
                {
                    CollisionResults results = new CollisionResults();
                    
                    if(!spatial.getName().equals("Floor"))
                        p.getGeometry().collideWith(spatial.getWorldBound(), results);
                    
                    if(results.size() > 0)
                    {
                        //collision = true; 
                        p.setVelocity(p.getVelocity().negate().divide(1.5f));
                    }
                }

                // Update player
                //if(!collision)
                    p.update(0.03f);

                // Broadcast new player info
                myServer.broadcast(new UpdateMessage("Player", p.getPosition(), p.getRotation(), p.getID(), p.getID()));
            }
            
            // Bullet collisions/updates
            for(int i = 0; i < bullets.size(); i++)
            {
                Bullet b = bullets.get(i);
                
                // Bullet - Player collision
                for(Player p : players.values())
                {
                    CollisionResults results = new CollisionResults();
                    b.getGeometry().collideWith(p.getGeometry().getWorldBound(), results);
                    
                    if(results.size() > 0)
                    {
                        //collision = true;
                        //b.setVelocity(new Vector3f(0,0,0));
                    }
                }
                
                for(Spatial spatial : world.getChildren())
                {
                    CollisionResults results = new CollisionResults();
                    
                    if(!spatial.getName().equals("Floor"))
                        b.getGeometry().collideWith(spatial.getWorldBound(), results);
                    
                    if(results.size() > 0)
                    {
                        //collision = true; 
                        //b.setVelocity(new Vector3f(0,0,0));
                    }
                }
                
                b.update(0.03f);
                
                myServer.broadcast(new UpdateMessage("Bullet", b.getPosition(), b.getRotation(), b.getID(), b.getOwnerID()));
            }
            
            
        }

    }

}

