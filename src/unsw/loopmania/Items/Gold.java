package unsw.loopmania.Items;


import javafx.beans.property.SimpleIntegerProperty;
import unsw.loopmania.Item;

/**
 * In-game currency which is used to buy items.
 */
public class Gold extends Item {
    static final private int GOLD_AMOUNT = 20;

    public int getGoldAmount() {
        return GOLD_AMOUNT;
    }
    
    public Gold(SimpleIntegerProperty x, SimpleIntegerProperty y) {
        // in general will be dropped in denominations of 20
        super(x, y, GOLD_AMOUNT, "src/images/gold_pile.png");
    }

}