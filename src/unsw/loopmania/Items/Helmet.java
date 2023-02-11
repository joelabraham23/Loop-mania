package unsw.loopmania.Items;
import unsw.loopmania.*;


import javafx.beans.property.SimpleIntegerProperty;

/**
 * Defends against enemy attacks: enemy attacks are reduced by a scalar value. 
 * The damage inflicted by the Character against enemies is reduced (since it is harder to see).
 */
public class Helmet extends EquippedItem implements ProtectiveGear {
    private double percentDecreaseInEnemyAttack = 0.4;
    private double percentDecreaseInCharacterAttack = 0.3;
    static final private int COST = 50;
    static final private int NUM_ROUNDS = 3;

    public Helmet(SimpleIntegerProperty x, SimpleIntegerProperty y) {
        super(x, y, COST, "src/images/helmet.png", NUM_ROUNDS);
    }    

    /**
     * reduce enemy attack but also reduce character's attack
     * will return the new amount which character's health should
     * reduce by
     */
    public double defend(double originalDamage, BasicEnemy enemy) {
        // lower roundsLeft for item
        this.decreaseRoundsLeft();
        return originalDamage * (1 - percentDecreaseInEnemyAttack);
    }

    public static int getCost() {
        return COST;
    }

    public double getPercentDecreaseInEnemyAttack() {
        return percentDecreaseInEnemyAttack;
    }

    public double getPercentDecreaseInCharacterAttack() {
        return percentDecreaseInCharacterAttack;
    }
}
