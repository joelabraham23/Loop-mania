package test;
import unsw.loopmania.AlliedSoldier;
import unsw.loopmania.BasicEnemy;
import unsw.loopmania.Character;
import unsw.loopmania.LoopManiaWorld;
import unsw.loopmania.PathPosition;
import unsw.loopmania.SlugCreator;
import unsw.loopmania.Buildings.*;
import unsw.loopmania.Enemies.Vampire;
import unsw.loopmania.Enemies.Zombie;
import unsw.loopmania.Goals.AndCompositeGoal;
import unsw.loopmania.GameModes.*;


import org.javatuples.Pair;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.util.ArrayList;
import java.util.Arrays;

import org.junit.Test;

import javafx.beans.property.SimpleIntegerProperty;

public class BuildingTests {
    TestHelper helper = new TestHelper();
    ArrayList<Pair<Integer, Integer>> orderedPath = helper.getOrderedPath();
    LoopManiaWorld game = new LoopManiaWorld(3, 3, orderedPath, new AndCompositeGoal(), new ArrayList<>(),
            new BerserkerMode());
    @Test
    public void testBarracksCreateAlliedSoldier() {
        Character c = game.getCharacter();
        Barracks b = new Barracks(new SimpleIntegerProperty(c.getX()), new SimpleIntegerProperty(c.getY()));
       
        game.addToBuildingEntities(b);
        ArrayList<AlliedSoldier> soldiers = game.possiblyCreateAlliedSoldier();
        // expect soldier to have a health of 50 and damage of 10
        assertEquals(soldiers.get(0).getHealth(), 25);
        assertEquals(soldiers.get(0).getDamage(), 5);
    }

    @Test
    public void testCampfireDoubleDamage() {
        Campfire c = new Campfire(new SimpleIntegerProperty(1), new SimpleIntegerProperty(1));
        // create character
        Character character = new Character(null);
        int ogDamage = character.getDamage();
        // apply double damage
        c.doubleCharacterDamage(character);
        int newDamage = character.getDamage();
        assertEquals(ogDamage * 2, newDamage);
        // halve damage
        c.halveCharacterDamage(character);
        assertEquals(character.getDamage(), ogDamage);
    }


    @Test
    public void testShootingTower() {
        PathPosition path = new PathPosition(1, Arrays.asList(new Pair<>(1, 1), new Pair<>(1, 2)));
        // create 2 enemies to shoot at
        Vampire v = new Vampire(path);
        Zombie z = new Zombie(path);
        ArrayList<BasicEnemy> enemies = new ArrayList<>();
        enemies.add(v);
        enemies.add(z);

        // test their health decreases
        double originalV = v.getHealth();
        double originalZ = z.getHealth();

        TowerBuilding tower = new TowerBuilding(new SimpleIntegerProperty(1), new SimpleIntegerProperty(1));
        tower.attackEnemies(enemies);
        assertEquals(originalV - tower.getDamage(), v.getHealth());
        assertEquals(originalZ - tower.getDamage(), z.getHealth());
    }

    @Test
    public void testTrap() {
        PathPosition path = new PathPosition(1, Arrays.asList(new Pair<>(1, 1), new Pair<>(1, 2)));
        // create 2 enemies to shoot at
        Vampire v = new Vampire(path);
        
        double originalV = v.getHealth();
        Trap trap = new Trap(new SimpleIntegerProperty(), new SimpleIntegerProperty());
        trap.damageEnemy(v);
        assertEquals(originalV - trap.getDamage(), v.getHealth());
    }


    @Test
    public void testVillageHealth() {
        // create character and reduce health
        PathPosition path = new PathPosition(1, Arrays.asList(new Pair<>(1, 1), new Pair<>(1, 2)));
        Character c = new Character(path);
        c.setHealth(80);
        // check that reset health sets to 100
        Village village = new Village(new SimpleIntegerProperty(), new SimpleIntegerProperty());
        village.regainHealth(c);

        assertEquals(c.getHealth().getValue(), 95);
    }

    @Test
    public void testNoVillageRegainAtFullHealth() {
        Character c = new Character(null);
        c.setHealth(100);
        // check that reset health sets to 100
        Village village = new Village(new SimpleIntegerProperty(), new SimpleIntegerProperty());
        village.regainHealth(c);
        assertEquals(c.getHealth().getValue(), 100);

    }

    @Test
    public void checkOnlySpawnZombiesOnNewCycle() {
        Character c = game.getCharacter();
        ZombiePitBuilding z = new ZombiePitBuilding(null, null);
        c.setX(1);
        c.setY(1);
        Zombie zombie = z.spawnEnemy(orderedPath, c, false);
        assertNull(zombie);

        c.setX(0);
        c.setY(1);
        Zombie zombie1 = z.spawnEnemy(orderedPath, c, false);
        assertNull(zombie1);

        c.setX(1);
        c.setY(0);
        Zombie zombie2 = z.spawnEnemy(orderedPath, c, false);
        assertNull(zombie2);
    }

