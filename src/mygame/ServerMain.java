package mygame;

import com.jme3.app.SimpleApplication;
import com.jme3.material.Material;
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
        
        for(int i = 0; i < players.size(); i++)
        {
            ClientMessage message = new ClientMessage(players.get(i).getPosition(), players.get(i).getRotation(), i);
            myServer.broadcast(message);
        }
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
        
        Player p = new Player(new Vector3f(2,0,0), geom);
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
        for(Player p : players.values())
        {
            System.out.println("Looping");
            p.update(tpf);
        }
        
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
    
    
}
