package unsw.loopmania;

import java.util.List;

/**
 * This is an subject interace with
 * EnemyCreator as subjects
 * to streamline process of creating new enemies
 */
public interface LoopManiaEnemies {

    /**
     * Attach an enemy creator (enemy building/allied soldier as of now)
     * @param e enemy creator
     */
    void attachEnemyCreator(EnemyCreator e);

    /**
     * remove an enemy creator from notification list
     * @param e enemy creator to remove
     */
    void detachEnemyCreator(EnemyCreator e);

    /**
     * notifying enemy creators to possibly create enemies
     * @param inBattle true if game is in active battle else false
     * @return list of new enemies created
     */
    List<BasicEnemy> spawnEnemies(Boolean inBattle);
}