package unsw.loopmania.GameModes;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

import org.javatuples.Pair;

import unsw.loopmania.RareItems.*;

public class ConfusionMode extends GameMode {

    private ArrayList<String> allRareItems = new ArrayList<>(Arrays.asList("the_one_ring", "anduril_flame_of_the_west", "tree_stump"));
    private Random rand = new Random();
    static final private double CHANCE_RARE_ITEM = 0.2f;

    public ConfusionMode() {}

    /**
     * get two indexes that will not have the same value in their respective arrays
     * to ensure we get two diff items for confusion mode
     * @param itemsAllowed rare items that we're allowd to have in game
     * @return pair of two integers
     */
    private Pair<Integer, Integer> getTwoIndexes(ArrayList<String> itemsAllowed) {
        int index0 = rand.nextInt(itemsAllowed.size());
        int index1 = rand.nextInt(allRareItems.size());
        // check not same item at these indexes
        while (itemsAllowed.get(index0).equals(allRareItems.get(index1))) {
            // change index1
            index1 = rand.nextInt(allRareItems.size());
        }
        return new Pair<>(index0, index1);
    }

    /**
     * will create a rare item that has an innerItem as well that is different
     */
    @Override
    public RareItem getRareItem(ArrayList<String> itemsAllowed, Pair<Integer, Integer> slot, float chance) {
        // CompositeRareItem result = null;

        if (itemsAllowed.size() > 0 && chance < CHANCE_RARE_ITEM) {
            // get two random indexes 
            Pair<Integer, Integer> indexes = getTwoIndexes(itemsAllowed);
            int index0 = indexes.getValue0();
            int index1 = indexes.getValue1();
            int posX = slot.getValue0();
            int posY = slot.getValue1();
            RareItem firstItem = getRareItemForIndex(index0, itemsAllowed, true, posX, posY);
            RareItem secondItem = getRareItemForIndex(index1, allRareItems, false, posX, posY);
            // getting first item that will wrap the secondItem
            firstItem.setInnerItem(secondItem);
            return firstItem;             
        }
        return null;
    }
}
    