package unsw.loopmania.Cards;

import unsw.loopmania.Buildings.*;
import unsw.loopmania.Items.Stake;

import java.util.ArrayList;

import javafx.beans.property.SimpleIntegerProperty;
import unsw.loopmania.*;
/**
 * Will produce a zombie when dropped on ground
 */
public class ZombiePitCard extends Card {

    static final private int ZOMBIE_PIT_VALUE = 30;

    public ZombiePitCard(SimpleIntegerProperty x, SimpleIntegerProperty y) {
        super(x, y, ZOMBIE_PIT_VALUE, "src/images/zombie_pit_card.png");
    }

    @Override
    public Building createBuilding(SimpleIntegerProperty x, SimpleIntegerProperty y, ArrayList<Building> buildings) {
        return new ZombiePitBuilding(x, y);
    }

    @Override
    public Item giveItemReward(SimpleIntegerProperty x, SimpleIntegerProperty y) {
        return new Stake(x, y);
    }

}
