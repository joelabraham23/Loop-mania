package test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.util.ArrayList;
import java.util.Random;

import unsw.loopmania.*;

import org.javatuples.Pair;
import org.junit.Test;

import javafx.beans.property.SimpleIntegerProperty;
import unsw.loopmania.AlliedSoldier;
import unsw.loopmania.Character;
import unsw.loopmania.Item;
import unsw.loopmania.LoopManiaWorld;
import unsw.loopmania.PathPosition;
import unsw.loopmania.Cards.BarracksCard;
import unsw.loopmania.Cards.CampfireCard;
import unsw.loopmania.Cards.TowerCard;
import unsw.loopmania.Cards.TrapCard;
import unsw.loopmania.Cards.VampireCastleCard;
import unsw.loopmania.Cards.VillageCard;
import unsw.loopmania.Cards.ZombiePitCard;
import unsw.loopmania.Enemies.Slug;
import unsw.loopmania.Enemies.Vampire;
import unsw.loopmania.Enemies.Zombie;
import unsw.loopmania.Goals.AndCompositeGoal;
import unsw.loopmania.Items.*;
import unsw.loopmania.RareItems.Anduril;
import unsw.loopmania.RareItems.RareItem;
import unsw.loopmania.RareItems.TreeStump;
import unsw.loopmania.GameModes.*;

public class EnemyTests {

    TestHelper helper = new TestHelper();
    ArrayList<org.javatuples.Pair<Integer, Integer>> orderedPath = helper.getOrderedPath();
    Random rand = new Random();
    LoopManiaWorld game = new LoopManiaWorld(3, 3, orderedPath, new AndCompositeGoal(), new ArrayList<>(),
            new BerserkerMode());
    Character c = game.getCharacter();

    @Test
    public void testSlugAttackCharacter() {
        Character c = new Character(null);
        double fullHealth = c.getHealth().getValue();
        Slug s = new Slug(null);
        s.attackCharacter(c, 0.1f, 1);
        // expect health to go down 10
        assertEquals(fullHealth - s.getDamage(), c.getHealth().getValue());
    }

    @Test
    public void testSlugAttackAlliedCharacter() {
        AlliedSoldier c = new AlliedSoldier(null);
        int fullHealth = c.getHealth();
        Slug s = new Slug(null);
        s.attackAlliedSolider(c, 0.1f);
        // expect health to go down 10
        assertEquals(fullHealth - s.getDamage(), c.getHealth());
    }



    @Test
    public void testZombieCriticalBite() {
        Zombie z = new Zombie(null);
        AlliedSoldier c = new AlliedSoldier(null);
        z.performCriticalbite(c);
        // check that c's isZombie parameter is set to true
        assertTrue(c.getIsZombie());
    }

    @Test
    public void testVampireCriticalBite() {
        Vampire v = new Vampire(null);
        Character c = new Character(null);
        // set character to be in critical bite for now
        int rounds = 5;
        c.setSecondsLeftInCriticalBite(rounds);
        double fullHealth = c.getHealth().getValue();
        int vampireDamage = v.getDamage();
        // get vampire to attack character 
        v.attackCharacter(c, 0.1f, 1);
        // expect health to be - 10
        assertEquals(fullHealth - 10 - vampireDamage, c.getHealth().getValue());
        // check number of rounds updated
        assertEquals(rounds - 1, c.getSecondsLeftInCriticalBite());
    }

    @Test
    public void testSlugAttackArmour() {
        Character c = new Character(null);
        // give character armour
        Armour armour = new Armour(new SimpleIntegerProperty(), new SimpleIntegerProperty());
        c.addGear(armour);
        
        double fullHealth = c.getHealth().getValue();
        Slug s = new Slug(null);
        s.attackCharacter(c, 0.1f, 1);
        // should go down to 0.
        assertEquals(fullHealth - (s.getDamage()/2.0), c.getHealth().getValue());
    }

