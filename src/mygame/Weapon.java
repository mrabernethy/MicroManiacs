/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package mygame;

/**
 *
 * @author Taylor
 */
public enum Weapon {
    PISTOL(1000,1),
    SHOTGUN(1800,3),
    MACHINE_GUN(200,1);

    private long shotCooldown;
    private int bulletSpread;
    
    private Weapon(long shotCooldown, int bulletSpread)
    {
        this.shotCooldown = shotCooldown;
        this.bulletSpread = bulletSpread;
    }
    
    public long getShotCooldown()
    {
        return this.shotCooldown;
    }
    
    public int getBulletSpread()
    {
        return this.bulletSpread;
    }
}
