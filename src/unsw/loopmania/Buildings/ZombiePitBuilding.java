package unsw.loopmania.Buildings;


import java.util.List;

import org.javatuples.Pair;

import javafx.beans.property.SimpleIntegerProperty;
import unsw.loopmania.*;
import unsw.loopmania.Character;
import unsw.loopmania.Enemies.Zombie;

/**
 * a basic form of building in the world
 */
public class ZombiePitBuilding extends EnemyBuilding implements EnemyCreator {

    public ZombiePitBuilding(SimpleIntegerProperty x, SimpleIntegerProperty y) {
        super(x, y, "src/images/zombie_pit.png");
    }

    /**
     * only spawn enemy every cycle (when character is at 0, 0)
     */
    public Zombie spawnEnemy(List<Pair<Integer, Integer>> orderedPath, Character character, Boolean inBattle) {
        // get the path position for the enemy
        if (character.getX() == 0 && character.getY() == 0 && !inBattle) {
            PathPosition path = getPathPosition(orderedPath);
            return new Zombie(path);
        }
        return null;
    }
}
