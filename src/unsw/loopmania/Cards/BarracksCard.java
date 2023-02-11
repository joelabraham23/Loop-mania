package unsw.loopmania.Cards;

import unsw.loopmania.Buildings.*;
import unsw.loopmania.Items.Staff;

import java.util.ArrayList;

import javafx.beans.property.SimpleIntegerProperty;
import unsw.loopmania.*;
/**
 * Will produce a barracks when dropped on ground
 */
public class BarracksCard extends Card {

    /**
     * max number of barracks in game, if this number
     * is reached, won't create a new barrack, just give
     * character gold
     */
    static final private int MAX_BARRACKS = 2;
    static final private int BARRACKS_VALUE = 30;

    public BarracksCard(SimpleIntegerProperty x, SimpleIntegerProperty y) {
        // initially the building is off the map ie -1 -1
        super(x, y, BARRACKS_VALUE, "src/images/barracks_card.png");
    }  
    
    @Override
    public Building createBuilding(SimpleIntegerProperty x, SimpleIntegerProperty y, ArrayList<Building> currBuildings) {
        // check no more than 2 barracks exist
        int currNum = 0;
        for (Building b: currBuildings) {
            if (b instanceof Barracks) {
                currNum += 1;
            }
        }
        if (currNum < MAX_BARRACKS) {
            return new Barracks(x, y);
        }
        return null;
    }

    /**
     * return staff
     */
    @Override
    public Item giveItemReward(SimpleIntegerProperty x, SimpleIntegerProperty y) {
        return new Staff(x, y);
    }

    @Override
    public Boolean canBeDroppedOnPath() {
        return true;
    }
}
