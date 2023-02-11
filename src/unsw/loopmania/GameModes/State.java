package unsw.loopmania.GameModes;

import java.util.ArrayList;
import java.util.List;

import org.javatuples.Pair;

import unsw.loopmania.Character;
import unsw.loopmania.Item;
import unsw.loopmania.RareItems.*;

/**
 * state of the game from the different game modes
 * all logic for differences in game modes added here
 */
public interface State {
    
    /**
     * check that item can be bought
     * @param item item to buy
     * @param character character of the game
     * @param reachedRestriction true or false if a restriction has been reached
     * @return true if can buy else false
     */
    Boolean checkCantBuy(Item item, Character character, Boolean reachedRestriction);

    /**
     * check item can be sold
     * @param obj item that's being sold
     * @param unequippedInventoryItems list of items in unequipped inventory
     * @return  true if can sell else false
     */
    Boolean checkCantSell(Object obj, List<Item> unequippedInventoryItems);


    RareItem getRareItem(ArrayList<String> itemsAllowed, Pair<Integer, Integer> slot, float chance);
}