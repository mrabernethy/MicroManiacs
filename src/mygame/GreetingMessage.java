package mygame;

import com.jme3.network.AbstractMessage;
import com.jme3.network.serializing.Serializable;


/**
 * 
 * @author Mike
 */
@Serializable(id=0)
public class GreetingMessage extends AbstractMessage
{
    private String greeting = "Hello!"; // init your message data
    
    public GreetingMessage() {} // empty default constructor
    public GreetingMessage(String s) 
    {
        greeting = s;
    }
    
    public void setGreeting(String s)
    {
        greeting = s;
    }
    
    public String getGreeting()
    {
        return greeting;
    }
}

/*
 * Class adapted from Kusterer, R. (2013). JMonkeyEngine 3.0 Beginner's Guide. Packt Publishing Ltd.
 */
