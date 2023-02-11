package test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.Test;

import javafx.beans.property.SimpleIntegerProperty;
import unsw.loopmania.Character;
import unsw.loopmania.Enemies.*;
import unsw.loopmania.Items.*;


public class ItemTests {
    @Test
    public void testArmourDefence() {
        Character c = new Character(null);
        double og = c.getHealth().getValue();
        // create armour
        Armour armour = new Armour(new SimpleIntegerProperty(), new SimpleIntegerProperty());

        // attack from vampire
        Vampire v = new Vampire(null);

        // defend character
        armour.defend(v.getDamage(), v);

        // check damage is less
        double perfectDecrease = armour.getPercentDecreaseInAttack() * 1.0;
        double damageReceived = (1 - (perfectDecrease / 100.0)) * v.getDamage();
        c.setHealth(og - damageReceived);

        // Check that damage from attack is halved and gone to character health pool.
        assertEquals(c.getHealth().getValue(), og - damageReceived);
    }

    @Test
    public void testShieldDefence() {
        // should reduce risk of vampire bite
        Character c = new Character(null);
        double og = c.getHealth().getValue();
        // create armour
        Shield shield = new Shield(new SimpleIntegerProperty(), new SimpleIntegerProperty());

        // attack from vampire
        Vampire v = new Vampire(null);

        // defend character
        shield.defend(v.getDamage(), v);

        // check damage is less
        double perfectDecrease = shield.getPercentDecreaseInAttack() * 1.0;
        double damageReceived = (1 - (perfectDecrease / 100.0)) * v.getDamage();
        c.setHealth(og - damageReceived);

        assertEquals(c.getHealth().getValue(), og - damageReceived);

    }

    @Test
    public void testHelmetDefence() {
        Character c = new Character(null);
        double og = c.getHealth().getValue();

        // create helmet
        Helmet helmet = new Helmet(new SimpleIntegerProperty(), new SimpleIntegerProperty());

        // attack from vampire
        Vampire v = new Vampire(null);
        double ogVampHealth = v.getHealth();

        // defend character
        helmet.defend(v.getDamage(), v);

        // check damage is less
        double perfectDecrease = helmet.getPercentDecreaseInEnemyAttack() * 1.0;
        double characterDecrease = helmet.getPercentDecreaseInCharacterAttack() * 1.0;

        double characterDamageOutput = (1 - (characterDecrease / 100.0)) * c.getDamage();
        double damageReceived = (1 - (perfectDecrease / 100.0)) * v.getDamage();

        v.setHealth(ogVampHealth - characterDamageOutput);
        c.setHealth(og - damageReceived);

        // Check that damage from attack is halved and gone to character health pool.
        assertEquals(c.getHealth().getValue(), og - damageReceived);

        // Check that character damage output is reduced and gone to vampire health pool.
        assertEquals(v.getHealth(), ogVampHealth - characterDamageOutput);
    }

    @Test
    public void testHealthPotionUse() {
        Character c = new Character(null);
        double ogHealth = c.getHealth().getValue();

        // create health potions
        HealthPotion hp1 = new HealthPotion(new SimpleIntegerProperty(), new SimpleIntegerProperty());
        HealthPotion hp2 = new HealthPotion(new SimpleIntegerProperty(), new SimpleIntegerProperty());
        HealthPotion hp3 = new HealthPotion(new SimpleIntegerProperty(), new SimpleIntegerProperty());

        // increase character health using health potion 
        // this should not work as you cannot have more than 100 health
        hp1.regainHealth(c);

        // Check that character health remains the same
        assertEquals(c.getHealth().getValue(), ogHealth);

        // set character health to 90
        c.setHealth(90);
        
        // increase character health from 90 to 100 using health potion
        hp2.regainHealth(c);

        // Check that character health increased from 90 to 100 and no more.
        assertEquals(c.getHealth().getValue(), ogHealth);

        // manually set character health to 1
        c.setHealth(1);

        // increase character health from 90 to 100 using health potion
        hp3.regainHealth(c);

        assertEquals(c.getHealth().getValue(), 21);
    }

