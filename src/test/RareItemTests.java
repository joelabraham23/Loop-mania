package test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import unsw.loopmania.Character;

import org.junit.Test;

import javafx.beans.property.SimpleIntegerProperty;
import unsw.loopmania.Enemies.*;
import unsw.loopmania.Enemies.Bosses.Elan;
import unsw.loopmania.RareItems.Anduril;
import unsw.loopmania.RareItems.OneRing;
import unsw.loopmania.RareItems.RareItemAction;
import unsw.loopmania.RareItems.TreeStump;

public class RareItemTests {
    
    @Test
    public void testNormalTreeStumpDefend() {
        // attack from vampire
        Vampire v = new Vampire(null);
        int originalDamage = v.getDamage();

        // create ts
        TreeStump ts = new TreeStump(new SimpleIntegerProperty(), new SimpleIntegerProperty());

        // defend character
        double actualDamage = ts.defend(originalDamage, v);

        // check damage is less
        double expectedDamage = originalDamage * 0.7;

        // Check that damage from attack is halved and gone to character health pool.
        assertEquals(expectedDamage, actualDamage);
    }

    @Test
    public void testBossTreeStumpDefend() {
        // attack from vampire
        Elan e = new Elan(null);
        int originalDamage = e.getDamage();

        // create ts
        TreeStump ts = new TreeStump(new SimpleIntegerProperty(), new SimpleIntegerProperty());

        // defend character
        double actualDamage = ts.defend(originalDamage, e);

        // check damage is less
        double expectedDamage = originalDamage / 2;

        // Check that damage from attack is halved and gone to character health pool.
        assertEquals(expectedDamage, actualDamage);
    }

    @Test
    public void testBossTreeStumpDoMagicInnerItem() {
        Elan e = new Elan(null);
        int originalDamage = e.getDamage();
        Character c = new Character(null);

        // create ts
        TreeStump ts = new TreeStump(new SimpleIntegerProperty(), new SimpleIntegerProperty());
        double actualDamage = ts.doMagic(originalDamage, c, e, 0.1f, RareItemAction.ATTACK);
        // because there is no inner item, and this is not an attack item
        // do nothing
        assertEquals(originalDamage, actualDamage);

        Anduril sword = new Anduril(new SimpleIntegerProperty(), new SimpleIntegerProperty());
        ts.setInnerItem(sword);
        double actualDamage1 = ts.doMagic(originalDamage, c, e, 0.1f, RareItemAction.ATTACK);
        // because there is now an inner item
        // it counts as an attack (* 3 because elan)
        assertEquals(originalDamage * 3, actualDamage1);
    }

    @Test
    public void testTreeStumpIsSword() {
        TreeStump ts = new TreeStump(new SimpleIntegerProperty(), new SimpleIntegerProperty());
        assertFalse(ts.isSword());
        
        Anduril sword = new Anduril(new SimpleIntegerProperty(), new SimpleIntegerProperty());
        TreeStump ts1 = new TreeStump(new SimpleIntegerProperty(), new SimpleIntegerProperty(), sword);
        assertTrue(ts1.isSword());
    }

    @Test
    public void testTreeStumpEndOfLife() {
        TreeStump ts = new TreeStump(new SimpleIntegerProperty(), new SimpleIntegerProperty());
        // check default round left
        assertFalse(ts.endOfLife());
        assertEquals(7, ts.getroundsLeft());
        ts.decreaseRoundsLeft();
        assertEquals(6, ts.getroundsLeft());
        for (int i = 0; i < 6; i++) {
            ts.decreaseRoundsLeft();
        }
        assertTrue(ts.endOfLife());
    }

    @Test
    public void testTreeStumpInnerEndOfLife() {
        TreeStump ts = new TreeStump(new SimpleIntegerProperty(), new SimpleIntegerProperty());
        Anduril sword = new Anduril(new SimpleIntegerProperty(), new SimpleIntegerProperty());
        ts.setInnerItem(sword);
        assertFalse(ts.endOfLife());
        // now make sure when sword dies, ts dies
        assertEquals(7, sword.getroundsLeft());
        for (int i = 0; i < 7; i++) {
            sword.decreaseRoundsLeft();
        }
        assertTrue(ts.endOfLife());
    }

    @Test
    public void testNormalAndurilAttack() {
        Anduril sword = new Anduril(new SimpleIntegerProperty(), new SimpleIntegerProperty());
        Vampire v = new Vampire(null);
        int originalDamage = v.getDamage();

        double actualDamage = sword.attack(originalDamage, v, 0.1f);
        double expectedDamage = originalDamage * 1.5;

        assertEquals(expectedDamage, actualDamage);
    }

    @Test
    public void testAndurilIsShield() {
        Anduril sword = new Anduril(new SimpleIntegerProperty(), new SimpleIntegerProperty());
        assertFalse(sword.isShield());
        
        TreeStump ts = new TreeStump(new SimpleIntegerProperty(), new SimpleIntegerProperty());
        sword.setInnerItem(ts);
        assertTrue(sword.isShield());
    }

    @Test
    public void testAndurilDoMagic() {
        Elan e = new Elan(null);
        int originalDamage = e.getDamage();
        Character c = new Character(null);
        Anduril sword = new Anduril(new SimpleIntegerProperty(), new SimpleIntegerProperty());

        double actualDamage = sword.doMagic(originalDamage, c, e, 0.1f, RareItemAction.DEFEND);
        // because there is no inner item, and this is not a defend item
        // do nothing
        assertEquals(originalDamage, actualDamage);

        TreeStump ts = new TreeStump(new SimpleIntegerProperty(), new SimpleIntegerProperty());
        Anduril sword1 = new Anduril(new SimpleIntegerProperty(), new SimpleIntegerProperty());
        sword1.setInnerItem(ts);
        double actualDamage1 = sword1.doMagic(originalDamage, c, e, 0.1f, RareItemAction.DEFEND);
        // because there is now an inner item
        // it counts as an attack (/2 because elan)
        assertEquals(originalDamage/2, actualDamage1);
    }

