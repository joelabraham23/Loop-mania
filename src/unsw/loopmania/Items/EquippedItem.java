package unsw.loopmania.Items;

import javafx.beans.property.SimpleIntegerProperty;
import unsw.loopmania.Item;

public abstract class EquippedItem extends Item {

    /**
     * number of rounds left for item to be used before it is destroyed
     */
    private int roundsLeft;

    public EquippedItem(SimpleIntegerProperty x, SimpleIntegerProperty y, int value, String entityImage, int rounds) {
        super(x, y, value, entityImage);
        roundsLeft = rounds;
    }

    /**
     * when item is used once, call this function to decrease
     * it's life span by one
     */
    public void decreaseRoundsLeft() {
        roundsLeft -= 1;
    }

    public void setroundsLeft(int roundsLeft) {
        this.roundsLeft = roundsLeft;
    }

    public int getroundsLeft() {
        return roundsLeft;
    }
    
}