    @Test
    public void checkCorrectVampireSpawns() {
        LoopManiaWorld game = new LoopManiaWorld(3, 3, orderedPath, new AndCompositeGoal(), new ArrayList<>(),
            new BerserkerMode());       
        Character c = game.getCharacter();
        VampireCastleBuilding vc = new VampireCastleBuilding(null, null);
        
        // check only happens at heros castle
        c.setX(1);
        c.setY(1);
        Vampire vampire = vc.spawnEnemy(orderedPath, c, false);
        assertNull(vampire);

        c.setX(0);
        c.setY(1);
        Vampire vampire1 = vc.spawnEnemy(orderedPath, c, false);
        assertNull(vampire1);

        c.setX(1);
        c.setY(0);
        Vampire vampire2 = vc.spawnEnemy(orderedPath, c, false);
        assertNull(vampire2);

        // check doesnt work on wrong cycle
        c.setX(0);
        c.setY(0);
        c.increaseCyclesCompleted();
        Vampire vampire3 = vc.spawnEnemy(orderedPath, c, false);
        assertNull(vampire3);
    }

    @Test
    public void checkCorrectVampireSpawnPosition() {
        // create vampire not on pth
        // closest to 0,1 is 0,0
        VampireCastleBuilding vc = new VampireCastleBuilding(new SimpleIntegerProperty(0), new SimpleIntegerProperty(1));
        assertEquals(0, vc.getClosestPosition(orderedPath));

        // closest to 2,3 is 2,2
        VampireCastleBuilding vc1 = new VampireCastleBuilding(new SimpleIntegerProperty(2), new SimpleIntegerProperty(3));
        assertEquals(2, vc1.getClosestPosition(orderedPath));
    }



    @Test
    public void testDoggieMineSpawn() {
        LoopManiaWorld game = new LoopManiaWorld(3, 3, orderedPath, new AndCompositeGoal(), new ArrayList<>(),
            new BerserkerMode());       
        Character c = game.getCharacter();
        DoggieMine doggieMine = new DoggieMine(new SimpleIntegerProperty(1), new SimpleIntegerProperty(1));
        c.setX(0);
        c.setY(0);
        c.increaseCyclesCompleted();
        // make sure doesnt spawn on wrong cycle
        assertNull(doggieMine.spawnEnemy(orderedPath, c, false));
        
        // make sure doesnt spawn on wrong x and wrong cycle
        c.setX(1);
        c.setY(1);

        // give it 19 cycles
        for (int i = 0; i < 19; i++) {
            c.increaseCyclesCompleted();
        }

        // make sure doesnt spawn on wrong x, y, inBattle
        assertNull(doggieMine.spawnEnemy(orderedPath, c, true));

        c.setX(0);
        // make sure doesnt spawn on wrong y, inBattle
        assertNull(doggieMine.spawnEnemy(orderedPath, c, true));

        c.setY(0);
        // make sure doesnt spawn on wrong inBattle
        assertNull(doggieMine.spawnEnemy(orderedPath, c, true));

        // make sure spawns with correct values
        assertNotNull(doggieMine.spawnEnemy(orderedPath, c, false));
    }

    @Test
    public void testRocketSpawn() {
        LoopManiaWorld game = new LoopManiaWorld(3, 3, orderedPath, new AndCompositeGoal(), new ArrayList<>(),
            new BerserkerMode());       
        Character c = game.getCharacter();
        Rocket rocket = new Rocket(new SimpleIntegerProperty(1), new SimpleIntegerProperty(1));
        c.setX(0);
        c.setY(0);
        c.setExperience(0);
        c.increaseCyclesCompleted();
        // make sure doesnt spawn on wrong cycle
        assertNull(rocket.spawnEnemy(orderedPath, c, false));
        // make sure doesn't spawn on wrong cycle w/ right exp
        c.increaseExperience(10000);
        assertNull(rocket.spawnEnemy(orderedPath, c, false));
        
        // make sure doesnt spawn on wrong x and wrong cycle and wrong exp
        c.setX(1);
        c.setY(1);
        c.setExperience(0);

        // give it 19 cycles
        for (int i = 0; i < 39; i++) {
            c.increaseCyclesCompleted();
        }

        c.setExperience(10000);

        // make sure doesnt spawn on wrong x, y, inBattle
        assertNull(rocket.spawnEnemy(orderedPath, c, true));
        
        c.setX(0);
        assertNull(rocket.spawnEnemy(orderedPath, c, true));

        c.setY(0);
        // make sure doesnt spawn on wrong inBattle
        assertNull(rocket.spawnEnemy(orderedPath, c, true));

        // make sure spawns with correct values
        c.increaseExperience(10000);
        assertNotNull(rocket.spawnEnemy(orderedPath, c, false));
    }

    @Test
    public void testSlugCreatorFreePos() {
        SlugCreator creator = new SlugCreator();
        Character c = game.getCharacter();
        Pair<Integer, Integer> res = creator.possiblyGetFreePosition(orderedPath, c, 0);
        assertNotNull(res);

    }

}