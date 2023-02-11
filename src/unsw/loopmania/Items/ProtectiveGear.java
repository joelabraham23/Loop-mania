package unsw.loopmania.Items;

import unsw.loopmania.BasicEnemy;

/**
 * all protective gear will reduce the damage inflicted by the enemy
 */
public interface ProtectiveGear {
    
    /**
     * enemy attack will be reduced
     * @param originalDamage how much damage was meant to be inflicted before the gear's effects
     * @param enemy enemy defending against
     * @return the new amount which character's health will decrease by after gear's affect
     */
    public double defend(double originalDamage, BasicEnemy enemy);
}