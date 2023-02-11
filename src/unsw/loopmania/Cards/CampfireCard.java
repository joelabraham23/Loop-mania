package unsw.loopmania.Cards;

import unsw.loopmania.Buildings.*;
import unsw.loopmania.Items.Helmet;

import java.util.ArrayList;

import javafx.beans.property.SimpleIntegerProperty;
import unsw.loopmania.*;
/**
 * Will produce a campfire when dropped on ground
 */
public class CampfireCard extends Card {

    /**
     * max number of campfires in game, if this number
     * is reached, won't create a new campire, just give
     * character gold
     */
    static final private int MAX_CAMPFIRE = 2;
    static final private int CAMPFIRE_VALUE = 30;

    public CampfireCard(SimpleIntegerProperty x, SimpleIntegerProperty y) {
        // initially the building is off the map ie -1 -1
        super(x, y, CAMPFIRE_VALUE, "src/images/campfire_card.png");
    }

    @Override
    public Building createBuilding(SimpleIntegerProperty x, SimpleIntegerProperty y, ArrayList<Building> currBuildings) {
        // check no more than 2 campfires exist
        // before creating a new one
        int currNum = 0;
        for (Building b: currBuildings) {
            if (b instanceof Campfire) {
                currNum += 1;
            }
        }
        if (currNum < MAX_CAMPFIRE) {
            return new Campfire(x, y);
        }
        return null;
    }

    @Override
    public Item giveItemReward(SimpleIntegerProperty x, SimpleIntegerProperty y) {
        return new Helmet(x, y);
    }
}
