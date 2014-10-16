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
public class Bullet extends Entity{

    private int owner_id;
    
    public Bullet(Vector3f position, Geometry geom, int bullet_id, int owner_id)                            //, SpriteImage spriteImage)
    {
        super(position, geom, bullet_id);                                          //, spriteImage);
        this.owner_id = owner_id;
        this.setTerminalVelocity(6.0f);
    }
    
    public int getOwnerID()
    {
        return owner_id;
    }
}
