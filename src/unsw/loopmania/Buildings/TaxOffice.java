package unsw.loopmania.Buildings;


import javafx.beans.property.SimpleIntegerProperty;
import unsw.loopmania.Building;
import unsw.loopmania.Character;

/**
 * During a battle within its shooting radius, enemies will be attacked by the tower
 */
public class TaxOffice extends Building {
    
    static final private double TAX_PERCENT = 0.1;

    public TaxOffice(SimpleIntegerProperty x, SimpleIntegerProperty y) {
        super(x, y, "src/images/tax_office.png");
    }

    /**
     * Tax character TAX_PERCENT of their gold
     * everytime character walks past this building
     * @param c character of game
     */
    public void taxCharacter(Character c) {
        double goldToTake = c.getMoney().getValue() * TAX_PERCENT;
        c.decreaseMoney((int) goldToTake);
    }

}