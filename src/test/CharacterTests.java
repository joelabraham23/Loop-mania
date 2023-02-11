package test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.util.ArrayList;
import java.util.Random;

import org.junit.Test;

import javafx.beans.property.SimpleIntegerProperty;
import unsw.loopmania.*;
import unsw.loopmania.Character;
import unsw.loopmania.Enemies.Vampire;
import unsw.loopmania.Items.Armour;
import unsw.loopmania.Items.EquippedItem;
import unsw.loopmania.Items.Helmet;
import unsw.loopmania.Items.Shield;
import unsw.loopmania.Items.Sword;
import unsw.loopmania.RareItems.Anduril;
import unsw.loopmania.RareItems.RareItem;
import unsw.loopmania.RareItems.TreeStump;
import unsw.loopmania.Goals.AndCompositeGoal;
import unsw.loopmania.GameModes.*;



public class CharacterTests {
    TestHelper helper = new TestHelper();
    ArrayList<org.javatuples.Pair<Integer, Integer>> orderedPath = helper.getOrderedPath();
    Random rand = new Random();
    LoopManiaWorld game = new LoopManiaWorld(3, 3, orderedPath, new AndCompositeGoal(), new ArrayList<>(),
            new BerserkerMode());
    Character c = game.getCharacter();

    @Test
    public void testCharHasEquipment() {
        Armour armour = new Armour(new SimpleIntegerProperty(), new SimpleIntegerProperty());
        assertFalse(c.charHasArmour());
        assertFalse(c.charHasShield());
        c.addGear(armour);
        assertTrue(c.charHasArmour());
        Shield shield = new Shield(new SimpleIntegerProperty(), new SimpleIntegerProperty());
        c.addGear(shield);
        assertTrue(c.charHasArmour());
        assertTrue(c.charHasShield());
        Helmet helmet = new Helmet(new SimpleIntegerProperty(), new SimpleIntegerProperty());
        c.addGear(helmet);
        assertNotNull(c.charHasHelmet());

    }

    @Test
    public void testCharDecreaseMoney() {
        c.decreaseMoney(10);
        // should keep it at 0
        assertEquals(c.getMoney().getValue(), 0);

    }

    @Test
    public void testCharacterStunnedAttack() {
        c.setSecondsStunned(3);
        Vampire v = new Vampire(null);
        double fullHealth = v.getHealth();
        c.attack(v, 0.3f);
        assertEquals(v.getHealth(), fullHealth);
        assertEquals(c.getSecondsStunned(), 2);

    }

    @Test
    public void testCharacterRareItemWeaponNormalMode() {
        RareItem anduril = new Anduril(new SimpleIntegerProperty(), new SimpleIntegerProperty());
        c.setEquippedWeapon((Item) anduril);

        Vampire v = new Vampire(null);
        double fullHealth = v.getHealth();
        c.attack(v, 0.3f);
        assertEquals(v.getHealth(), fullHealth - (c.getDamage() * (1.5)));
    }

    @Test
    public void testCharacterRareItemWeaponConfusionModeWeaponOuter() {

        RareItem tree = new TreeStump(new SimpleIntegerProperty(), new SimpleIntegerProperty());
        RareItem anduril = new Anduril(new SimpleIntegerProperty(), new SimpleIntegerProperty());
        anduril.setInnerItem(tree);
        c.setEquippedWeapon((Item) anduril);
        Vampire v = new Vampire(null);
        double fullHealth = v.getHealth();
        c.attack(v, 0.3f);
        assertEquals(v.getHealth(), fullHealth - (c.getDamage() * (1.5)));

    }

    @Test
    public void testCharacterRareItemWeaponConfusionModeWeaponInner() {

        RareItem tree = new TreeStump(new SimpleIntegerProperty(), new SimpleIntegerProperty());
        RareItem anduril = new Anduril(new SimpleIntegerProperty(), new SimpleIntegerProperty());
        tree.setInnerItem(anduril);
        c.setEquippedWeapon((Item) tree);
        Vampire v = new Vampire(null);
        double fullHealth = v.getHealth();
        c.attack(v, 0.3f);
        assertEquals(v.getHealth(), fullHealth - (c.getDamage() * (1.5)));

    }

    @Test
    public void testSetShieldNormal() {
        Shield s = new Shield(new SimpleIntegerProperty(), new SimpleIntegerProperty());
        c.setShield((Item) s);

        Shield newS = new Shield(new SimpleIntegerProperty(), new SimpleIntegerProperty());
        Item res = c.setShield((Item) newS);

        // ensure we remove s and it is returned
        assertEquals(s, res);
        assertFalse(c.getProtectiveGear().contains(s));
        assertTrue(c.getProtectiveGear().contains(newS));

    }

