package test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.Random;

import org.javatuples.Pair;

import unsw.loopmania.*;
import unsw.loopmania.Character;

import org.junit.Test;

import javafx.beans.property.SimpleIntegerProperty;
import unsw.loopmania.Buildings.*;
import unsw.loopmania.Enemies.*;
import unsw.loopmania.Goals.*;
import unsw.loopmania.Items.*;
import unsw.loopmania.RareItems.OneRing;
import unsw.loopmania.RareItems.RareItem;
import unsw.loopmania.GameModes.*;

public class IntegrationTests {

    /**
     * world's ordered path
     */
    TestHelper helper = new TestHelper();
    ArrayList<Pair<Integer, Integer>> orderedPath = helper.getOrderedPath();
    LoopManiaWorld game = new LoopManiaWorld(3, 3, orderedPath, new AndCompositeGoal(), new ArrayList<>(),
            new BerserkerMode());
    Character c = game.getCharacter();
    Random rand = new Random();
    

    /**
     * When a  character is created, they have an inistal
     * health of 100 and without any equipment can cause 10 damage
     */
    @Test
    public void testInitialCharacterStats(){
        c.setX(0);
        c.setY(0);
        assertEquals(c.getHealth().getValue(), 100);
        // create a vampire
        VampireCastleBuilding vBuilding = new VampireCastleBuilding(new SimpleIntegerProperty(1), new SimpleIntegerProperty(1));
        
        BasicEnemy v = vBuilding.spawnEnemy(orderedPath, c, false);
        // get character to attack v
        c.attack(v, rand.nextInt());
        // expect v's health to go down by 10 -> 50
        assertEquals(v.getHealth(), 50);
    }

    /**
     * In a battle, when character is in battle raedius of one enemy
     * and no enemies in support radius, battle begins automatically
     * with character throwing first punch and then enemy
     * 
     * battle ends once either character or enemy has health <= 0
     */
    @Test
    public void battleOneEnemyNoSupport(){
        c.setX(0);
        c.setY(0);
        VampireCastleBuilding vBuilding = new VampireCastleBuilding(new SimpleIntegerProperty(1), new SimpleIntegerProperty(1));
        BasicEnemy v = vBuilding.spawnEnemy(orderedPath, c, false);
        game.addEnemy(v);
        assertTrue(v instanceof Vampire);
        // set character close to vampire
        c.setX(v.getX());
        c.setY(v.getY());
        // run battles once
        ArrayList<BasicEnemy> enemies = game.getEnemiesInBattle();
        game.runBattles(enemies);
        assertTrue(v.getHealth() == 50 || v.getIsAlliedSolider() > 0);        
    }

    /**
     * test that towers do attack enemies
     */
    @Test
    public void battleBattleTowers(){
        c.setX(0);
        c.setY(0);
        VampireCastleBuilding vBuilding = new VampireCastleBuilding(new SimpleIntegerProperty(1), new SimpleIntegerProperty(1));
        game.addToBuildingEntities(vBuilding);
        BasicEnemy v = vBuilding.spawnEnemy(orderedPath, c, false);
        game.addEnemy(v);
        TowerBuilding tower = new TowerBuilding(new SimpleIntegerProperty(1), new SimpleIntegerProperty(1));
        game.addToBuildingEntities(tower);
        // set character close to vampire
        c.setX(v.getX());
        c.setY(v.getY());
        // run battles once
        ArrayList<BasicEnemy> enemies = game.getEnemiesInBattle();
        game.runBattles(enemies);
        // check that towers are attacking
        assertTrue(v.getHealth() == 45 || v.getIsAlliedSolider() > 0);
        // check character's health has also decreased
        
    }

    /**
     * test that towers do attack enemies
     */
    @Test
    public void battleDefeatedEnemies(){
        c.setX(0);
        c.setY(0);
        VampireCastleBuilding vBuilding = new VampireCastleBuilding(new SimpleIntegerProperty(1), new SimpleIntegerProperty(1));
        game.addToBuildingEntities(vBuilding);
        BasicEnemy v = vBuilding.spawnEnemy(orderedPath, c, false);
        game.addEnemy(v);
        TowerBuilding tower = new TowerBuilding(new SimpleIntegerProperty(1), new SimpleIntegerProperty(1));
        game.addToBuildingEntities(tower);
        // set character close to vampire
        c.setX(v.getX());
        c.setY(v.getY());
        v.setHealth(5);
        // run battles once
        ArrayList<BasicEnemy> enemies = game.getEnemiesInBattle();
        game.runBattles(enemies);
        // assert enemy is removed
        assertTrue(!game.getEnemies().contains(v) || v.getIsAlliedSolider() > 0);
        
    }

