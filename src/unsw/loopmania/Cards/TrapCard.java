package unsw.loopmania.Cards;

import unsw.loopmania.Buildings.*;
import unsw.loopmania.Items.Armour;

import java.util.ArrayList;

import javafx.beans.property.SimpleIntegerProperty;
import unsw.loopmania.*;
/**
 * Will produce a trap when dropped on ground
 */
public class TrapCard extends Card {

    /**
     * max number of traps in game, if this number
     * is reached, won't create a new trap, just give
     * character gold
     */
    static final private int MAX_TRAP = 3;
    static final private int TRAP_VALUE = 40;

    public TrapCard(SimpleIntegerProperty x, SimpleIntegerProperty y) {
        super(x, y, TRAP_VALUE, "src/images/trap_card.png");
    }   
    
    @Override
    public Building createBuilding(SimpleIntegerProperty x, SimpleIntegerProperty y, ArrayList<Building> currBuildings) {
        // check no more than 2 traps exist
        // before creating a trap
        int currNum = 0;
        for (Building b: currBuildings) {
            if (b instanceof Trap) {
                currNum += 1;
            }
        }
        if (currNum < MAX_TRAP) {
            return new Trap(x, y);
        }
        return null;
    }

    @Override
    public Item giveItemReward(SimpleIntegerProperty x, SimpleIntegerProperty y) {
        return new Armour(x, y);
    }

    @Override
    public Boolean canBeDroppedOnPath() {
        return true;
    }
}
