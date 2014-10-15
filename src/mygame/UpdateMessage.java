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
public class UpdateMessage extends AbstractMessage{
    
    private String toUpdate;
    private Vector3f pos;
    private Quaternion quat;
    private int updateID;
    private int clientID;
    
    public UpdateMessage(){}
    
    public UpdateMessage(String toUpdate, Vector3f pos, Quaternion quat, int updateID, int clientID)
    {
        this.toUpdate = toUpdate;
        this.pos = pos;
        this.quat = quat;
        this.updateID = updateID;
        this.clientID = clientID;
    }
    
    public String getToUpdate()
    {
        return this.toUpdate;
    }
    
    public Vector3f getPos()
    {
        return this.pos;
    }
    
    public Quaternion getQuat()
    {
        return quat;
    }
    
    public int getUpdateID()
    {
        return this.updateID;
    }
    
    public int getClientID()
    {
        return this.clientID;
    }
    
}
