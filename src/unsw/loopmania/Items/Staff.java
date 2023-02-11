
package unsw.loopmania.Items;
import unsw.loopmania.*;
import javafx.beans.property.SimpleIntegerProperty;

/**
 * A melee weapon with very low stats (lower than both the sword and stake),
 * which has a random chance of inflicting a trance, which transforms the attacked enemy
 * into an allied soldier temporarily (and fights alongside the Character). If the trance ends during the fight,
 * the affected enemy reverts back to acting as an enemy which fights the Character.
 * If the fight ends whilst the enemy is in a trance, the enemy dies
 */
public class Staff extends EquippedItem implements Weapon {
    private double percentIncreaseInAttack = 0.3;
    static final private int COST = 200;
    static final private int NUM_ROUNDS = 5;

    /**
     * number of secodns enemy is converted to soldier
     * after trance
     */
    static final private int SECONDS_IN_TRANCE = 3;

    public Staff(SimpleIntegerProperty x, SimpleIntegerProperty y) {
        super(x, y, COST, "src/images/staff.png", NUM_ROUNDS);
    }

    /**
     * random 5% chance of transforming enemy into allied soldier temprorarliy
     * hint: use the isAlliedSoldier property of BasicEnemy to convert
     */
    public double attack(double healthToReduce, BasicEnemy e, float chance) {
        // lower roundsLeft for item
        this.decreaseRoundsLeft();
        // first: check if can transform enemy to allied soldier
        // else reduce health
        if (chance <= 0.1f) {
            e.setIsAlliedSolider(SECONDS_IN_TRANCE);
            return healthToReduce;
        } else {
            // reduce health
            return (int)(healthToReduce * (1+percentIncreaseInAttack));
        }
    }

    public static int getCost() {
        return COST;
    }

    public double getDamage() {
        return percentIncreaseInAttack;
    }
}
