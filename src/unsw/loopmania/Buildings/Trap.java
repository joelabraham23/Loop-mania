package unsw.loopmania.Buildings;


import javafx.beans.property.SimpleIntegerProperty;
import unsw.loopmania.*;

/**
 * When an enemy steps on a trap, the enemy is damaged 
 * (and potentially killed if it loses all health) and the trap is destroyed
 */
public class Trap extends  Building {

    // The damage a trap inflicts on any enemy entity that steps on it
    static final private int DAMAGE = 20;

    public Trap(SimpleIntegerProperty x, SimpleIntegerProperty y) {
        super(x, y, "src/images/trap.png");
    }
    
    /**
     * reduce enemy health by damage amount
     * @param e enemy
     */
    public void damageEnemy(BasicEnemy e) {
        e.setHealth(e.getHealth() - DAMAGE);
    }
    
    public int getDamage() {
        return DAMAGE;
    }
}