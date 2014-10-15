package mygame;

import com.jme3.app.SimpleApplication;
import com.jme3.collision.CollisionResults;
import com.jme3.math.Quaternion;
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
        Serializer.registerClass(GreetingMessage.class); 
        Serializer.registerClass(ClientCommandMessage.class); 

        myServer.addMessageListener(new ServerListener(this, myServer),
                GreetingMessage.class);
        myServer.addMessageListener(new ServerListener(this, myServer),
                ClientMessage.class);
        myServer.addMessageListener(new ServerListener(this, myServer),
                ClientCommandMessage.class);
        
        Timer updateLoop = new Timer(true);
        updateLoop.scheduleAtFixedRate(new UpdateTimer(), 0, 30);

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
    
    public void updatePlayer(int id, Vector3f position, Quaternion rotation)
    {
        players.get(id).setPosition(position);
        players.get(id).setRotation(rotation);
        
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
            for(Player p : players.values())
            {
                p.update(0.03f);
                myServer.broadcast(new ClientMessage(p.getPosition(), p.getRotation(), p.getID()));
            }
            
            
        }

    }

}

