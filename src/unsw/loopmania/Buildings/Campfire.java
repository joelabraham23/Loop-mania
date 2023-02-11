package unsw.loopmania.Buildings;

import java.util.ArrayList;

import javafx.beans.property.SimpleIntegerProperty;
import unsw.loopmania.Character;
import unsw.loopmania.BasicEnemy;
import unsw.loopmania.Building;
import unsw.loopmania.Enemies.*;
/**
 * Character deals double damage within campfire battle radius
 */
public class Campfire extends Building {

    static final private int BATTLE_RADIUS = 5;

    public Campfire(SimpleIntegerProperty x, SimpleIntegerProperty y) {
        super(x, y, "src/images/campfire.png");
    }

    /**
     * Deals double character damage
     * @param c main character of game
     */
    public void doubleCharacterDamage(Character c) {
        c.setDamage(c.getDamage() * 2);
    }

    /**
     * halve character damage to go back to original
     * @param c main character of game
     */
    public void halveCharacterDamage(Character c) {
        c.setDamage(c.getDamage() / 2);
    }

    /**
     * Vampires get scared and run away from campfire battle radius
     * @param vampires list of vampires within battle radius
     */
    public void scareVampire(ArrayList<BasicEnemy> enemies) {
        for (BasicEnemy e: enemies) {
            if (e instanceof Vampire) {
                if (Math.pow((this.getX()-e.getX()), 2) +  Math.pow((this.getY()-e.getY()), 2) <= BATTLE_RADIUS) {
                    // vampire in battle radius, should change direction as a result
                    e.reversePath();
                }
            }
        }
    }
    
    public int getBattleRadius() {
        return BATTLE_RADIUS;
    }
}