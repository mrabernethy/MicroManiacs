package mygame;

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
    
    public ClientCommandMessage() {} // empty default constructor
    public ClientCommandMessage(ClientCommand cmd, int clientID) 
    {
        this.command = cmd;
        this.clientID = clientID;
    }
    
    public ClientCommand getCommand()
    {
        return this.command;
    }
    
    public int getClientID()
    {
        return this.clientID;
    }
}