    @Test
    public void testAndurilEndOfLife() {
        TreeStump ts = new TreeStump(new SimpleIntegerProperty(), new SimpleIntegerProperty());
        for (int i = 0; i < 7; i++) {
            ts.decreaseRoundsLeft();
        }
        Anduril sword = new Anduril(new SimpleIntegerProperty(), new SimpleIntegerProperty());
        sword.setInnerItem(ts);
        assertEquals(7, sword.getroundsLeft());
        assertTrue(sword.endOfLife());
    }

    @Test
    public void testOneRingRespawn() {
        OneRing r = new OneRing(new SimpleIntegerProperty(), new SimpleIntegerProperty());
        Character c = new Character(null);
        // too much health
        c.setHealth(100);
        assertFalse(r.respawnCharacter(c));

        // can be respawned
        c.setHealth(0);
        assertTrue(r.respawnCharacter(c));
        assertEquals(100, c.getHealth().getValue());
        assertTrue(r.getHasBeenUsed());
        
        // already been used
        c.setHealth(0);
        r.respawnCharacter(c);
        assertEquals(0, c.getHealth().getValue());
    }

    @Test
    public void testOneRingDoMagic() {
        Elan e = new Elan(null);
        int originalDamage = e.getDamage();
        Character c = new Character(null);
        c.setHealth(0); // kill enemy to test respawn

        OneRing r = new OneRing(new SimpleIntegerProperty(), new SimpleIntegerProperty());
        double actualDamage = r.doMagic(originalDamage, c, e, 0.1f, RareItemAction.DEFEND);
        // because there is no inner item, and this is not a defend item
        assertEquals(originalDamage, actualDamage);

        // but because it is a heal item, check healing still works
        r.doMagic(originalDamage, c, e, 0.1f, RareItemAction.HEAL);
        assertEquals(100, c.getHealth().getValue());

    }

    @Test
    public void testOneRingInnerMagic() {
        Elan e = new Elan(null);
        int originalDamage = e.getDamage();
        Character c = new Character(null);
        TreeStump ts = new TreeStump(new SimpleIntegerProperty(), new SimpleIntegerProperty());
        OneRing r1 = new OneRing(new SimpleIntegerProperty(), new SimpleIntegerProperty());
        r1.setInnerItem(ts);

        double actualDamage1 = r1.doMagic(originalDamage, c, e, 0.1f, RareItemAction.DEFEND);
        // because there is now an inner item
        // it counts as an attack (/2 because elan)
        assertEquals(originalDamage/2, actualDamage1);
    }

    @Test
    public void testOneRingShield() {
        TreeStump ts = new TreeStump(new SimpleIntegerProperty(), new SimpleIntegerProperty());
        OneRing r1 = new OneRing(new SimpleIntegerProperty(), new SimpleIntegerProperty());
        assertFalse(r1.isShield());
        r1.setInnerItem(ts);
        assertTrue(r1.isShield());
    }

    @Test
    public void testOneRingSword() {
        Anduril sword = new Anduril(new SimpleIntegerProperty(), new SimpleIntegerProperty());
        OneRing r1 = new OneRing(new SimpleIntegerProperty(), new SimpleIntegerProperty());
        assertFalse(r1.isSword());
        r1.setInnerItem(sword);
        assertTrue(r1.isSword());
    }

    @Test
    public void testEndOfLifeOneRingInner() {
        OneRing r = new OneRing(new SimpleIntegerProperty(), new SimpleIntegerProperty());
        assertFalse(r.endOfLife());
        
        Anduril sword = new Anduril(new SimpleIntegerProperty(), new SimpleIntegerProperty());
        r.setInnerItem(sword);

        for (int i = 0 ; i < 7; i++) {
            sword.decreaseRoundsLeft();
        }
        assertTrue(r.endOfLife());
    }

    @Test
    public void testEndOfLifeOneRingUsed() {
        OneRing r = new OneRing(new SimpleIntegerProperty(), new SimpleIntegerProperty());
        Character c = new Character(null);
        assertFalse(r.endOfLife());

        c.setHealth(0);
        r.respawnCharacter(c);
        assertTrue(r.endOfLife());

    }

    @Test
    public void testTreeStumpCanHeal() {
        TreeStump ts = new TreeStump(new SimpleIntegerProperty(), new SimpleIntegerProperty());
        assertFalse(ts.canHeal());
    }

    @Test
    public void testTreeStumpWithRingCanHeal() {
        TreeStump ts = new TreeStump(new SimpleIntegerProperty(), new SimpleIntegerProperty());
        OneRing r = new OneRing(new SimpleIntegerProperty(), new SimpleIntegerProperty());
        ts.setInnerItem(r);
        assertTrue(ts.canHeal());
    }

    @Test
    public void testAndurilCanHeal() {
        Anduril ts = new Anduril(new SimpleIntegerProperty(), new SimpleIntegerProperty());
        assertFalse(ts.canHeal());
    }

    @Test
    public void testAndurilWithRingCanHeal() {
        Anduril ts = new Anduril(new SimpleIntegerProperty(), new SimpleIntegerProperty());
        OneRing r = new OneRing(new SimpleIntegerProperty(), new SimpleIntegerProperty());
        ts.setInnerItem(r);
        assertTrue(ts.canHeal());
    }
}
