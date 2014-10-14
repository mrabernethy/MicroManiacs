package mygame;

import com.jme3.math.Quaternion;
import com.jme3.network.AbstractMessage;
import com.jme3.network.serializing.Serializable;


/**
 *
 * @author Mike
 */
@Serializable(id=0)
public class ClientCommandMessage extends AbstractMessage
{
    private ClientCommand command;
    private int clientID;
    private Quaternion rotation;
    
    public ClientCommandMessage() {} // empty default constructor
    public ClientCommandMessage(ClientCommand cmd, Quaternion rotation, int clientID) 
    {
        this.command = cmd;
        this.rotation = rotation;
        this.clientID = clientID;
    }
    
    public ClientCommand getCommand()
    {
        return this.command;
    }
    
    public Quaternion getRotation()
    {
        return this.rotation;
    }
    
    public int getClientID()
    {
        return this.clientID;
    }
}
