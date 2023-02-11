package unsw.loopmania.RareItems;

import javafx.beans.property.SimpleIntegerProperty;
import unsw.loopmania.BasicEnemy;
import unsw.loopmania.Character;
import unsw.loopmania.Enemies.Boss;
import unsw.loopmania.Items.Shield;

public class TreeStump extends Shield implements RareItem {

    static final private String IMAGE = "src/images/tree_stump.png";
    static final private int NUM_ROUNDS = 7;
    static final private int VALUE = 1000;

    private RareItem innerItem;

    public TreeStump(SimpleIntegerProperty x, SimpleIntegerProperty y) {
        super(x, y, VALUE, IMAGE, NUM_ROUNDS);
    }

    public TreeStump(SimpleIntegerProperty x, SimpleIntegerProperty y, RareItem innerItem) {
        super(x, y, VALUE, IMAGE, NUM_ROUNDS);
        this.innerItem = innerItem;
    }

    public boolean canHeal() {
        return innerItem != null ? innerItem.canHeal() : false;
    }


    /**
     * if enemy attacking is a boss then reduce damage even further
     */
    @Override
    public double defend(double originalDamage, BasicEnemy enemy) {
        // lower roundsLeft for item
        System.out.println("Defending with stump");

        this.decreaseRoundsLeft();
        if (enemy instanceof Boss) {
            return originalDamage / 2;
        }
        return originalDamage * 0.7;
    }

    public String getEntityImageString() {
        return TreeStump.IMAGE;
    }

    /**
     * if action is defend then delegate to this.defend otherwise, if we have an inner rare item
     * delegate to them incase they have abilities for that action
     */
    public double doMagic(double originalValue, Character character, 
                          BasicEnemy e, float chance, RareItemAction action) {
        if (action == RareItemAction.DEFEND) {
            // delegate to attack method from weapon
            return defend(originalValue, e);
        } else if (innerItem != null) {
            return innerItem.doMagic(originalValue, character, e, chance, action);
        } 
        return originalValue;
    }

    public boolean isShield() {
        return true;
    }

    public boolean isSword() {
        return innerItem != null ? innerItem.isSword() : false;
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