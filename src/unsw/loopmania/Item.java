package unsw.loopmania;

import javafx.beans.property.SimpleIntegerProperty;

public class Item extends StaticEntity {

    /**
     * the value of an item (ie how much gold it is worth)
     * for the gold class, value represents how many gold exists
     */
    private int value;

    public Item(SimpleIntegerProperty x, SimpleIntegerProperty y, int value, String entityImage) {
        super(x, y, entityImage);
        this.value = value;
    }   
    public int getValue() {
        return value;
    }
    public void setValue(int value) {
        this.value = value;
    }
}