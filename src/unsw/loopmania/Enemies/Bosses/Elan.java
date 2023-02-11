package unsw.loopmania.Enemies.Bosses;

import unsw.loopmania.BasicEnemy;
import unsw.loopmania.PathPosition;
import unsw.loopmania.Enemies.Boss;
import unsw.loopmania.Enemies.Slug;

import java.util.ArrayList;
import java.util.List;

import org.javatuples.Pair;

import unsw.loopmania.*;
import unsw.loopmania.Character;

/**
 * An incredibly tough boss which, when appears, causes the price 
 * of DoggieCoin to increase drastically. Defeating this boss 
 * causes the price of DoggieCoin to plummet. Elan has the 
 * ability to heal other enemy NPCs. 
 * The battle and support radii are the same as for slug
 */
public class Elan extends BasicEnemy implements Boss {

    static final public int ELAN_HEALTH = 100;
    static final public int ELAN_DAMAGE = 10;
    static final public int ELAN_EXP = 1000;
    static final public int ELAN_GOLD_AWARDED = 150;
    static final public double CHANCE_TAX = 0.01f;

    /**
     * Used in animation
     */
    static final private int SPRITE_SHEET_OFFSET_X = 0;

    public Elan(PathPosition position) {
        super(position, ELAN_HEALTH, Slug.BATTLE_RADIUS, Slug.SUPPORT_RADIUS, ELAN_DAMAGE, 
            ELAN_EXP, ELAN_GOLD_AWARDED, "src/images/elanwalkright.png", SPRITE_SHEET_OFFSET_X);
    }

    @Override
    public void move(){
        this.moveUpPath();
    }

    /**
     * 10% of adding the ATO to the game so character has to pay taxes every time
     */
    @Override
    public void attackCharacter(Character c, float floatChance, int intChance) {
        
        c.setHealth(c.getHealth().getValue() - getDamage());
        if (floatChance < CHANCE_TAX && !c.getIsTaxPayer()) {
            c.setTaxPayer(true);
        }
    }

    public void healEnemies(ArrayList<BasicEnemy> enemies, float chance) {
        if (chance < 0.01f) {
            for (BasicEnemy e: enemies) {
                if (!e.equals(this)) {
                    e.setHealth(e.getHealth() + 5);
                }
            }
        }
    }

    public Card giveCardRewardForDefeat(Pair<Integer, Integer> cardSpot) {
        return null;
    }

    public Item giveEquipmentRewardForDefeat(Pair<Integer, Integer> itemSpot, int chance) {
        return null;
    }

    public void giveCharacterStatsForDefeat(Character c) {
        c.increaseExperience(ELAN_EXP);
        c.increaseMoney(ELAN_GOLD_AWARDED);
    }

    @Override
    public void updateAnimation(List<Pair<Integer, Integer>> orderedPath, Boolean inActiveBattle) {
        // find out what coords come next
        int indexPosition = getNextIndexPositionClock(orderedPath, false);
        updateImageView(orderedPath, inActiveBattle, indexPosition, "elan");

    }
    
}