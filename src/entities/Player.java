/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package entities;

import com.jme3.asset.AssetManager;
import com.jme3.material.Material;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.shape.Box;
import engine.sprites.SpriteImage;
import mygame.ClientMain;

/**
 *
 * @author Taylor
 */
public class Player extends Entity{
    
    private Geometry m_geometry;
    
    public Player(Vector3f position)                            //, SpriteImage spriteImage)
    {
        super(position);                                          //, spriteImage);
        
    }
    
    public void setGeometry(Geometry geom)
    {
        m_geometry = geom;
    }
    
    public Geometry getGeometry()
    {
        return m_geometry;
    }
    
    public void setPosition(Vector3f position)
    {
        m_geometry.setLocalTranslation(position);
    }
    
    public Vector3f getPosition()
    {
        return m_geometry.getLocalTranslation();
    }
    
    public void setRotation(Quaternion rotation)
    {
        m_geometry.setLocalRotation(rotation);
    }
    
    public Quaternion getRotation()
    {
        return m_geometry.getLocalRotation();
    }
    
}