    @Test
    public void testSlugAttackShield() {
        Character c = new Character(null);
        Shield shield = new Shield(new SimpleIntegerProperty(), new SimpleIntegerProperty());
        double fullHealth = c.getHealth().getValue();
        Slug s = new Slug(null);
        c.addGear(shield);
        s.attackCharacter(c, 0.1f, 1);
        // should go down to 0.
        assertEquals(fullHealth - (s.getDamage()*(1-shield.getPercentDecreaseInAttack())), c.getHealth().getValue());
    }

    @Test
    public void testSlugAttackHelmetNoWeapon() {
        Character c = new Character(null);
        Helmet helmet = new Helmet(new SimpleIntegerProperty(), new SimpleIntegerProperty());
        double fullHealth = c.getHealth().getValue();
        Slug s = new Slug(null);
        double fullHealthSlug = s.getHealth();
        c.addGear(helmet);
        s.attackCharacter(c, 0.1f, 1);
        // should go down to 0.
        assertEquals(c.getHealth().getValue(), fullHealth - (s.getDamage()*(1-helmet.getPercentDecreaseInEnemyAttack())));
        // character attack slug, check their attack also decreased
       c.attack(s, rand.nextInt());
       // check slug's health is notthat low
       assertEquals(fullHealthSlug - c.getDamage()*(1-helmet.getPercentDecreaseInCharacterAttack()), s.getHealth());
    }

    @Test
    public void testSlugAttackHelmetWithWeapon() {
        Character c = new Character(null);
        Helmet helmet = new Helmet(new SimpleIntegerProperty(), new SimpleIntegerProperty());
        Sword sword = new Sword(new SimpleIntegerProperty(), new SimpleIntegerProperty());
        Slug s = new Slug(null);
        double fullHealthSlug = s.getHealth();
        c.addGear(helmet);
        c.setEquippedWeapon(sword);
        // character attack slug, check their attack also decreased
       c.attack(s, rand.nextInt());
       // check slug's health is notthat low
       double charDamageWithHelment = c.getDamage()*(1-helmet.getPercentDecreaseInCharacterAttack());
       double charDamageWithScord = charDamageWithHelment * 2.0;
       assertEquals(fullHealthSlug - charDamageWithScord, s.getHealth());
    }

    @Test
    public void testSlugAttackMultipleGears() {
        Character c = new Character(null);
        Helmet helmet = new Helmet(new SimpleIntegerProperty(), new SimpleIntegerProperty());
        Armour armour = new Armour(new SimpleIntegerProperty(), new SimpleIntegerProperty());
        double fullHealth = c.getHealth().getValue();
        Slug s = new Slug(null);
        c.addGear(helmet);
        c.addGear(armour);

        s.attackCharacter(c, 0.1f, 1);
        // expect damage to be *0.6 from helment and halved from armour
        assertEquals(fullHealth - ((s.getDamage()*0.6))/2, c.getHealth().getValue());
    }

    @Test
    public void testHelmetAttackMultipleGears() {
        Character c = new Character(null);
        Helmet helmet = new Helmet(new SimpleIntegerProperty(), new SimpleIntegerProperty());
        Armour armour = new Armour(new SimpleIntegerProperty(), new SimpleIntegerProperty());
        Slug s = new Slug(null);
        double fullHealth = s.getHealth();
        c.addGear(helmet);
        c.addGear(armour);
        c.attack(s, rand.nextInt());
        double charDamageWithHelment = c.getDamage()*(1-helmet.getPercentDecreaseInCharacterAttack());
        assertEquals(fullHealth - charDamageWithHelment, s.getHealth());
    }

    @Test
    public void testZombieAttackAlliedSoldier() {
        AlliedSoldier as = new AlliedSoldier(null);
        int fullHealth = as.getHealth();
        Zombie z = new Zombie(null);
        // give it the randomness for non-crit-bite
        z.attackAlliedSolider(as, 0.5f);
        // the allied soldier should not be a zombie
        assertFalse(as.getIsZombie());
        assertEquals((fullHealth - z.getDamage()), as.getHealth());
    }

