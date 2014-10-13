/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package entities;

import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;

/**
 *
 * @author Taylor
 */
public abstract class Entity {
    
    private Vector3f velocity;
    
    private Geometry geom;

    private boolean alive;
    
    
    public Entity(Vector3f position, Geometry geom)                                    //, SpriteImage spriteImage)
    {
        this.geom = geom;
        this.geom.setLocalTranslation(position);
        this.velocity = new Vector3f();
    }

    public void setVelocity(Vector3f velocity)
    {
        this.velocity = velocity;
    }
    
    public Vector3f getVelocity()
    {
        return this.velocity;
    }
    
    
//    public void update(float deltaTime)
//    {
//        Vector3f newPos = new Vector3f(position.x, position.y, position.z);
//        
//        newPos.x += deltaTime * velocity.x;
//        newPos.y += deltaTime * velocity.y;
//        newPos.z += deltaTime * velocity.z;
//        
//        setPosition(newPos);
//    }
    
    public void setRotation(Quaternion rotation)
    {
        this.geom.setLocalRotation(rotation);
    }
    
    public Quaternion getRotation()
    {
        return this.geom.getLocalRotation();
    }
    
    public Geometry getGeometry()
    {
        return this.geom;
    }
    
    public void setGeometry(Geometry geom)
    {
        this.geom = geom;
    }
    
    public void setPosition(Vector3f position)
    {
        this.geom.center();
        this.geom.setLocalTranslation(position);
    }
    
    public Vector3f getPosition()
    {
        return this.geom.getLocalTranslation();
    }
}
