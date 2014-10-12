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

    
    public Bullet(Vector3f position, Geometry geom)                            //, SpriteImage spriteImage)
    {
        super(position, geom);                                          //, spriteImage);
        
    }
}
