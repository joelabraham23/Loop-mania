package unsw.loopmania.Items;
import unsw.loopmania.*;

import javafx.beans.property.SimpleIntegerProperty;

/**
 * Defends against enemy attacks, critical vampire attacks have a 60% lower chance of occurring.
 */
public class Shield extends EquippedItem implements ProtectiveGear {
    private double percentDecreaseInAttack = 0.4;
    static final private int COST = 150;
    static final public double PCNT_DECREASE_CRITICAL_BITE = 0.6;
    static final private int NUM_ROUNDS = 7;

    public Shield(SimpleIntegerProperty x, SimpleIntegerProperty y, int cost, String image, int rounds) {
        super(x, y, cost, image, rounds);
    }    

    public Shield(SimpleIntegerProperty x, SimpleIntegerProperty y) {
        super(x, y, COST, "src/images/shield.png", NUM_ROUNDS);
    }    

    /**
     * if enemy is a vampire then decrease risk of critical bite by
     * 60%
     */
    public double defend(double originalDamage, BasicEnemy enemy) {
        // lower roundsLeft for item
        this.decreaseRoundsLeft();
        return originalDamage * (1 - percentDecreaseInAttack);
    }

    public static int getCost() {
        return COST;
    }

    public double getPercentDecreaseInAttack() {
        return percentDecreaseInAttack;
    }

    public double getPercentDecreaseInVampireCriticalBite() {
        return PCNT_DECREASE_CRITICAL_BITE;
    }
}
