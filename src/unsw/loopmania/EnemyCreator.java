package unsw.loopmania;

import java.util.List;

import org.javatuples.Pair;

/**
 * as part of the observer pattern with LoopManiaEnemies
 */
public interface EnemyCreator {

    /**
     * create a basic enemy
     * @param orderedPath path of the world
     * @param character main character
     * @param inBattle true if game is in an active battle else false
     * @return null if no enemy is created
     */
    public BasicEnemy spawnEnemy(List<Pair<Integer, Integer>> orderedPath, Character character, Boolean inBattle);
}