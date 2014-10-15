/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package entities;

import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;

/**
 *
 * @author Taylor
 */
public abstract class Entity {
    
    private Vector3f velocity;
    private Vector3f acceleration;
    
    private float terminalVelocity = 4.0f;
    
    private Geometry geom;

    private boolean alive;
    
    
    public Entity(Vector3f position, Geometry geom)
    {
        this.geom = geom;
        this.geom.setLocalTranslation(position);
        this.velocity = new Vector3f();
        this.acceleration = new Vector3f();
    }
    
    public void setAcceleration(Vector3f acceleration)
    {
        this.acceleration = acceleration;
    }
    
    public Vector3f getAcceleration()
    {
        return this.acceleration;
    }

    public void setVelocity(Vector3f velocity)
    {
        this.velocity = velocity;
    }
    
    public Vector3f getVelocity()
    {
        return this.velocity;
    }
    
    public void update(float deltaTime)
    {
        Vector3f newVel = new Vector3f(getVelocity().x, getVelocity().y, getVelocity().z);
        Vector3f newPos = new Vector3f(getPosition().x, getPosition().y, getPosition().z);
        
        newVel.x += deltaTime * acceleration.x;
        newVel.y += deltaTime * acceleration.y;
        newVel.z += deltaTime * acceleration.z;
        
        if(newVel.length() < terminalVelocity)
        {
            setVelocity(newVel);
        }
        
        newPos.x += deltaTime * velocity.x;
        newPos.y += deltaTime * velocity.y;
        newPos.z += deltaTime * velocity.z;
        
        setPosition(newPos);
    }
    
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
    
    public RigidBodyControl getRigidBodyControl()
    {
        return this.geom.getControl(RigidBodyControl.class);
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
