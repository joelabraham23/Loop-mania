package unsw.loopmania.Buildings;

import javafx.beans.property.SimpleIntegerProperty;
import unsw.loopmania.*;

/**
 * Character starts at the Hero's Castle, 
 * and upon finishing the required number of 
 * cycles of the path completed by the Character, 
 * when the Character enters this castle, the Human Player 
 * is offered a window to purchase items at the Hero's Castle
 */
public class HeroCastle extends Building {

    public HeroCastle(SimpleIntegerProperty x, SimpleIntegerProperty y) {
        super(x, y, "src/images/heros_castle.png");
    }

}
