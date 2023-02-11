package unsw.loopmania;

import java.util.ArrayList;

import javafx.beans.property.SimpleIntegerProperty;

/**
 * a Card in the world
 * which doesn't move
 */
public abstract class Card extends Item {

    public Card(SimpleIntegerProperty x, SimpleIntegerProperty y, int value, String entityImage) {
        super(x, y, value, entityImage);
    }

    /**
     * create a building at x, y coordinates for the card
     * @param x x-coordinate
     * @param y y-coordinate
     * @return building depending on what card it is
     */
    public abstract Building createBuilding(SimpleIntegerProperty x, SimpleIntegerProperty y, ArrayList<Building> currBuildings);

    /**
     * create a item at x, y coordinates for the card, 
     * when card is burnt this gets called
     * 
     * @param x x-coordinate
     * @param y y-coordinate
     * @return item depending on what card it is
     */
    public abstract Item giveItemReward(SimpleIntegerProperty x, SimpleIntegerProperty y);

    /**
     * decides where a card can be dropped
     * @return true if can be dropped of walkway and false otherwise
     */
    public Boolean canBeDroppedOnPath() {
        return false;
    }

}
