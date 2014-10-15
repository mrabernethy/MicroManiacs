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

    private int id;
    
    public Player(Vector3f position, Geometry geom, int id)                            //, SpriteImage spriteImage)
    {
        super(position, geom);                                          //, spriteImage);
        this.id = id;
    }
    
    public int getID()
    {
        return id;
    }
}