    /**
     * test that towers do attack enemies
     */
    @Test
    public void battleDefeatedSoldier(){
        c.setX(0);
        c.setY(0);
        VampireCastleBuilding vBuilding = new VampireCastleBuilding(new SimpleIntegerProperty(1), new SimpleIntegerProperty(1));
        game.addToBuildingEntities(vBuilding);
        BasicEnemy v = vBuilding.spawnEnemy(orderedPath, c, false);
        game.addEnemy(v);
        TowerBuilding tower = new TowerBuilding(new SimpleIntegerProperty(1), new SimpleIntegerProperty(1));
        game.addToBuildingEntities(tower);
        // set character close to vampire
        c.setX(v.getX());
        c.setY(v.getY());
        AlliedSoldier soldier = new AlliedSoldier(null);
        soldier.setHealth(4);
        game.addAlliedSoldier(soldier);
        // run battles once
        ArrayList<BasicEnemy> enemies = game.getEnemiesInBattle();
        game.runBattles(enemies);
        // assert enemy is removed
        assertTrue(!game.getAlliedSoldiers().contains(soldier) || soldier.getIsZombie());
        
    }

    @Test
    public void runTickMovesGoalReached(){
        
        GoalComponent simpleGoal = new MoneyGoal(5);
        CompositeGoal finalGoal = new AndCompositeGoal();
        finalGoal.addGoal(simpleGoal);
        LoopManiaWorld game = new LoopManiaWorld(3, 3, orderedPath, new AndCompositeGoal(), new ArrayList<>(),
        new BerserkerMode());
        Character c = game.getCharacter();
        c.increaseMoney(10);

        game.runTickMoves(false);
        // except gameWon to be true
        assertEquals(true, game.gameWon.getValue());
        
    }

    /**
     * In a battle, when character is in battle raedius of one enemy
     * and no enemies in support radius, if character has alliedSoldiers
     * the soldier is attacked only
     */
    @Test
    public void battleOneEnemyOneSoldier(){
        c.setX(0);
        c.setY(0);
        VampireCastleBuilding vBuilding = new VampireCastleBuilding(new SimpleIntegerProperty(1), new SimpleIntegerProperty(1));
        
        double fullHealth = c.getHealth().getValue();
        BasicEnemy v = vBuilding.spawnEnemy(orderedPath, c, false);
        double ogVHealth = v.getHealth();
        game.addEnemy(v);
        
        // set character close to vampire
        c.setX(v.getX());
        c.setY(v.getY());
        // create barracks
        Barracks barracks = new Barracks(new SimpleIntegerProperty(c.getX()), new SimpleIntegerProperty(c.getY()));
        game.addToBuildingEntities(barracks);
        ArrayList<AlliedSoldier> soldiers = game.possiblyCreateAlliedSoldier();
        double ogSoldierHealth = soldiers.get(0).getHealth();
        // run battles once
        ArrayList<BasicEnemy> enemies = game.getEnemiesInBattle();
        game.runBattles(enemies);
        assertEquals(v.getHealth(), ogVHealth - soldiers.get(0).getDamage() - c.getDamage());
        assertEquals(soldiers.get(0).getHealth(), ogSoldierHealth - v.getDamage());
        // check character's health has also decreased
        assertEquals(c.getHealth().getValue(), fullHealth);
        // check soldier's health decreased
    }

    @Test
    public void testKillEnemies() {
        c.setX(0);
        c.setY(0);
        ZombiePitBuilding zBuilding = new ZombiePitBuilding(new SimpleIntegerProperty(1), new SimpleIntegerProperty(1));
        Sword s = new Sword(null, null);

        BasicEnemy z = zBuilding.spawnEnemy(orderedPath, c, false);
        game.addEnemy(z);
        // equip a sword
        c.setEquippedWeapon(s);

        // set character close to zombie
        c.setX(z.getX());
        c.setY(z.getY());

        // run battles once to kill zombie
        ArrayList<BasicEnemy> enemies = game.getEnemiesInBattle();
        game.runBattles(enemies);

        // assert enemy has no health
        assertEquals(z.getHealth(), 0.0);
    }

    @Test
    public void testZombieMovement() {
        // test the zombie movement logic
        ZombiePitBuilding zBuilding = new ZombiePitBuilding(new SimpleIntegerProperty(1), new SimpleIntegerProperty(1));
        // zombies only spawn at heros castle
        c.setX(0);
        c.setY(0);
        Zombie z = (Zombie) zBuilding.spawnEnemy(orderedPath, c, false);
        z.setWasMovedLastRound(false);
        z.move();
        assertTrue(z.getWasMovedLastRound());
        z.move();
        assertFalse(z.getWasMovedLastRound());
    }