    @Test
    public void testSetShieldOldShieldNewRareItem() {
        Shield s = new Shield(new SimpleIntegerProperty(), new SimpleIntegerProperty());
        c.setShield((Item) s);

        Item tree = new TreeStump(new SimpleIntegerProperty(), new SimpleIntegerProperty());

        Item res = c.setShield(tree);

        // ensure we remove s and it is returned
        assertEquals(s, res);
        assertFalse(c.getProtectiveGear().contains(s));
        assertTrue(c.getProtectiveGear().contains(tree));

    }

    @Test
    public void testSetShieldOldShieldNewRareItemConfusion() {
        Shield s = new Shield(new SimpleIntegerProperty(), new SimpleIntegerProperty());
        c.setShield((Item) s);

        RareItem tree = new TreeStump(new SimpleIntegerProperty(), new SimpleIntegerProperty());
        RareItem anduril = new Anduril(new SimpleIntegerProperty(), new SimpleIntegerProperty());
        anduril.setInnerItem(tree);
        Item res = c.setShield((Item) anduril);

        // ensure we remove s and it is returned
        assertEquals(s, res);
        assertFalse(c.getProtectiveGear().contains(s));
        assertTrue(c.getProtectiveGear().contains((Item) anduril));

    }

    @Test
    public void testSetAmour() {
        Armour a = new Armour(new SimpleIntegerProperty(), new SimpleIntegerProperty());
        c.setArmour((Item) a);
        Armour a2 = new Armour(new SimpleIntegerProperty(), new SimpleIntegerProperty());
       
        Item res = c.setArmour((Item) a2);

        // ensure we remove s and it is returned
        assertEquals(a, res);
        assertFalse(c.getProtectiveGear().contains(a));
        assertTrue(c.getProtectiveGear().contains((Item) a2));

    }

    @Test
    public void testSetHelmet() {
        Helmet h = new Helmet(new SimpleIntegerProperty(), new SimpleIntegerProperty());
        c.setHelmet((Item) h);
        Helmet h2 = new Helmet(new SimpleIntegerProperty(), new SimpleIntegerProperty());
       
        Item res = c.setHelmet((Item) h2);

        // ensure we remove s and it is returned
        assertEquals(h, res);
        assertFalse(c.getProtectiveGear().contains(h));
        assertTrue(c.getProtectiveGear().contains((Item) h2));

    }

    @Test
    public void testcheckItemsToRemoveNormalWeapon() {
        Sword s = new Sword(new SimpleIntegerProperty(), new SimpleIntegerProperty());

        c.setEquippedWeapon(s);
        s.setroundsLeft(0);
        c.checkItemsToRemove();
        assertNull(c.getEquippedWeapon());

    }

    @Test
    public void testcheckItemsToRemoveRareWeaponNormalMode() {
        RareItem anduril = new Anduril(new SimpleIntegerProperty(), new SimpleIntegerProperty());

        c.setEquippedWeapon((Item) anduril);
        ((EquippedItem) anduril).setroundsLeft(0);

        c.checkItemsToRemove();
        assertNull(c.getEquippedWeapon());

    }

    @Test
    public void testcheckItemsToRemoveRareWeaponConfusionMode() {
        RareItem anduril = new Anduril(new SimpleIntegerProperty(), new SimpleIntegerProperty());
        RareItem tree = new TreeStump(new SimpleIntegerProperty(), new SimpleIntegerProperty());
        anduril.setInnerItem(tree);
        c.setEquippedWeapon((Item) anduril);
        ((EquippedItem) anduril).setroundsLeft(0);
        
        c.checkItemsToRemove();
        assertNull(c.getEquippedWeapon());

    }

    @Test
    public void testcheckItemsToRemoveGear() {
        
        Helmet h = new Helmet(new SimpleIntegerProperty(), new SimpleIntegerProperty());
        c.setHelmet(h);
        h.setroundsLeft(0);
        
        c.checkItemsToRemove();
        assertFalse(c.getProtectiveGear().contains(h));

    }

    @Test
    public void testcheckItemsToRemoveRareGear() {
        
        RareItem tree = new TreeStump(new SimpleIntegerProperty(), new SimpleIntegerProperty());
        c.setShield((Item) tree);
        ((EquippedItem) tree).setroundsLeft(0);
        
        c.checkItemsToRemove();
        assertFalse(c.getProtectiveGear().contains((Item) tree));

    }

}