/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package entities;

import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;

/**
 *
 * @author Taylor
 */
public class Player extends Entity{

    private int life;
    
    public Player(Vector3f position, Geometry geom, int id)                            
    {
        super(position, geom, id);
        
        this.life = 5;
    }
    
    public void checkAlive()
    {
        this.setAlive(this.life > 0);
    }
    
    public void removeLife(int lifeToRemove)
    {
        this.life -= lifeToRemove;
        
        checkAlive();
    }
    
    public void setLife(int life)
    {
        this.life = life;
        
        checkAlive();
    }
    
    public int getLife()
    {
        return this.life;
    }
    
    
    
    @Override
    public String toString()
    {
        return  "Player&" + 
                getID() + "&" +
                getPosition().toString() + "&" + 
                getRotation().toString() + "&" +
                getAlive() + "&" +
                getLife();
    }
    
}
