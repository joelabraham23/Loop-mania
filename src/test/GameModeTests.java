package test;

import unsw.loopmania.Item;
import unsw.loopmania.LoopManiaWorld;
import unsw.loopmania.Character;
import unsw.loopmania.Items.*;
import unsw.loopmania.GameModes.*;
import unsw.loopmania.Goals.*;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.Test;

import org.javatuples.Pair;


public class GameModeTests {
    
    @Test
    public void testHerosCastleBeserkerCantBuy() {
        TestHelper helper = new TestHelper();
        BerserkerMode mode = new BerserkerMode();
        ArrayList<org.javatuples.Pair<Integer, Integer>> orderedPath = helper.getOrderedPath();
        LoopManiaWorld game = new LoopManiaWorld(3, 3, orderedPath, new AndCompositeGoal(), new ArrayList<>(),
                mode);
        Character c = game.getCharacter();
        
        Armour a = new Armour(null, null);
        Sword s = new Sword(null, null);
        boolean reachedRest = false;
        c.setMoney(0);
        // check dont have enough money
        assertTrue(mode.checkCantBuy(a, c, reachedRest));

        // check can buy non-protective gear normally
        c.setMoney(1000);
        assertFalse(mode.checkCantBuy(s, c, reachedRest));

        // check can buy armour if not reached restrictions
        assertFalse(mode.checkCantBuy(a, c, reachedRest));

        // check cant buy armour if reached restrictions
        reachedRest = true;
        assertTrue(mode.checkCantBuy(a, c, reachedRest));
    }

    @Test
    public void testHerosCastleBeserkerCantSell() {
        BerserkerMode mode = new BerserkerMode();
        Item a = new Armour(null, null);
        Item s = new Sword(null, null);
        List<Item> unequippedInventoryItems = new ArrayList<Item>();

        // try to sell an item with it not in inventory
        assertTrue(mode.checkCantSell(a, unequippedInventoryItems));

        // add armour and sword to inventory 
        // and make sure it only sells armour
        unequippedInventoryItems.add(s);
        unequippedInventoryItems.add(a);
        assertFalse(mode.checkCantSell(a, unequippedInventoryItems));
    }

    @Test
    public void testHerosCastleSurvivalCantBuy() {
        TestHelper helper = new TestHelper();
        SurvivalMode mode = new SurvivalMode();
        ArrayList<org.javatuples.Pair<Integer, Integer>> orderedPath = helper.getOrderedPath();
        LoopManiaWorld game = new LoopManiaWorld(3, 3, orderedPath, new AndCompositeGoal(), new ArrayList<>(),
                mode);
        Character c = game.getCharacter();

        HealthPotion hp = new HealthPotion(null, null);
        Sword s = new Sword(null, null);
        boolean reachedRest = false;
        c.setMoney(0);
        // check dont have enough money
        assertTrue(mode.checkCantBuy(hp, c, reachedRest));

        // check can buy non-health potion normally
        c.setMoney(1000);
        assertFalse(mode.checkCantBuy(s, c, reachedRest));

        // check can buy healthpotion if not reached restrictions
        assertFalse(mode.checkCantBuy(hp, c, reachedRest));

        // check cant buy healthpotion if reached restrictions
        reachedRest = true;
        assertTrue(mode.checkCantBuy(hp, c, reachedRest));
    }

    @Test
    public void testHerosCastleSurvivalCantSell() {
        SurvivalMode mode = new SurvivalMode();
        Item a = new Armour(null, null);
        Item s = new Sword(null, null);
        List<Item> unequippedInventoryItems = new ArrayList<Item>();

        // try to sell an item with it not in inventory
        assertTrue(mode.checkCantSell(a, unequippedInventoryItems));

        // add armour and sword to inventory 
        // and make sure it only sells armour
        unequippedInventoryItems.add(s);
        unequippedInventoryItems.add(a);
        assertFalse(mode.checkCantSell(a, unequippedInventoryItems));

    }

    @Test
    public void testHerosCastleStandardCantBuy() {

        TestHelper helper = new TestHelper();
        GameMode mode = new GameMode();
        ArrayList<org.javatuples.Pair<Integer, Integer>> orderedPath = helper.getOrderedPath();
        LoopManiaWorld game = new LoopManiaWorld(3, 3, orderedPath, new AndCompositeGoal(), new ArrayList<>(),
                mode);
        Character c = game.getCharacter();

        HealthPotion hp = new HealthPotion(null, null);
        Sword s = new Sword(null, null);
        boolean reachedRest = false;
        c.setMoney(0);
        // check dont have enough money
        assertTrue(mode.checkCantBuy(hp, c, reachedRest));

        // check can buy sword normally
        c.setMoney(1000);
        assertFalse(mode.checkCantBuy(s, c, reachedRest));

        // check can buy healthpotion if not reached restrictions
        assertFalse(mode.checkCantBuy(hp, c, reachedRest));

        // check can buy healthpotion as there are no restrictions
        reachedRest = true;
        assertFalse(mode.checkCantBuy(hp, c, reachedRest));
    }

    @Test
    public void testHerosCastleStandardCantSell() {
        GameMode mode = new GameMode();
        Item a = new Armour(null, null);
        Item s = new Sword(null, null);
        List<Item> unequippedInventoryItems = new ArrayList<Item>();

        // try to sell an item with it not in inventory
        assertTrue(mode.checkCantSell(a, unequippedInventoryItems));

        // add armour and sword to inventory 
        // and make sure it only sells armour
        unequippedInventoryItems.add(s);
        unequippedInventoryItems.add(a);
        assertFalse(mode.checkCantSell(a, unequippedInventoryItems));
    }

    @Test
    public void testConfusionMode() {
        TestHelper helper = new TestHelper();
        ConfusionMode mode = new ConfusionMode();
        ArrayList<org.javatuples.Pair<Integer, Integer>> orderedPath = helper.getOrderedPath();
        LoopManiaWorld game = new LoopManiaWorld(3, 3, orderedPath, new AndCompositeGoal(), new ArrayList<>(),
                mode);
        Pair<Integer, Integer> slot = game.getFirstAvailableSlotForRareItem();
        // test success with chance
        assertNotNull(mode.getRareItem(new ArrayList<String>(Arrays.asList("tree_stump","the_one_ring","anduril_flame_of_the_west")), slot, 0.1f));
        // test fail with chance
        assertNull(mode.getRareItem(new ArrayList<String>(Arrays.asList("tree_stump","the_one_ring","anduril_flame_of_the_west")), slot, 0.9f));
    }
}
