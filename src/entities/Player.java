/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package entities;

import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import mygame.Weapon;

/**
 *
 * @author Taylor
 */
public class Player extends Entity{

    private int life;
    private int currentVehicleID;
    private long lastAttackTime;
    private Weapon weapon;
    
    public Player(Vector3f position, Geometry geom, int id)                            
    {
        super(position, geom, id);
        
        this.currentVehicleID = -1;
        this.life = 5;
        this.weapon = Weapon.SHOTGUN;
    }
    
    public void setLastAttackTime(long lastAttackTime)
    {
        this.lastAttackTime = lastAttackTime;
    }
    
    public long getLastAttackTime()
    {
        return this.lastAttackTime;
    }
    
    public void setWeapon(Weapon weapon)
    {
        this.weapon = weapon;
    }
    
    public Weapon getWeapon()
    {
        return this.weapon;
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
    
    public int getCurrentVehicleID()
    {
        return this.currentVehicleID;
    }
    
    public void setCurrentVehicleID(int currentVehicleID)
    {
        this.currentVehicleID = currentVehicleID;
    }
    
    public void removeFromVehicle()
    {
        this.currentVehicleID = -1;
    }
    
    @Override
    public String toString()
    {
        return  "Player&" + 
                getID() + "&" +
                getPosition().toString() + "&" + 
                getRotation().toString() + "&" +
                getAlive() + "&" +
                getLife() + "&" +
                getCurrentVehicleID() + "&" +
                getLastAttackTime() + "&" +
                getWeapon().name();
    }
    
}
