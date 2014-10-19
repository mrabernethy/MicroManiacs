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
import entities.Car;
import entities.Player;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;
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
    private HashMap<Integer, Car> cars = new HashMap();
    
    private Node world;
    private ArrayList<Vector3f> possiblePlayerSpawns = new ArrayList();
    private ArrayList<Vector3f> possibleCarSpawns = new ArrayList();
    
    private Server myServer;
    int connections = 0;
    int connectionsOld = -1;
    
    @Override
    public void simpleInitApp() 
    {
        initPossiblePlayerSpawns();
        initPossibleCarSpawns();
        
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
        initCars();
        
        Timer updateLoop = new Timer(true);
        updateLoop.scheduleAtFixedRate(new UpdateTimer(), 0, 30);
    }
    
    public void initPossiblePlayerSpawns()
    {
        possiblePlayerSpawns.add(new Vector3f(2, 0, 0));
        possiblePlayerSpawns.add(new Vector3f(10, 24, 0));
        possiblePlayerSpawns.add(new Vector3f(32, 18, 0));
        possiblePlayerSpawns.add(new Vector3f(45, -8, 0));
        possiblePlayerSpawns.add(new Vector3f(16,-16, 0));
        possiblePlayerSpawns.add(new Vector3f(-25, -8, 0));
        possiblePlayerSpawns.add(new Vector3f(-56, -7, 0));
        possiblePlayerSpawns.add(new Vector3f(-42, 24, 0));
    }
    
    public void initPossibleCarSpawns()
    {
        possibleCarSpawns.add(new Vector3f(3.5f, 0, 0));
//        possibleCarSpawns.add(new Vector3f(10, 24, 0));
//        possibleCarSpawns.add(new Vector3f(32, 18, 0));
//        possibleCarSpawns.add(new Vector3f(45, -8, 0));
//        possibleCarSpawns.add(new Vector3f(16,-16, 0));
//        possibleCarSpawns.add(new Vector3f(-25, -8, 0));
//        possibleCarSpawns.add(new Vector3f(-56, -7, 0));
//        possibleCarSpawns.add(new Vector3f(-42, 24, 0));
    }
    
    public Vector3f getRandomPlayerSpawn()
    {
        Random rnd = new Random();
        
        return possiblePlayerSpawns.get(rnd.nextInt(possiblePlayerSpawns.size()));
    }
    
    public void initCars()
    {
        for(int i = 0; i < possibleCarSpawns.size(); i++)
        {
            addCar(i, possibleCarSpawns.get(i));
        }
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
    
    public Car getCar(int id)
    {
        return cars.get(id);
    }
    
    public boolean playerExists(int id)
    {
        return players.containsKey(id);
    }
    
    public void addCar(int id, Vector3f position)
    {
        String idStr = "Car " + id;
        
        Box b = new Box(0.8f, 0.5f, 0.1f);
        Geometry geom = new Geometry(idStr, b);
        //Material mat = new Material(assetManager, "Common/MatDefs/Misc/ShowNormals.j3md");
        //geom.setMaterial(mat);
        
        Car c = new Car(position, geom, id);
        rootNode.attachChild(geom);
        cars.put(id, c);
    }
    
    public void addPlayer(int id)
    {
        String idStr = "Player " + Integer.toString(id);
        
        Box b = new Box(0.5f,0.5f,0.1f);
        Geometry geom = new Geometry(idStr, b);
        //Material mat = new Material(assetManager, "Common/MatDefs/Misc/ShowNormals.j3md");
        //geom.setMaterial(mat);
        
        Player p = new Player(getRandomPlayerSpawn(), geom, id);
        rootNode.attachChild(geom);
        players.put(id, p);
    }
    
    public void addBullet(int owner_id)
    {
        int bullet_id = bulletIDCounter++;
        
        String idStr = "Bullet " + bullet_id + " of player " + owner_id;
        
        Sphere sphere = new Sphere(32, 32, 0.2f);
        Geometry geom = new Geometry(idStr, sphere);
        
        Player shooter = players.get(owner_id);
        Bullet b = new Bullet(shooter.getPosition(), geom, bullet_id, owner_id);

        Quaternion quat = new Quaternion();
        quat.fromAngleAxis(FastMath.acos(shooter.getRotation().getZ() * -1) * 2  , Vector3f.UNIT_Z);
        Vector3f bulletVelocity = new Vector3f(-7, 0 , 0);
        quat.mult(bulletVelocity, bulletVelocity);
      
        b.setVelocity(bulletVelocity);
        rootNode.attachChild(geom);
        bullets.put(bullet_id, b);
    }
    
    public void removeBullet(int bullet_id)
    {
        bullets.get(bullet_id).getGeometry().removeFromParent();
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
                
                if(p.getCurrentVehicleID() >= 0)
                    break;
                
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
                myServer.broadcast(new UpdateMessage(p.toString()));
            }
            
            // Bullet collisions/updates
            for(int i = 0; i < bullets.size(); i++)
            {
                Bullet b = bullets.get(i);
                
                if(b.getAlive())
                {
                    // Bullet - Player collision
                    for(Player p : players.values())
                    {
                        CollisionResults results = new CollisionResults();
                        if(p.getID() != b.getOwnerID())
                            b.getGeometry().collideWith(p.getGeometry().getWorldBound(), results);

                        if(results.size() > 0)
                        {
                            b.setAlive(false);
                            p.removeLife(1);
                            if(!p.getAlive())
                            {
                                p.setLife(5);
                                p.setPosition(getRandomPlayerSpawn());
                            }
                        }
                    }
                    
                    // Bullet - Car collision
                    for(Car c : cars.values())
                    {
                        CollisionResults results = new CollisionResults();
                        
                        if(!c.hasRider())
                            b.getGeometry().collideWith(c.getGeometry().getWorldBound(), results);

                        if(results.size() > 0)
                        {
                            //collision = true;
                            b.setAlive(false);
                            removeBullet(b.getID());
                            if(!c.hasRider() && players.get(b.getOwnerID()).getPosition().distance(c.getPosition()) < 3)
                            {
                                c.setRiderID(b.getOwnerID());
                                players.get(b.getOwnerID()).setCurrentVehicleID(c.getID());
                                players.get(b.getOwnerID()).setPosition(c.getPosition());
                            }
                        }
                    }

                    // Bullet - World collision
                    for(Spatial spatial : world.getChildren())
                    {
                        CollisionResults results = new CollisionResults();

                        if(!spatial.getName().equals("Floor"))
                            b.getGeometry().collideWith(spatial.getWorldBound(), results);

                        if(results.size() > 0)
                        {
                            //collision = true;
                            b.setAlive(false);
                            removeBullet(b.getID());
                            //b.setVelocity(new Vector3f(0,0,0));
                        }
                    }

                    b.update(0.03f);
                    myServer.broadcast(new UpdateMessage(b.toString()));
                }
            }
            
            for(int i = 0; i < cars.size(); i++)
            {
                Car c = cars.get(i);
                
                if(c.getAlive())
                {
                    // Car - Player collisions
                    for(Player p : players.values())
                    {
                        CollisionResults results = new CollisionResults();

                        if(p.getID() != c.getRiderID() && c.hasRider())
                        {
                            c.getGeometry().collideWith(p.getGeometry().getWorldBound(), results);
                        }

                        if(results.size() > 0)
                        {
                            p.setVelocity(c.getVelocity());
                        }
                    }
                    
                    // Car - Building collisions
                    for(Spatial spatial : world.getChildren())
                    {
                        CollisionResults results = new CollisionResults();

                        if(!spatial.getName().equals("Floor"))
                            c.getGeometry().collideWith(spatial.getWorldBound(), results);

                        if(results.size() > 0)
                        {
                           c.setVelocity(c.getVelocity().negate().divide(1.5f));
                        }
                    }
                }
                
                c.update(0.03f);
                if(c.hasRider())
                {
                    players.get(c.getRiderID()).setPosition(c.getPosition());
                    c.setRotation(players.get(c.getRiderID()).getRotation());
                    myServer.broadcast(new UpdateMessage(players.get(c.getRiderID()).toString()));
                }
                myServer.broadcast(new UpdateMessage(c.toString()));
            }
        }
    }
}

