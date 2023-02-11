package unsw.loopmania.Cards;

import unsw.loopmania.Buildings.*;
import unsw.loopmania.Items.Sword;

import java.util.ArrayList;

import javafx.beans.property.SimpleIntegerProperty;
import unsw.loopmania.*;
/**
 * represents a vampire castle card in the backend game world
 */
public class VampireCastleCard extends Card {

    static final private int VAMPIRE_CASTLE_VALUE = 20;
    
    public VampireCastleCard(SimpleIntegerProperty x, SimpleIntegerProperty y) {
        super(x, y, VAMPIRE_CASTLE_VALUE, "src/images/vampire_castle_card.png");
    }    

    @Override
    public Building createBuilding(SimpleIntegerProperty x, SimpleIntegerProperty y, ArrayList<Building> buildings) {
        return new VampireCastleBuilding(x, y);
    }

    @Override
    public Item giveItemReward(SimpleIntegerProperty x, SimpleIntegerProperty y) {
        return new Sword(x, y);
    }

}
