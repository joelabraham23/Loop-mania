package unsw.loopmania.Buildings;

import java.util.List;

import org.javatuples.Pair;
import javafx.beans.property.SimpleIntegerProperty;
import unsw.loopmania.*;
import unsw.loopmania.Character;
import unsw.loopmania.Enemies.Bosses.Doggie;

/**
 * a basic form of building in the world
 */
public class DoggieMine extends EnemyBuilding implements EnemyCreator {

    static final public int SPAWN_CYCLE = 20;
    
    public DoggieMine(SimpleIntegerProperty x, SimpleIntegerProperty y) {
        super(x, y, "src/images/mine.png");
    }

    /**
     * spawn doggie every 20 cycles
     */
    public Doggie spawnEnemy(List<Pair<Integer, Integer>> orderedPath, Character character, Boolean inBattle) {
        // get the path position for the enemy
        if (character.getX() == 0 && character.getY() == 0 && !inBattle) {
            // every cycle notify doggies
            if (character.getCyclesCompleted().getValue() % SPAWN_CYCLE == 0) {
                PathPosition path = getPathPosition(orderedPath);
                return new Doggie(path);
            }
        }
        return null;
    }

}
