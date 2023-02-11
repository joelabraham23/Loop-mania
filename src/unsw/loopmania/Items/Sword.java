package unsw.loopmania.Items;
import unsw.loopmania.*;

import javafx.beans.property.SimpleIntegerProperty;


/**
 * A standard melee weapon. Increases damage dealt by Character.
 */
public class Sword extends EquippedItem implements Weapon {
    
    private double percentIncreaseInAttack = 1;

    static final private int COST = 100;
    static final private int NUM_ROUNDS = 5;

    public Sword(SimpleIntegerProperty x, SimpleIntegerProperty y, int cost, String image, int rounds) {
        super(x, y, cost, image, rounds);
    }

    public Sword(SimpleIntegerProperty x, SimpleIntegerProperty y) {
        super(x, y, COST, "src/images/basic_sword.png", NUM_ROUNDS);
    }

    /**
     * Will increase damage for ALL enemy types
     */
    public double attack(double healthToReduce, BasicEnemy e, float chance) {
        // lower roundsLeft for item
        this.decreaseRoundsLeft();
        return healthToReduce * (1 + percentIncreaseInAttack);
    }

    public static int getCost() {
        return COST;
    }

    public double getDamage() {
        return percentIncreaseInAttack;
    }
}
