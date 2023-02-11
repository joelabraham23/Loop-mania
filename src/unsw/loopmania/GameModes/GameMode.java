package unsw.loopmania.GameModes;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.javatuples.Pair;

import javafx.beans.property.SimpleIntegerProperty;
import unsw.loopmania.Character;
import unsw.loopmania.Item;
import unsw.loopmania.RareItems.*;

public class GameMode implements State {


    private double chanceRareItem;


    public GameMode() {
        setChanceRareItem(0.2f);
    }

    public Boolean checkCantBuy(Item item, Character character, Boolean reachedRestriction) {
        if (character.getMoney().getValue() < item.getValue()) {
            return true;
        }
        return false;
    }

    /**
     * Finds if an item exists in inventory to be sold
     * @param obj an object of the type to check exists to sell
     * @param unequippedInventoryItems list of items in unequipped inventory
     * @return true if an object of type obj is not in inventory
     */
    public Boolean checkCantSell(Object obj, List<Item> unequippedInventoryItems) {
        for (Item item : unequippedInventoryItems) {
            if (item.getClass().equals(obj.getClass())) {
                return false;
            }
        }
        return true;
    }

    public RareItem getRareItemForIndex(int index, ArrayList<String> itemsAllowed, Boolean modifyArray, int posX, int posY) {
        String item = itemsAllowed.get(index);
        if (modifyArray) itemsAllowed.remove(item);
        switch (item) {
            case "the_one_ring":
                return new OneRing(new SimpleIntegerProperty(posX), new SimpleIntegerProperty(posY));
            case "anduril_flame_of_the_west":
                return new Anduril(new SimpleIntegerProperty(posX), new SimpleIntegerProperty(posY));
            case "tree_stump":
                return new TreeStump(new SimpleIntegerProperty(posX), new SimpleIntegerProperty(posY));
            default:
                return null;
        }
    }

    public RareItem getRareItem(ArrayList<String> itemsAllowed, Pair<Integer, Integer> slot, float chance) {
        // get two random indexes 
        Random rand = new Random();
        // CompositeRareItem result = null;
        if (itemsAllowed.size() > 0) {
            int index = rand.nextInt(itemsAllowed.size());
            if (chance < chanceRareItem) {
                // randomly choose a rare item
                int posX = slot.getValue0();
                int posY = slot.getValue1();
                RareItem firstItem = getRareItemForIndex(index, itemsAllowed, true, posX, posY);
                return firstItem;
            }
        }
        // return result;
        return null;
    }

    public void setChanceRareItem(double chanceRareItem) {
        this.chanceRareItem = chanceRareItem;
    }
}