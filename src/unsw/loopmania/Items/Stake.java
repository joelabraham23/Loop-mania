package unsw.loopmania.Items;
import unsw.loopmania.*;
import unsw.loopmania.Enemies.Vampire;


import javafx.beans.property.SimpleIntegerProperty;

/**
 * A melee weapon with lower stats than the sword, but causes very high damage to vampires.
 */
public class Stake extends EquippedItem implements Weapon {

    private double percentIncreaseInAttack = 0.3;
    private double percentIncreaseInAttackVampire = 3;
    static final private int COST = 100;
    static final private int NUM_ROUNDS = 5;

    public Stake(SimpleIntegerProperty x, SimpleIntegerProperty y) {
        super(x, y, COST, "src/images/stake.png", NUM_ROUNDS);
    }

    /**
     * lower damage increase than sword unless enemy is a vampire,
     * double the damage
     */
    public double attack(double healthToReduce, BasicEnemy e, float chance) {
        // lower roundsLeft for item
        this.decreaseRoundsLeft();
        // check if enemy is vampire
        if (e instanceof Vampire) {
            double moreDamage = (1 + percentIncreaseInAttackVampire) * healthToReduce;
            return moreDamage;
        } else {
            return (int)(healthToReduce * (1+percentIncreaseInAttack));
        }
    }

    public double getDamage() {
        return percentIncreaseInAttack;
    }

    public static int getCost() {
        return COST;
    }

    public double getPercentIncreaseInAttackVampire() {
        return percentIncreaseInAttackVampire;
    }

}
