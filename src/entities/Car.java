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
public class Car extends Entity{

    private int riderID;
    
    public Car(Vector3f position, Geometry geom, int carID)                            //, SpriteImage spriteImage)
    {
        super(position, geom, carID);                                          //, spriteImage);
        this.riderID = -1;
        this.setTerminalVelocity(6.5f);
    }
    
    public int getRiderID()
    {
        return this.riderID;
    }
    
    public void removeRider()
    {
        this.riderID = -1;
    }
    
    public void setRiderID(int riderID)
    {
        this.riderID = riderID;
    }
    
    public boolean hasRider()
    {
        return this.riderID >= 0;
    }
    
    @Override
    public String toString()
    {
        return  "Car&" + 
                getID() + "&" +
                getRiderID() + "&" +
                getPosition().toString() + "&" +
                getRotation().toString() + "&" + 
                getAlive();
    }
}
