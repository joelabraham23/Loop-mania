package unsw.loopmania.Items;

import unsw.loopmania.BasicEnemy;

/**
 * all weapons will increase the damage afforded by character
 * to all or some enemies
 */
public interface Weapon {

    /**
     * attack an enemy with increased damage
     * 
     * @param healthToReduce how health is originally reduced by
     * @param e the enemy to attack
     * @param chance the odds of reducing attack
     */
    public double attack(double healthToReduce, BasicEnemy e, float chance);
}