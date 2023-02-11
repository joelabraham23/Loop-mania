package unsw.loopmania.Enemies;
import java.util.ArrayList;
import java.util.Random;
import java.util.List;

import org.javatuples.Pair;

import javafx.beans.property.SimpleIntegerProperty;
import unsw.loopmania.*;
import unsw.loopmania.Character;
import unsw.loopmania.Items.*;
import unsw.loopmania.RareItems.RareItem;
import unsw.loopmania.RareItems.RareItemAction;
/**
 * Vampires have high damage, are susceptible to the stake weapon, 
 * and run away from campfires. They have a higher battle radius than slugs, 
 * and an even higher support radius. A critical bite (which has a random chance of occurring)
 * from a vampire causes random additional damage with every vampire attack, for a random number of vampire attacks
 */
public class Vampire extends BasicEnemy {

    static final private double CHANCE_CRITICAL_BITE = 0.05;

    /**
     * when it gives character critical bite, will
     * inflict more damage than usual
     */
    static final private int EXTRA_DAMAGE_CRITICAL_BITE = 10;

    static final public int VAMPIRE_HEALTH = 60;
    static final public int VAMPIRE_BATTLE_RADIUS = 3;
    static final public int VAMPIRE_SUPPORT_RADIUS = 5;
    static final public int VAMPIRE_DAMAGE = 10;
    static final public int VAMPIRE_EXP = 500;
    static final public int VAMPIRE_GOLD_AWARDED = 100;

    /**
     * Used in animation
     */
    static final private int SPRITE_SHEET_OFFSET_X = 8;
  
    public Vampire(PathPosition position) {
        super(position, VAMPIRE_HEALTH, VAMPIRE_BATTLE_RADIUS, VAMPIRE_SUPPORT_RADIUS, 
            VAMPIRE_DAMAGE, VAMPIRE_EXP, VAMPIRE_GOLD_AWARDED, "src/images/vampirerightwalk.png", SPRITE_SHEET_OFFSET_X);
    }

    public double getChanceOfCriticalBite() {
        return CHANCE_CRITICAL_BITE;
    }

    @Override
    public void move(){
        continueInPath();
    }

    @Override
    public void updateAnimation(List<Pair<Integer, Integer>> orderedPath, Boolean inActiveBattle) {
        int indexPosition = getNextIndexPositionBothDirections(orderedPath);
        updateImageView(orderedPath, inActiveBattle, indexPosition, "vampire");
    }

    /**
     * allow for random chance of critical bite that will cause
     * vampire to have additional damage for x number of future attacks
     * i.e. their damage increases
     */
    @Override
    public void attackCharacter(Character c, float floatChance, int intChance) {
        
        double chanceBite = CHANCE_CRITICAL_BITE;
        Boolean inCriticalBite = c.getSecondsLeftInCriticalBite() > 0 ? true : false;
        ArrayList<Item> gears = c.getProtectiveGear();
        double damageToInflict = this.getDamage();

        // apply gear's affects
        if (gears != null) {
            for (Item item: gears) {
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
                // if it's a shield then chanceBite reduces
                if (item instanceof Shield || 
                    (item instanceof RareItem && ((RareItem) item).isShield())) {
                    chanceBite = CHANCE_CRITICAL_BITE * (1 - Shield.PCNT_DECREASE_CRITICAL_BITE);
                }
            }
        }
        // cannot get a critical bite if already in one
        if (!inCriticalBite && floatChance <= chanceBite) {
            // in likely event we do have a critical bite
            inCriticalBite = true;
            // set character's critical bite
            c.setSecondsLeftInCriticalBite(intChance);
        }

        if (inCriticalBite == true) {
            damageToInflict += EXTRA_DAMAGE_CRITICAL_BITE;
            c.setSecondsLeftInCriticalBite(c.getSecondsLeftInCriticalBite() - 1);
        } 
        c.setHealth(c.getHealth().getValue() - damageToInflict);
    }

    public Card giveCardRewardForDefeat(Pair<Integer, Integer> cardSpot) {
        int chance = new Random().nextInt(100);
        return this.generateRandomCard(chance, cardSpot.getValue0());
    }

    public Item giveEquipmentRewardForDefeat(Pair<Integer, Integer> itemSpot, int chance) {
        // vampires return staffs/shields
        SimpleIntegerProperty x = new SimpleIntegerProperty(itemSpot.getValue0());
        SimpleIntegerProperty y = new SimpleIntegerProperty(itemSpot.getValue1());
        if (chance == 0) {
            return new Staff(x, y);
        } else {
            return new Shield(x, y);
        }
    }

    public void giveCharacterStatsForDefeat(Character c) {
        c.increaseExperience(500);
        c.increaseMoney(100);
    }
}
