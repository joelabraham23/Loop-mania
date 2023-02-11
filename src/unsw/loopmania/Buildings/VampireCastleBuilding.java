package unsw.loopmania.Buildings;

import java.util.List;

import org.javatuples.Pair;
import javafx.beans.property.SimpleIntegerProperty;
import unsw.loopmania.*;
import unsw.loopmania.Character;
import unsw.loopmania.Enemies.Vampire;

/**
 * a basic form of building in the world
 */
public class VampireCastleBuilding extends EnemyBuilding implements EnemyCreator {

    public VampireCastleBuilding(SimpleIntegerProperty x, SimpleIntegerProperty y) {
        super(x, y, "src/images/vampire_castle.png");
    }

    /**
     * only spawn vampire every 5 cycles
     */
    public Vampire spawnEnemy(List<Pair<Integer, Integer>> orderedPath, Character character, Boolean inBattle) {
        // get the path position for the enemy
        if (character.getX() == 0 && character.getY() == 0 && !inBattle) {
            // every cycle notify zombies
            if (character.getCyclesCompleted().getValue() % 5 == 0) {
                PathPosition path = getPathPosition(orderedPath);
                return new Vampire(path);
            }
        }
        return null;
    }

}
