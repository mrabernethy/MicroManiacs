package mygame;

import com.jme3.network.AbstractMessage;
import com.jme3.network.serializing.Serializable;

/**
 *
 * @author Mike
 */
@Serializable(id=0)
public class GameStateMessage extends AbstractMessage{
    private GameState state;
    
    public GameStateMessage(){}
    
    public GameStateMessage(GameState state)
    {
        this.state = state;
    }
    
    public GameState getGameState()
    {
        return state;
    }
}
