package unsw.loopmania;

import java.util.ArrayList;

import java.util.List;

import org.javatuples.Pair;

import javafx.beans.property.SimpleIntegerProperty;
import unsw.loopmania.Cards.*;
import unsw.loopmania.Frontend.MovingAnimatedEntity;
import unsw.loopmania.Items.ProtectiveGear;
import unsw.loopmania.RareItems.RareItem;
import unsw.loopmania.RareItems.RareItemAction;

/**
 * a basic form of enemy in the world
 */
public abstract class BasicEnemy extends MovingAnimatedEntity implements AlliedSoldierCreator {

    /**
     * experience given to character when this enemy dies
     */
    private int experienceAwarded = 0;
    /**
     * gold given to character when this enemy dies
     */
    private int goldAwarded = 0;
    /**
     * Radius from which enemy will begin attacking character
     */
    private int battleRadius;
    /**
     * Radius within which other characters will join battle
     */
    private int supportRadius;
    /**
     * damage inflict to character/soldier every attack
     */
    private int damage;
    /**
     * full health of enemy
     */
    private double health;

    /**
     * if enemy is being transformed into an alliedsoldier
     * will be set to number of iterations it will remain
     * an allied soldier for
     */
    private int isAlliedSolider = -1;
    static final private int SPRITE_SHEET_COLUMNS = 3;
    static final private int SPRITE_SHEET_COUNT = 3;
    
    
    public BasicEnemy(PathPosition position, double health, int battleRadius, int supportRadius, int damage, int experienceAwarded, int goldAwarded, 
                      String entityImage, int spriteSheetOffsetX) {
        super(position, entityImage, spriteSheetOffsetX, SPRITE_SHEET_COLUMNS, SPRITE_SHEET_COUNT);
        this.health = health;
        this.battleRadius = battleRadius;
        this.supportRadius = supportRadius;
        this.damage = damage;
        this.experienceAwarded = experienceAwarded;
        this.goldAwarded = goldAwarded;
    }

    

    /**
     * move the enemy back and forward randomly
     * will be overridden in some subclasses
     */
    public abstract void move();

    

    /**
     * basic implementation of character attack, their
     * health is reduced by enemy's damage
     * @param c main character
     * @param floatChance determines critical bite for vampire
     * @param intChance determines length of critical bite for zombie
     */
    public void attackCharacter(Character c, float floatChance, int intChance) {
        // origianlly, damage is equal to enemy's damage
        double damageToInflict = damage;
        ArrayList<Item> gears = c.getProtectiveGear();
        if (gears != null) {
            // reduce the damage with protective hear
            for (Item item: gears) {
                // check if gear is a rare item
                if (item instanceof RareItem) {
                    RareItem rareItem = (RareItem) item;
                    if (rareItem.isShield()) {
                        // use doMagic to get value to remove
                        damageToInflict = rareItem.doMagic(damageToInflict, c, this, 0.0f, RareItemAction.DEFEND);
                    }
                } else {
                    ProtectiveGear gear = (ProtectiveGear) item;
                    damageToInflict = gear.defend(damageToInflict, this);
                }
            }
        }

        // inflict damage
        c.setHealth(c.getHealth().getValue() - damageToInflict);
    }

    public AlliedSoldier produceAlliedSolider(Character c, List<Pair<Integer, Integer>> orderedPath, int numberOfAlliedSoldiers) {
        // if -1 then not converted, if greater than 0 then needs conversion
        if (isAlliedSolider > 0 && numberOfAlliedSoldiers < 4) {
            // want soldier to be one step behind character
            int indexPosition = orderedPath.indexOf(new Pair<Integer, Integer>(c.getX(), c.getY()));
            // inclusive start and exclusive end of range of positions not allowed
            if (indexPosition == 0) {
                // need to go backwards
                indexPosition = orderedPath.size();
            }
            int pos = (indexPosition - 1)%orderedPath.size();
            // choose random choice
            Pair<Integer, Integer> spawnPosition = orderedPath.get(pos);
            int indexInPath = orderedPath.indexOf(spawnPosition);
            
            PathPosition soldierPath = new PathPosition(indexInPath, orderedPath);
            AlliedSoldier newSoldier = new AlliedSoldier(soldierPath, this);
            newSoldier.setIsEnemy(3);
            // move one step in reverse so that not on same block as character
            return newSoldier;
        }
        return null;
    }

    /**
     * increase character's money + experience and
     * return an item as reward to character for defeating
     * the enemy
     * @param c character of game
     */
    public abstract Card giveCardRewardForDefeat(Pair<Integer, Integer> cardSpot);

    public abstract Item giveEquipmentRewardForDefeat(Pair<Integer, Integer> itemSpot, int chance);

    public abstract void giveCharacterStatsForDefeat(Character c);

    /**
     * Generate a random card based on chance
     * @param chance used to decide what item will be generated
     * @return random card generated
     */
    public Card generateRandomCard(int chance, int cardX) {
        SimpleIntegerProperty x = new SimpleIntegerProperty(cardX);
        SimpleIntegerProperty y = new SimpleIntegerProperty(0);
        if (chance <= 10) {
            return new VampireCastleCard(x, y);
        } else if (chance <= 25) {
            return new VillageCard(x, y);
        } else if (chance <= 30) {
            return new TrapCard(x, y);
        } else if (chance <= 55) {
            return new CampfireCard(x, y);
        } else if (chance <= 70) {
            return new TowerCard(x, y);
        } else if (chance <= 90) {
            return new ZombiePitCard(x, y);
        } else {
            return new BarracksCard(x, y);
        }
    }

    /**
     * basic implementation of attack, their
     * health is reduced by enemy's damage
     * @param soldier an allied soldier in game
     * @param chance chance of a critical bite
     */
    public void attackAlliedSolider(AlliedSoldier soldier, float chance) {
        soldier.setHealth(soldier.getHealth() - this.damage);
    }

    public void setIsAlliedSolider(int duration) {
        this.isAlliedSolider = duration;
    }

    public int getIsAlliedSolider() {
        return isAlliedSolider;
    }

    public int getExperienceAwarded() {
        return experienceAwarded;
    }
    public void setExperienceAwarded(int experienceAwarded) {
        this.experienceAwarded = experienceAwarded;
    }
    public int getGoldAwarded() {
        return goldAwarded;
    }

    public double getHealth() {
        return health;
    }
    public void setHealth(double health) {
        this.health = health;
    }

    public int getBattleRadius() {
        return battleRadius;
    }
    public int getSupportRadius() {
        return supportRadius;
    }

    public int getDamage() {
        return damage;
    }

}
