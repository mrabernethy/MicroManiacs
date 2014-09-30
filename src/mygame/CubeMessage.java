package mygame;

import com.jme3.math.ColorRGBA;
import com.jme3.network.AbstractMessage;
import com.jme3.network.serializing.Serializable;

/**
 *
 * @author Mike
 */
@Serializable(id=0)
public class CubeMessage extends AbstractMessage
{
    private ColorRGBA colour; // init your message data
    
    public CubeMessage() {} // empty default constructor
    public CubeMessage(ColorRGBA colour) 
    {
        this.colour = colour;
    }
    
    public void setColour(ColorRGBA colour) 
    {
        this.colour = colour;
    }
    
    public ColorRGBA getColour()
    {
        return colour;
    }
}
