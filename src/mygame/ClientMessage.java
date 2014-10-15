/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package mygame;

import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.jme3.network.AbstractMessage;
import com.jme3.network.serializing.Serializable;

/**
 *
 * @author Taylor
 */
@Serializable(id=0)
public class ClientMessage extends AbstractMessage{
    
    private Vector3f pos;
    private Quaternion quat;
    private int clientID;
    
    public ClientMessage(){}
    
    public ClientMessage(Vector3f pos, Quaternion quat, int clientID)
    {
        this.pos = pos;
        this.quat = quat;
        this.clientID = clientID;
    }
    
    public Vector3f getPos()
    {
        return this.pos;
    }
    
    public Quaternion getQuat()
    {
        return quat;
    }
    
    public int getClientID()
    {
        return this.clientID;
    }
    
    
}
