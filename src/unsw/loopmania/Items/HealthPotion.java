package unsw.loopmania.Items;

import javafx.beans.property.SimpleIntegerProperty;
import unsw.loopmania.*;
import unsw.loopmania.Character;

/**
 * Refills Character health
 */
public class HealthPotion extends Item {
    static final private double HEALTH_GAIN = 20;
    static final private int COST = 50;

    public HealthPotion(SimpleIntegerProperty x, SimpleIntegerProperty y) {
        super(x, y, COST, "src/images/brilliant_blue_new.png");
    }
    /**
     * Character regains health by healthGain amount
     * hint: use c.resetHealth
     * @param c Main character
     */
    public void regainHealth(Character c) {
        double newHealth = c.getHealth().getValue() + HEALTH_GAIN;
        if (newHealth > Character.getFullhealth()) {
            newHealth = Character.getFullhealth();
        }
        c.setHealth(newHealth);
    }
    
    public static int getCost() {
        return COST;
    }
}
