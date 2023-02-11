package unsw.loopmania.Items;
import unsw.loopmania.*;

import javafx.beans.property.SimpleIntegerProperty;

/**
 * Body armour, provides defence and halves enemy attack
 */
public class Armour extends EquippedItem implements ProtectiveGear {
    static final private double PERCENT_DECREASE_ATTACK = 0.5;
    static final private int NUM_ROUNDS = 3;
    static final private int COST = 200;

    public double getPercentDecreaseInAttack() {
        return Armour.PERCENT_DECREASE_ATTACK;
    }

    public Armour(SimpleIntegerProperty x, SimpleIntegerProperty y) {
        super(x, y, COST, "src/images/armour.png", NUM_ROUNDS);
    }    

    public double defend(double originalDamage, BasicEnemy enemy) {
        // lower roundsLeft for item
        this.decreaseRoundsLeft();
        return originalDamage * PERCENT_DECREASE_ATTACK;
    }

    public static int getCost() {
        return COST;
    }
    
}
