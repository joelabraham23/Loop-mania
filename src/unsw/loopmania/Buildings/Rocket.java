package unsw.loopmania.Buildings;

import java.util.List;

import org.javatuples.Pair;
import javafx.beans.property.SimpleIntegerProperty;
import unsw.loopmania.*;
import unsw.loopmania.Character;
import unsw.loopmania.Enemies.Bosses.Elan;

/**
 * a basic form of building in the world
 */
public class Rocket extends EnemyBuilding implements EnemyCreator {

    static final private int SPAWN_CYCLE_COUNT = 40;
    static final private int SPAWN_EXP = 10000;


    public Rocket(SimpleIntegerProperty x, SimpleIntegerProperty y) {
        super(x, y, "src/images/rocket.png");
    }

    /**
     * spawn elan every 40 cycles if experience >= 10000
     */
    public Elan spawnEnemy(List<Pair<Integer, Integer>> orderedPath, Character character, Boolean inBattle) {
        // get the path position for the enemy
        if (character.getX() == 0 && character.getY() == 0 && !inBattle) {
            // every cycle notify zombies
            if (character.getCyclesCompleted().getValue() % SPAWN_CYCLE_COUNT == 0 && character.getExperience().getValue() >= SPAWN_EXP) {
                PathPosition path = getPathPosition(orderedPath);
                return new Elan(path);
            }
        }
        return null;
    }

}
