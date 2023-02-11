package test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.util.ArrayList;
import java.util.Random;

import org.javatuples.Pair;
import org.junit.Test;

import javafx.beans.property.SimpleIntegerProperty;
import unsw.loopmania.BasicEnemy;
import unsw.loopmania.Character;
import unsw.loopmania.LoopManiaWorld;
import unsw.loopmania.Enemies.Slug;
import unsw.loopmania.Enemies.Bosses.Doggie;
import unsw.loopmania.Enemies.Bosses.Elan;
import unsw.loopmania.Goals.AndCompositeGoal;
import unsw.loopmania.Items.Armour;
import unsw.loopmania.Items.Helmet;
import unsw.loopmania.RareItems.Anduril;
import unsw.loopmania.RareItems.RareItemAction;
import unsw.loopmania.RareItems.TreeStump;
import unsw.loopmania.GameModes.*;

public class EnemyBossesTests {
    TestHelper helper = new TestHelper();
    ArrayList<org.javatuples.Pair<Integer, Integer>> orderedPath = helper.getOrderedPath();
    Random rand = new Random();
    LoopManiaWorld game = new LoopManiaWorld(3, 3, orderedPath, new AndCompositeGoal(), new ArrayList<>(),
            new BerserkerMode());
    Character c = game.getCharacter();

    @Test
    public void testDoggieAttackCharacterWithoutStun() {
        Character c = new Character(null);
        double fullHealth = c.getHealth().getValue();
        Doggie d = new Doggie(null);
        d.attackCharacter(c, 0.5f, 1);;
        assertEquals(fullHealth - d.getDamage(), c.getHealth().getValue());
    }

    @Test
    public void testDoggieAttackCharacterWithStun() {
        Character c = new Character(null);
        double fullHealth = c.getHealth().getValue();
        Doggie d = new Doggie(null);
        d.attackCharacter(c, 0.19f, 1);
        assertEquals(c.getSecondsStunned(), 1);
        assertEquals(fullHealth - d.getDamage(), c.getHealth().getValue());
    }

    @Test
    public void testDoggieRareItems() {
        Character c = new Character(null);
        double fullHealth = c.getHealth().getValue();
        Doggie d = new Doggie(null);
        // equip rare item
        Anduril sword = new Anduril(new SimpleIntegerProperty(), new SimpleIntegerProperty());
        c.addGear(sword);

        // equip rare shield
        TreeStump shield = new TreeStump(new SimpleIntegerProperty(), new SimpleIntegerProperty());
        c.addGear(shield);

        // equip normal helmet
        Helmet helmet = new Helmet(new SimpleIntegerProperty(), new SimpleIntegerProperty());
        c.addGear(helmet);

        d.attackCharacter(c, 0.19f, 1);
        double final_damage = d.getDamage();
        final_damage = shield.doMagic(final_damage, c, d, 0.0f, RareItemAction.DEFEND);
        final_damage = helmet.defend(final_damage, d);
        assertEquals(fullHealth - final_damage, c.getHealth().getValue());        
    }

    @Test
    public void testDoggieRewards() {
        Character c = new Character(null);
        Doggie d = new Doggie(null);
        assertNull(d.giveCardRewardForDefeat(new Pair<Integer, Integer>(1, 1)));
        assertNull(d.giveEquipmentRewardForDefeat(new Pair<Integer, Integer>(1, 1), new Random().nextInt(2)));
        d.giveCharacterStatsForDefeat(c);
        assertEquals(100, c.getDoggieCoins().getValue());
    }

    @Test
    public void testGearAffectElan() {
        Character c1 = new Character(null);
        Character c2 = new Character(null);
        Elan e = new Elan(null);
        Armour armour = new Armour(new SimpleIntegerProperty(), new SimpleIntegerProperty());
        c1.addGear(armour);
        e.attackCharacter(c1, 0.1f, 1);
        e.attackCharacter(c2, 0.1f, 1);
        assertEquals(c1.getHealth().getValue(), c2.getHealth().getValue());
    }

    @Test
    public void testElanTaxCollector() {
        Character c = new Character(null);
        double origMoney = c.getMoney().getValue();
        Elan e = new Elan(null);
        e.attackCharacter(c, 0.1f, 1);  
        double newMoney = c.getMoney().getValue();
        assertEquals(origMoney*0.9, newMoney);
    }

    @Test
    public void testElanSetTaxes() {
        Character c = new Character(null);
        Elan e = new Elan(null);
        // test incorrect branches
        e.attackCharacter(c, 0.1f, 1);  
        assertFalse(c.getIsTaxPayer());

        // now test correct values
        e.attackCharacter(c, 0.001f, 1);  
        assertTrue(c.getIsTaxPayer());

        // check more incorrect branches
        e.attackCharacter(c, 0.1f, 1);  
        assertTrue(c.getIsTaxPayer());

        e.attackCharacter(c, 0.001f, 1);  
        assertTrue(c.getIsTaxPayer());
    }

    @Test
    public void testElanHealEnemies() {
        Slug s = new Slug(null);
        Double origHealth = s.getHealth();

        Elan e = new Elan(null);
        ArrayList<BasicEnemy> enemies = new ArrayList<>();
        enemies.add(s);
        enemies.add(e); // checks elan doesnt heal himself
        e.healEnemies(enemies, 0.1f);
        assertEquals(origHealth, s.getHealth());      

        e.healEnemies(enemies, 0f);  
        assertEquals(origHealth + 5, s.getHealth());      
    }

    @Test
    public void testElanRewards() {
        Character c = new Character(null);
        Elan e = new Elan(null);
        assertNull(e.giveCardRewardForDefeat(new Pair<Integer, Integer>(1, 1)));
        assertNull(e.giveEquipmentRewardForDefeat(new Pair<Integer, Integer>(1, 1), new Random().nextInt(2)));
        e.giveCharacterStatsForDefeat(c);
        assertEquals(150, c.getMoney().getValue());
        assertEquals(1000, c.getExperience().getValue());
    }
}