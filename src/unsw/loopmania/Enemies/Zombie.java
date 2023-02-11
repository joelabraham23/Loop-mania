package unsw.loopmania.Enemies;

import java.util.List;
import java.util.Random;

import org.javatuples.Pair;

import javafx.beans.property.SimpleIntegerProperty;
import unsw.loopmania.AlliedSoldier;
import unsw.loopmania.BasicEnemy;
import unsw.loopmania.Card;
import unsw.loopmania.Character;
import unsw.loopmania.Item;
import unsw.loopmania.PathPosition;
import unsw.loopmania.Items.Helmet;
import unsw.loopmania.Items.Stake;

/**
 * Zombies have low health, moderate damage, and are slower 
 * compared to other enemies. A critical bite from a 
 * zombie against an allied soldier (which has a random 
 * chance of occurring) will transform the allied soldier 
 * into a zombie, which will then proceed to fight against 
 * the Character until it is killed. Zombies have a higher battle radius than slugs
 */
public class Zombie extends BasicEnemy {

    /**
     * allow zombies to move at half the speed
     */
    private Boolean wasMovedLastRound = false;

    static final private double CHANCE_CRITICAL_BITE = 0.1;
    static final public int ZOMBIE_HEALTH = 20;
    static final public int ZOMBIE_BATTLE_RADIUS = 3;
    static final public int ZOMBIE_SUPPORT_RADIUS = 3;
    static final public int ZOMBIE_DAMAGE = 5;
    static final public int ZOMBIE_EXP = 250;
    static final public int ZOMBIE_GOLD_AWARDED = 50;
    
    /**
     * Used in animation
     */
    static final private int SPRITE_SHEET_OFFSET_X = 8;

    public Zombie(PathPosition position) {
        super(position, ZOMBIE_HEALTH, ZOMBIE_BATTLE_RADIUS, ZOMBIE_SUPPORT_RADIUS, 
            ZOMBIE_DAMAGE, ZOMBIE_EXP, ZOMBIE_GOLD_AWARDED, "src/images/zombierightwalk.png", SPRITE_SHEET_OFFSET_X);        
    }

    @Override
    public void updateAnimation(List<Pair<Integer, Integer>> orderedPath, Boolean inActiveBattle) {
        if (wasMovedLastRound) { return; } // dont change directions if not going to move
        int indexPosition = getNextIndexPositionClock(orderedPath, false);
        updateImageView(orderedPath, inActiveBattle, indexPosition, "zombie");
    }

    public Boolean getWasMovedLastRound() {
        return wasMovedLastRound;
    }
    public void setWasMovedLastRound(Boolean wasMovedLastRound) {
        this.wasMovedLastRound = wasMovedLastRound;
    }
    
    /**
     * zombies move at half speed by only moving
     * every second tick
     */
    @Override
    public void move() {
        if (!wasMovedLastRound) {
            moveUpPath();
            wasMovedLastRound = true;
        } else {
            wasMovedLastRound = false;
        }
    }
    
    /**
     * allow for random chance of critical bite that will turn
     * allied soldier into a zombie
     */
    @Override
    public void attackAlliedSolider(AlliedSoldier soldier, float chance) {
        // less than 10% chance
        if (chance <= CHANCE_CRITICAL_BITE) {
            performCriticalbite(soldier);
        } else {
            soldier.setHealth(soldier.getHealth() - this.getDamage());  
        }
    }

    /**
     * will turn allied soldier into a zombie forever
     * using the allied soldier's isZombie paramter
     */
    public void performCriticalbite(AlliedSoldier soldier) {
        soldier.setIsZombie(true);
    }


    public Card giveCardRewardForDefeat(Pair<Integer, Integer> cardSpot) {
        int chance = new Random().nextInt(100);
        return this.generateRandomCard(chance, cardSpot.getValue0());
    }

    public Item giveEquipmentRewardForDefeat(Pair<Integer, Integer> itemSpot, int chance) {
        // vampires return helmet/stake
        SimpleIntegerProperty x = new SimpleIntegerProperty(itemSpot.getValue0());
        SimpleIntegerProperty y = new SimpleIntegerProperty(itemSpot.getValue1());
        if (chance == 0) {
            return new Stake(x, y);
        } else {
            return new Helmet(x, y);
        }
    }

    public void giveCharacterStatsForDefeat(Character c) {
        c.increaseExperience(250);
        c.increaseMoney(50);
    }

}