    @Test
    public void testVampireAttackCriticalBite() {
        Character c = new Character(null);
        Vampire v = new Vampire(null);
        int secondsInCritial = 3;
        v.attackCharacter(c, 0.03f, secondsInCritial);
        assertEquals(secondsInCritial - 1, c.getSecondsLeftInCriticalBite());
    }

    @Test
    public void testVampireAttackCharacterOtherGear() {
        Vampire v = new Vampire(null);
        Character c = new Character(null);
        Helmet h = new Helmet(null, null);
        double oldChangeCritBite = v.getChanceOfCriticalBite();
        int secondsInCritial = 3;
        // Give the character a helmet
        c.addGear(h);
        // get make the random chance
        // less then the non-shield crit rate
        float randomChance = (float) (oldChangeCritBite - 0.01);
        v.attackCharacter(c, randomChance, secondsInCritial);
        assertEquals(secondsInCritial - 1, c.getSecondsLeftInCriticalBite());
    }

    @Test
    public void testVampireAttackCharacterShield() {
        Vampire v = new Vampire(null);
        Character c = new Character(null);
        Shield s = new Shield(null, null);
        //chanceOfCriticalBite * (1 - gearShield.getPercentDecreaseInVampireCriticalBite())
        double shieldCriticalBite = v.getChanceOfCriticalBite() * (1 - s.getPercentDecreaseInVampireCriticalBite());
        int secondsInCritial = 3;
        // Give the character a shield
        c.addGear(s);
        // get make the random chance something that
        // only works with shield crit rate
        float randomChance = (float) (shieldCriticalBite - 0.01);
        v.attackCharacter(c, randomChance, secondsInCritial);
        assertEquals(secondsInCritial - 1, c.getSecondsLeftInCriticalBite());
    }

    @Test
    public void testZombieAlliedSoldierCriticalBite() {
        AlliedSoldier as = new AlliedSoldier(null);
        Zombie z = new Zombie(null);
        // give it the randomness for crit-bite
        z.attackAlliedSolider(as, 0.09f);
        // the allied soldier should not be a zombie
        assertTrue(as.getIsZombie());
    }

    @Test
    public void testMoveBasic() {
        Slug slug = new Slug(new PathPosition(0, orderedPath));
        slug.move();
        assertFalse(slug.getX() == 0);
        assertFalse(slug.getY() == 0);
        
    }

    @Test
    public void testProduceSoldierReachedCap() {
        Slug slug = new Slug(new PathPosition(0, orderedPath));
        slug.setIsAlliedSolider(2);
        AlliedSoldier result = slug.produceAlliedSolider(c, orderedPath, 4);
        assertNull(result);

    }

    @Test
    public void testGetExperienceAwarded() {
        Slug slug = new Slug(new PathPosition(0, orderedPath));
        int exp = slug.getExperienceAwarded();
        assertEquals(exp, 100);

    }

    @Test
    public void testMoveInPath() {
        Vampire v = new Vampire(new PathPosition(0, orderedPath));
        v.move();
        // check continue in path
        Boolean currWay = v.getCurrentlyMovingUp();
        v.continueInPath();
        assertEquals(v.getCurrentlyMovingUp(), currWay);
    }

    @Test
    public void testMoveInPath2() {
        Vampire v = new Vampire(new PathPosition(0, orderedPath));
        v.moveDownPath();
        // check continue in path
        Boolean currWay = v.getCurrentlyMovingUp();
        v.continueInPath();
        assertEquals(v.getCurrentlyMovingUp(), currWay);
    }

    @Test
    public void testVampireReversePath() {
        Vampire v = new Vampire(new PathPosition(0, orderedPath));
        v.move();
        v.moveUpPath();
        // check continue in path
        Boolean currWay = v.getCurrentlyMovingUp();
        v.reversePath();
        assertTrue(c.getCurrentlyMovingUp() == currWay);
    }
    
