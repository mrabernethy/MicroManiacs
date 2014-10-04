/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package entities;

import com.jme3.math.Vector3f;
import engine.sprites.Sprite;
import engine.sprites.SpriteImage;

/**
 *
 * @author Taylor
 */
public abstract class Entity {
    
    private Vector3f position;
    private Vector3f velocity;
    
    private Sprite sprite;
    
    private boolean alive;
    private float rotation;
    
    public Entity(Vector3f position, SpriteImage spriteImage)
    {
        sprite = new Sprite(spriteImage);
        sprite.setPosition(position);
        this.position = position;
        this.velocity = new Vector3f();
    }
    
    public Vector3f getPosition()
    {
        return this.position;
    }
    
    public void setPosition(Vector3f position)
    {
        this.position = position;
        sprite.setPosition(position);
    }
    
    public void setVelocity(Vector3f velocity)
    {
        this.velocity = velocity;
    }
    
    public Vector3f getVelocity()
    {
        return this.velocity;
    }
    
    public Sprite getSprite()
    {
        return sprite;
    }
    
    public void update(float deltaTime)
    {
        Vector3f newPos = new Vector3f(position.x, position.y, position.z);
        
        newPos.x += deltaTime * velocity.x;
        newPos.y += deltaTime * velocity.y;
        newPos.z += deltaTime * velocity.z;
        
        setPosition(newPos);
    }
    
    
}