    @Test
    public void testVampireMovement() {
        // Makes sure a vampire can move on a path
        LoopManiaWorld game = new LoopManiaWorld(3, 3, orderedPath, new AndCompositeGoal(), new ArrayList<>(),
        new BerserkerMode());
        Character c = game.getCharacter();
        
        c.setX(0);
        c.setY(0);
        for (int i = 0; i < 5; i++) {
            c.increaseCyclesCompleted();
        }
        assertEquals(5, c.getCyclesCompleted().intValue());
        VampireCastleBuilding zBuilding = new VampireCastleBuilding(new SimpleIntegerProperty(1), new SimpleIntegerProperty(1));
        Vampire v = (Vampire) zBuilding.spawnEnemy(orderedPath, c, false);
        assertNotNull(v);

        int oldX = v.getX();
        int oldY = v.getY();

        v.move();
        assertTrue(oldX != v.getX());
        assertTrue(oldY != v.getY());
    }


    @Test
    public void battleElan(){
        c.setX(0);
        c.setY(0);
        for (int i = 0; i < 40; i ++) {
            c.increaseCyclesCompleted();
        }
        VampireCastleBuilding vBuilding = new VampireCastleBuilding(new SimpleIntegerProperty(1), new SimpleIntegerProperty(1));
        BasicEnemy v = vBuilding.spawnEnemy(orderedPath, c, false);
        game.addEnemy(v);
        v.setHealth(80);
        Rocket rocket = new Rocket(new SimpleIntegerProperty(1), new SimpleIntegerProperty(1));
        c.setExperience(120000);
        BasicEnemy elan = rocket.spawnEnemy(orderedPath, c, false);
        game.addEnemy(elan);
        // set character close to vampire
        c.setX(v.getX());
        c.setY(v.getY());
        // run battles once
        ArrayList<BasicEnemy> enemies = game.getEnemiesInBattle();
        game.runBattles(enemies);
        assertTrue(v.getHealth() >= 50 || v.getIsAlliedSolider() > 0);        
    }

    @Test
    public void battleKillBoss(){
        c.setX(0);
        c.setY(0);
        for (int i = 0; i < 40; i ++) {
            c.increaseCyclesCompleted();
        }

        Rocket rocket = new Rocket(new SimpleIntegerProperty(1), new SimpleIntegerProperty(1));
        c.setExperience(120000);
        BasicEnemy elan = rocket.spawnEnemy(orderedPath, c, false);
        game.addEnemy(elan);
        // set character close to vampire
        c.setX(elan.getX());
        c.setY(elan.getY());
        
        // run battles once
        ArrayList<BasicEnemy> enemies = game.getEnemiesInBattle();
        elan.setHealth(0);
        game.runBattles(enemies);
        assertTrue(c.getBossesKilled().contains((Boss) elan));
        assertFalse(game.getEnemies().contains(elan));
    }

    /**
     * one ring not yet used in normal game mode
     */
    @Test
    public void battleOneRingNormalMode(){
        c.setX(0);
        c.setY(0);
        for (int i = 0; i < 5; i ++) {
            c.increaseCyclesCompleted();
        }
        VampireCastleBuilding vBuilding = new VampireCastleBuilding(new SimpleIntegerProperty(1), new SimpleIntegerProperty(1));
        BasicEnemy v = vBuilding.spawnEnemy(orderedPath, c, false);
        game.addEnemy(v);

        RareItem ring = new OneRing(new SimpleIntegerProperty(1), new SimpleIntegerProperty(1));
        game.addToRareItem(ring);
        c.setHealth(2);
        c.setX(v.getX());
        c.setY(v.getY());
        // run battles once
        ArrayList<BasicEnemy> enemies = game.getEnemiesInBattle();
        game.runBattles(enemies);
        assertFalse(game.getRareItems().contains(ring));
        assertEquals(c.getHealth().getValue(), Character.getFullhealth());

    }

    /**
     * one ring already used :(
    */
    @Test
    public void battleDead(){
        c.setX(0);
        c.setY(0);
        for (int i = 0; i < 5; i ++) {
            c.increaseCyclesCompleted();
        }
        VampireCastleBuilding vBuilding = new VampireCastleBuilding(new SimpleIntegerProperty(1), new SimpleIntegerProperty(1));
        BasicEnemy v = vBuilding.spawnEnemy(orderedPath, c, false);
        game.addEnemy(v);

        c.setHealth(2);
        c.setX(v.getX());
        c.setY(v.getY());
        // run battles once
        ArrayList<BasicEnemy> enemies = game.getEnemiesInBattle();
        game.runBattles(enemies);
        assertEquals(game.gameLost.getValue(), true);

    }

    

}