package unsw.loopmania.RareItems;

import javafx.beans.property.SimpleIntegerProperty;
import unsw.loopmania.BasicEnemy;
import unsw.loopmania.Character;
import unsw.loopmania.Item;

/**
 * If the Character is killed, it respawns with full health up to a single time
 */
public class OneRing extends Item implements RareItem {

    static final private int VALUE = 4000;
    static final private String IMAGE = "src/images/the_one_ring.png";
    
    private Boolean hasBeenUsed = false;
    private RareItem innerItem;

    public OneRing(SimpleIntegerProperty x, SimpleIntegerProperty y) {
        super(x, y, VALUE, IMAGE);
    }

    /**
     * Restores a characters health and marks the One Ring as used.
     * 
     * @param c current character
     * @see Character#provideFullHealth
     * @return true of respawned
     */
    public boolean respawnCharacter(Character c) {
        if (c.getHealth().getValue() <= 0) {
            if (!hasBeenUsed)
                c.provideFullHealth();
            hasBeenUsed = true;
            return true;
        }
        return false;
    }

    public String getEntityImageString() {
        return OneRing.IMAGE;
    }

    public Boolean getHasBeenUsed() {
        return hasBeenUsed;
    }

    public boolean canHeal() {
        return !hasBeenUsed;
    }

    /**
     * if action is HEAL then set character health to 100 otherwise,
     * delegate to innerItem's doMagic
     */
    public double doMagic(double originalValue, Character character, 
                        BasicEnemy e, float chance, RareItemAction action) {
        if (action == RareItemAction.HEAL) {
            this.respawnCharacter(character);
        } else if (innerItem != null) {
            return innerItem.doMagic(originalValue, character, e, chance, action);
        } 
        return originalValue;
    }

    public boolean isShield() {
        return innerItem != null ? innerItem.isShield() : false;
    }

    public boolean isSword() {
        return innerItem != null ? innerItem.isSword() : false;
    }
    public boolean endOfLife() {
        if (hasBeenUsed) {
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