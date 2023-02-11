package test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.util.ArrayList;

import org.junit.Test;

import unsw.loopmania.AlliedSoldier;
import unsw.loopmania.BasicEnemy;
import unsw.loopmania.Character;
import unsw.loopmania.LoopManiaWorld;
import unsw.loopmania.PathPosition;
import unsw.loopmania.Enemies.Vampire;
import unsw.loopmania.Enemies.Zombie;
import unsw.loopmania.GameModes.BerserkerMode;
import unsw.loopmania.Goals.AndCompositeGoal;

public class AlliedSoldierTests {

    TestHelper helper = new TestHelper();
    ArrayList<org.javatuples.Pair<Integer, Integer>> orderedPath = helper.getOrderedPath();
    LoopManiaWorld game = new LoopManiaWorld(3, 3, orderedPath, new AndCompositeGoal(), new ArrayList<>(),
            new BerserkerMode());
    Character c = game.getCharacter();
    @Test
    public void testSpawnEnemy() {
        PathPosition path = new PathPosition(0, orderedPath);
        AlliedSoldier soldier = new AlliedSoldier(path);
        soldier.setIsZombie(true);
        BasicEnemy result = soldier.spawnEnemy(orderedPath, c, false);
        assertTrue(result instanceof Zombie);
    }

    @Test
    public void testSpawnEnemyReturnEnemyInBattle() {
        PathPosition path = new PathPosition(0, orderedPath);
        AlliedSoldier soldier = new AlliedSoldier(path);
        Vampire v = new Vampire(path);
        soldier.setIsEnemy(0);
        soldier.setPrevEnemy(v);
        // expect vampire to be added to enemies
        BasicEnemy result = soldier.spawnEnemy(orderedPath, c, true);
        assertEquals(result, v);
    }

    @Test
    public void testSpawnEnemyReturnEnemyNotInBattle() {
        PathPosition path = new PathPosition(0, orderedPath);
        AlliedSoldier soldier = new AlliedSoldier(path);
        Vampire v = new Vampire(path);
        soldier.setIsEnemy(0);
        soldier.setPrevEnemy(v);
        // expect vampire to be added to enemies
        BasicEnemy result = soldier.spawnEnemy(orderedPath, c, false);
        assertNull(result);

    }

    @Test
    public void testSpawnEnemyDuringConversion() {
        PathPosition path = new PathPosition(0, orderedPath);
        AlliedSoldier soldier = new AlliedSoldier(path);
        soldier.setIsEnemy(2);
        // expect vampire to be added to enemies
        BasicEnemy result = soldier.spawnEnemy(orderedPath, c, false);
        assertNull(result);
        assertEquals(soldier.getIsEnemy(), 1);
    }

}