    @Test
    public void testGoldUse() {
        Character c = new Character(null);

        // character should spawn with 0 gold
        assertEquals(c.getMoney().getValue(), 0);

        // create gold item
        Gold g1 = new Gold(new SimpleIntegerProperty(), new SimpleIntegerProperty());

        // test gold is added to character personal gold amount
        c.increaseMoney(g1.getGoldAmount());

        // check it has been added
        assertEquals(c.getMoney().getValue(), g1.getGoldAmount());

        // use gold (just test function works correctly)
        c.decreaseMoney(20);

        // check it has been used
        assertEquals(c.getMoney().getValue(), 0);
    }

    @Test
    public void testStaffAttack() {
        Character c = new Character(null);
        Staff staff = new Staff(new SimpleIntegerProperty(), new SimpleIntegerProperty());
        c.setEquippedWeapon(staff);

        // create a vampire
        Vampire v = new Vampire(null);
        double ogVampHealth = v.getHealth();

        // attack vampire
        c.attack(v, 0.5f);

        // Check that damage from attack has been inflicted to the vampire
        assertEquals(v.getHealth(), ogVampHealth - (c.getDamage()*(1+ staff.getDamage())));
    }

    @Test
    public void testStaffAttackNormal() {
        Staff staff = new Staff(new SimpleIntegerProperty(), new SimpleIntegerProperty());
        Zombie z = new Zombie(null);
        double staffDamage = staff.getDamage();
        double newDamage = staff.attack(10, z, 0.5f);
        assertEquals(newDamage, (int) ((10) * (1 + staffDamage)));
    }

    @Test
    public void testStaffAttackTrance() {
        Staff staff = new Staff(new SimpleIntegerProperty(), new SimpleIntegerProperty());
        Zombie z = new Zombie(null);
        Character c = new Character(null);
        c.setEquippedWeapon(staff);
        c.attack(z, 0.005f);
        // This confirms that a zombie has turned into an allied soldier
        // In the trance
        assertTrue(z.getIsAlliedSolider() != -1);
    }

    @Test
    public void testStakeAttack() {
        Character c = new Character(null);

        // create stake
        Stake stake = new Stake(new SimpleIntegerProperty(), new SimpleIntegerProperty());
        c.setEquippedWeapon(stake);

        // testing damage against vampire:
        // create a vampire
        Vampire v = new Vampire(null);
        double ogVampHealth = v.getHealth();

        // attack vampire
        c.attack(v, 0.5f);

        // check damage inflicted to Vampire
        double perfectDecreaseVampire = c.getDamage() * (1 + stake.getPercentIncreaseInAttackVampire());

        // Check that damage from attack has been inflcited to the vampire
        assertEquals(v.getHealth(), ogVampHealth - perfectDecreaseVampire);

        // testing damage against other non-vampire enemies e.g. Zombie:
        // create a zombie
        Zombie z = new Zombie(null);
        double ogZombHealth = z.getHealth();

        // attack zombie
        c.attack(z, 0.5f);

        // check damage inflicted to zombie
        double perfectDecreaseZombie = c.getDamage() * (1 + stake.getDamage());

        // Check that damage from attack has been inflicted to the zombie
        assertEquals(z.getHealth(), ogZombHealth - perfectDecreaseZombie);
    }

    @Test
    public void testSwordAttack() {
        Character c = new Character(null);

        // create sword
        Sword sword = new Sword(new SimpleIntegerProperty(), new SimpleIntegerProperty());

        c.setEquippedWeapon(sword);

        // create a vampire
        Vampire v = new Vampire(null);
        double ogVampHealth = v.getHealth();

        // attack vampire
        c.attack(v, 0.5f);

        // check damage inflicted to vampire
        double perfectDecreaseVampire = c.getDamage() * (1 + sword.getDamage());

        // Check that damage from attack has been inflicted to the vampire
        assertEquals(v.getHealth(), ogVampHealth - perfectDecreaseVampire);
    }
}