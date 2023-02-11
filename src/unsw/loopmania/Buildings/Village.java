package unsw.loopmania.Buildings;


import javafx.beans.property.SimpleIntegerProperty;
import unsw.loopmania.Character;
import unsw.loopmania.Building;

/**
 * character regains health when passing through -> full health baby
 */
public class Village extends Building {

    /**
     * amount of health it gives character
     * who steps ona  village
     */
    static final private double INCREASE_IN_HEALTH = 15;

    public Village(SimpleIntegerProperty x, SimpleIntegerProperty y) {
        super(x, y, "src/images/village.png");
    }

    /**
     * make characer health full again, hint: user c.provideFullhealth
     * instead of hardcoding please
     * @param c character of the game
     */
    public void regainHealth(Character c) {
        double newHealth = c.getHealth().getValue() + INCREASE_IN_HEALTH;
        if (newHealth > 100) {
            newHealth = 100;
        }
        c.setHealth(newHealth);
    }

    public double getIncreaseInHealth() {
        return INCREASE_IN_HEALTH;
    }

}