    @Test
    public void testRandomRewardZombie() {
        Zombie z = new Zombie(null);
        Item reward1 = z.giveEquipmentRewardForDefeat(new Pair<Integer, Integer>(1, 1), 0);
        assertTrue(reward1 instanceof Stake);

        Item reward2 = z.giveEquipmentRewardForDefeat(new Pair<Integer, Integer>(1, 1), 1);
        assertTrue(reward2 instanceof Helmet);
    }

    @Test
    public void vampireAttackCharacterWithRareItemNormalMode() {
        Item tree = new TreeStump(new SimpleIntegerProperty(), new SimpleIntegerProperty());
        c.setShield(tree);
        double fullHealth = c.getHealth().getValue();
        Vampire v = new Vampire(null);
        v.attackCharacter(c, 1, 1);
        // damage to the character should be 0.7 treeStump's damage
        assertEquals(c.getHealth().getValue(), fullHealth - (0.7 * v.getDamage()) );
    }

    @Test
    public void vampireAttackCharacterWithRareItemConfusingMode() {
        RareItem tree = new TreeStump(new SimpleIntegerProperty(), new SimpleIntegerProperty());
        RareItem anduril = new Anduril(new SimpleIntegerProperty(), new SimpleIntegerProperty());
        tree.setInnerItem(anduril);
        c.setShield((Item) tree);
        double fullHealth = c.getHealth().getValue();
        Vampire v = new Vampire(null);
        v.attackCharacter(c, 1, 1);
        // damage to the character should be 0.7 treeStump's damage
        assertEquals(c.getHealth().getValue(), fullHealth - (0.7 * v.getDamage()) );
    }

    @Test
    public void vampireAttackCharacterShield() {
        RareItem tree = new TreeStump(new SimpleIntegerProperty(), new SimpleIntegerProperty());
        RareItem anduril = new Anduril(new SimpleIntegerProperty(), new SimpleIntegerProperty());
        anduril.setInnerItem(tree);
        c.setShield((Item) anduril);
        Vampire v = new Vampire(null);
        double chanceBite = v.getChanceOfCriticalBite();
        // chance of critical bite is going ot be 0.6 * v.getChanceOfCriticalBite()
        v.attackCharacter(c, (float) (0.6 * chanceBite), 1);
        // damage to the character should be 0.7 treeStump's damage
        assertEquals(c.getHealth().getValue(), 100 - (0.7 * v.getDamage()) );
    }

    @Test
    public void vampireEquipmentReward() {
       
        Vampire v = new Vampire(null);
        Item reward = v.giveEquipmentRewardForDefeat(new Pair<Integer, Integer>(1, 1), 1);
        assertTrue(reward instanceof Shield);
    }

    @Test
    public void testBasicEnemyTreeStump() {
        RareItem tree = new TreeStump(new SimpleIntegerProperty(), new SimpleIntegerProperty());
        RareItem anduril = new Anduril(new SimpleIntegerProperty(), new SimpleIntegerProperty());
        anduril.setInnerItem(tree);
        c.setShield((Item) anduril);
        double fullHealth = c.getHealth().getValue();
        Slug s = new Slug(null);
        s.attackCharacter(c, 1, 1);
        // damage to the character should be 0.7 treeStump's damage
        assertEquals(c.getHealth().getValue(), fullHealth - (0.7 * s.getDamage()) );
    }

    @Test
    public void testGenerateCards() {
        Slug s = new Slug(null);
        Card res = s.generateRandomCard(5, 0);
        assertTrue(res instanceof VampireCastleCard);
        res = s.generateRandomCard(15, 0);
        assertTrue(res instanceof VillageCard);
        res = s.generateRandomCard(26, 0);
        assertTrue(res instanceof TrapCard);
        res = s.generateRandomCard(60, 0);
        assertTrue(res instanceof TowerCard);
        res = s.generateRandomCard(95, 0);
        assertTrue(res instanceof BarracksCard);
        res = s.generateRandomCard(35, 0);
        assertTrue(res instanceof CampfireCard);
        res = s.generateRandomCard(80, 0);
        assertTrue(res instanceof ZombiePitCard);
    }
}

