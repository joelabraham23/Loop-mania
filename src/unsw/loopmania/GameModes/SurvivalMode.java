package unsw.loopmania.GameModes;

import unsw.loopmania.Character;

import unsw.loopmania.Items.HealthPotion;
import unsw.loopmania.Item;

/**
 * can only purchase health potion from hero's castle once
 * 
 * and have a very low chance of getting rare items
 */
public class SurvivalMode extends GameMode {
    public SurvivalMode() {
        this.setChanceRareItem(0.005);
    }

    @Override
    public Boolean checkCantBuy(Item item, Character character, Boolean reachedRestriction) {
        if (character.getMoney().getValue() < item.getValue()) {
            return true;
        }
        if (item instanceof HealthPotion) {
            if (reachedRestriction) return true;
        }
        return false;
    }

}