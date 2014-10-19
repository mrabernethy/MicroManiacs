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
    
    private String updateMessageString;
    
    public UpdateMessage(){}
    
    public UpdateMessage(String updateMessageString, int clientID)
    {
        this.updateMessageString = updateMessageString;
    }
    
    public String getUpdateMessageString()
    {
        return this.updateMessageString;
    }
    
}
