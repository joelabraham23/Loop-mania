package unsw.loopmania.Cards;

import unsw.loopmania.Buildings.*;
import unsw.loopmania.Items.Shield;

import java.util.ArrayList;

import javafx.beans.property.SimpleIntegerProperty;
import unsw.loopmania.*;
/**
 * Will produce a tower when dropped on ground
 */
public class TowerCard extends Card {

    /**
     * max number of towers in game, if this number
     * is reached, won't create a new tower, just give
     * character gold
     */
    static final private int MAX_TOWER = 3;
    static final private int TOWER_VALUE = 50;


    public TowerCard(SimpleIntegerProperty x, SimpleIntegerProperty y) {
        super(x, y, TOWER_VALUE, "src/images/tower_card.png");
    }    

    @Override
    public Building createBuilding(SimpleIntegerProperty x, SimpleIntegerProperty y, ArrayList<Building> currBuildings) {
        // check no more than 2 towers exist
        // before creating a new one
        int currNum = 0;
        for (Building b: currBuildings) {
            if (b instanceof TowerBuilding) {
                currNum += 1;
            }
        }
        if (currNum < MAX_TOWER) {
            return new TowerBuilding(x, y);
        }
        return null;
    }

    @Override
    public Item giveItemReward(SimpleIntegerProperty x, SimpleIntegerProperty y) {
        return new Shield(x, y);
    }

}
