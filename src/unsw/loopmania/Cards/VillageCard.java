package unsw.loopmania.Cards;

import unsw.loopmania.Buildings.*;
import unsw.loopmania.Items.HealthPotion;

import java.util.ArrayList;

import javafx.beans.property.SimpleIntegerProperty;
import unsw.loopmania.*;
/**
 * Will produce a village when dropped on ground
 */
public class VillageCard extends Card {

    static final private int VILLAGE_VALUE = 30;

    public VillageCard(SimpleIntegerProperty x, SimpleIntegerProperty y) {
        super(x, y, VILLAGE_VALUE, "src/images/village_card.png");
    }

    @Override
    public Building createBuilding(SimpleIntegerProperty x, SimpleIntegerProperty y, ArrayList<Building> buildings) {
        return new Village(x, y);
    }

    @Override
    public Item giveItemReward(SimpleIntegerProperty x, SimpleIntegerProperty y) {
        return new HealthPotion(x, y);
    }

    @Override
    public Boolean canBeDroppedOnPath() {
        return true;
    }
}
