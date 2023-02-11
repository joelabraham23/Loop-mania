package unsw.loopmania.RareItems;

import javafx.beans.property.SimpleIntegerProperty;
import unsw.loopmania.BasicEnemy;
import unsw.loopmania.Character;
import unsw.loopmania.Enemies.Boss;
import unsw.loopmania.Items.Sword;

/**
 * A very high damage sword which causes triple damage against bosses
 */
public class Anduril extends Sword implements RareItem {

    static final private String IMAGE = "src/images/anduril_flame_of_the_west.png";
    static final private int NUM_ROUNDS = 7;
    static final private int VALUE = 1200;

    private RareItem innerItem;

    public Anduril(SimpleIntegerProperty x, SimpleIntegerProperty y) {
        super(x, y, VALUE, IMAGE, NUM_ROUNDS);
    }

    /**
     * if enemy is a boss then cause triple damaage else cause normal damage
     */
    public double attack(double healthToReduce, BasicEnemy e, float chance) {
        // lower roundsLeft for item
        System.out.println("Attacking with anduril");
        this.decreaseRoundsLeft();
        if (e instanceof Boss) {
            healthToReduce = healthToReduce * 3;
        } else {
            healthToReduce = healthToReduce * 1.5;
        }
        return healthToReduce;
    }

    public String getEntityImageString() {
        return Anduril.IMAGE;
    }

    public boolean canHeal() {
        return innerItem != null ? innerItem.canHeal() : false;
    }

    /**
     * if action is attBack then return the new value from attack, if it's defend
     * then check if then send to inner object incase that has defend
     */
    public double doMagic(double originalValue, Character character, 
                        BasicEnemy e, float chance, RareItemAction action) {
        if (action == RareItemAction.ATTACK) {
            // delegate to attack method from weapon
            return attack(originalValue, e, chance);
        } else if (innerItem != null) {
            return innerItem.doMagic(originalValue, character, e, chance, action);
        }
        return originalValue;
    }

    public boolean isShield() {
        return innerItem != null ? innerItem.isShield() : false;
    }

    public boolean isSword() {
        return true;
    }

    public boolean endOfLife() {
        if (this.getroundsLeft() <= 0) {
            return true;
        } 
        if (innerItem != null) {
            return innerItem.endOfLife();
        }
        return false;
    }

    public void setInnerItem(RareItem item) {
        this.innerItem = item;
    }

}