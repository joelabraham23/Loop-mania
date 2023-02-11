package unsw.loopmania.GameModes;


import unsw.loopmania.Character;
import unsw.loopmania.Item;
import unsw.loopmania.Items.*;

/**
 * Can only purchase one protective gear from hero's castle
 * each round
 * 
 * lower chance of getting a rare item
 */
public class BerserkerMode extends GameMode {


    public BerserkerMode() {
        this.setChanceRareItem(0.1);
    }


    @Override
    public Boolean checkCantBuy(Item item, Character character, Boolean reachedRestriction) {
        if (character.getMoney().getValue() < item.getValue()) {
            return true;
        }
        if (item instanceof ProtectiveGear) {
            if (reachedRestriction) return true;
        }
        return false;
    }

}
    