package unsw.loopmania.Enemies.Bosses;

import unsw.loopmania.BasicEnemy;
import unsw.loopmania.PathPosition;
import unsw.loopmania.Enemies.Boss;
import unsw.loopmania.Enemies.Slug;
import unsw.loopmania.Items.*;
import unsw.loopmania.RareItems.RareItem;
import unsw.loopmania.RareItems.RareItemAction;

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
public class Doggie extends BasicEnemy implements Boss {

    static final private double CHANCE_STUN = 0.2;
    static final private int DOGGIE_HEALTH = 100;
    static final private int DOGGIE_DAMAGE = 10;
    static final private int DOGGIE_EXP = 1000;
    static final private int DOGGIE_GOLD_AWARDED = 150;
    static final private int DOGGIE_COIN_AWARDED = 100;

    /**
     * Used in animation
     */
    static final private int SPRITE_SHEET_OFFSET_X = 0;

    public Doggie(PathPosition position) {
        super(position, DOGGIE_HEALTH, Slug.BATTLE_RADIUS, Slug.SUPPORT_RADIUS, 
            DOGGIE_DAMAGE, DOGGIE_EXP, DOGGIE_GOLD_AWARDED, 
            "src/images/doggierightwalk.png", SPRITE_SHEET_OFFSET_X);
    }

    @Override
    public void move(){
        this.moveUpPath();
    }

    @Override
    public void attackCharacter(Character c, float floatChance, int intChance) {
        double chanceStun = CHANCE_STUN;
        ArrayList<Item> gears = c.getProtectiveGear();
        double damageToInflict = this.getDamage();
        boolean charStunned = c.getSecondsStunned() > 0 ? true : false;
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
            }
        }
        // cannot get a critical bite if already in one
        if (!charStunned && floatChance <= chanceStun) {
            c.setSecondsStunned(intChance);
        }
        c.setHealth(c.getHealth().getValue() - damageToInflict);
    }

    public Card giveCardRewardForDefeat(Pair<Integer, Integer> cardSpot) {
        return null;
    }

    public Item giveEquipmentRewardForDefeat(Pair<Integer, Integer> itemSpot, int chance) {
        return null;
    }

    public void giveCharacterStatsForDefeat(Character c) {
        c.increaseDoggieCoin(DOGGIE_COIN_AWARDED);
    }

    @Override
    public void updateAnimation(List<Pair<Integer, Integer>> orderedPath, Boolean inActiveBattle) {
        // find out what coords come next
        int indexPosition = getNextIndexPositionClock(orderedPath, false);
        updateImageView(orderedPath, inActiveBattle, indexPosition, "doggie");
    }

    
}