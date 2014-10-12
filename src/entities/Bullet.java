/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package entities;

import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;

/**
 *
 * @author Damon
 */
public class Bullet extends Entity {
    private Geometry m_geometry;

    public Bullet(Vector3f position) {
        super(position);
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
        m_geometry.getControl(RigidBodyControl.class).setPhysicsLocation(position);
    }
    
    public Vector3f getPosition()
    {
        return m_geometry.getControl(RigidBodyControl.class).getPhysicsLocation();
    }
    
    public void setRotation(Quaternion rotation)
    {
        m_geometry.getControl(RigidBodyControl.class).setPhysicsRotation(rotation);
    }
    
    public Quaternion getRotation()
    {
        return m_geometry.getControl(RigidBodyControl.class).getPhysicsRotation();
    }
}
