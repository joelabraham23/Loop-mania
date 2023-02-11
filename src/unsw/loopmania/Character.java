package unsw.loopmania;

import java.util.ArrayList;
import java.util.List;
import org.javatuples.Pair;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import unsw.loopmania.Enemies.Boss;
import unsw.loopmania.Frontend.MovingAnimatedEntity;
import unsw.loopmania.Items.*;
import unsw.loopmania.RareItems.RareItem;
import unsw.loopmania.RareItems.RareItemAction;

/**
 * represents the main character in the backend of the game world
 */
public class Character extends MovingAnimatedEntity {

    /**
     * health of character at full
     */
    private DoubleProperty health;

    /**
     * bosses chracter has killed
     */
    private ArrayList<Boss> bossesKilled = new ArrayList<>();

    /**
     * character's max health
     */
    static final private double FULL_HEALTH = 100;

    /**
     * damage inflicted when it has no weapons
     */
    private int damage = 10;
    /**
     * character's gold origianlly 0
     */
    private IntegerProperty money = new SimpleIntegerProperty(0);

    /**
     * character's dogge coin originally 0
     */
    private IntegerProperty doggieCoins = new SimpleIntegerProperty(0);

    /**
     * character's weapon
     */
    private Item equippedWeapon = null;

    /**
     * character's protectives gears, can have one of each tye of gear
     */
    private ArrayList<Item> protectiveGears = new ArrayList<>();

    /**
     * when vampire attacks the character, if they are
     * have +ive seconds in critical bite they get more damage
     */
    private int secondsLeftInCriticalBite = 0;

    /**
     * seconds remaining for which character is stunned (can't attack)
     */
    private int secondsStunned = 0;
    /**
     * number of cycles of game completed by the charater
     */
    private IntegerProperty cyclesCompleted;


    /**
     * true if there is a tax office in game
     */
    private boolean isTaxPayer = false;

    /**
     * character's experience throughout game
     * will icnrease as completes cycles, collections cards
     * and kills enemies
     */
    private IntegerProperty experience;
    
    // magic numbers for animation
    static final private int SPRITE_SHEET_COLUMNS = 4;
    static final private int SPRITE_SHEET_COUNT = 6;
    static final private int SPRITE_SHEET_OFFSET_X = 0;
    

    public Character(PathPosition position) {
        super(position, "src/images/charrightwalk.png", SPRITE_SHEET_OFFSET_X, SPRITE_SHEET_COLUMNS, SPRITE_SHEET_COUNT);
        this.health = new SimpleDoubleProperty(FULL_HEALTH);
        this.experience = new SimpleIntegerProperty(0);
        this.cyclesCompleted = new SimpleIntegerProperty(0);
    }

    
    /**
     * checks if characer is equipped with a helmet
     * @return null if no helmet
     */
    public Helmet charHasHelmet() {
        for (Item gear: protectiveGears) {
            if (gear instanceof Helmet) {
                return (Helmet) gear;
            }
        }
        return null;
    }
    
    /**
     * checks if characer is equipped with armour
     * @return true if equipped else false
     */
    public Boolean charHasArmour() {
        for (Item gear: protectiveGears) {
            if (gear instanceof Armour) {
                return true;
            }
        }
        return false;
    }
    
    /**
     * checks if characer is equipped with a shield
     * @return true if equipped else false
     */
    public Boolean charHasShield() {
        for (Item gear: protectiveGears) {
            if (gear instanceof Shield) {
                return true;
            }
        }
        return false;
    }
    
    
    /**
     * when a character attacks an enemy,
     * should deduct 10 points from the enemy's
     * health if character is not armed. Otherwise, will
     * @param e enemy to attack
     * @param chanceTrance chance that character if they have a staff will cause trance
     * @param weapon if character has a weapon will increase damage
     */
    public void attack(BasicEnemy e, float chanceTrance) {
        if (secondsStunned > 0) {
            secondsStunned -= 1;
        } else {
            double damageToRemove = damage;
            Helmet helmet = charHasHelmet();
            if (helmet != null) {
                // then damage to remove is reduced 30%
                damageToRemove = damageToRemove * (1 - helmet.getPercentDecreaseInCharacterAttack());
            }
            Item weapon = equippedWeapon;
            if (weapon != null) {
                // check if weapon is a rare item
                if (weapon instanceof RareItem) {
                    RareItem rareItem = (RareItem) weapon;
                    if (rareItem.isSword()) {
                        // use doMagic to get value to remove
                        damageToRemove = rareItem.doMagic(damageToRemove, this, e, chanceTrance, RareItemAction.ATTACK);
                    }
                } else {
                    Weapon w = (Weapon) weapon;
                    damageToRemove = w.attack(damageToRemove, e, chanceTrance);
                }
            } 
            e.setHealth(e.getHealth() - damageToRemove);
        }
    }

    @Override
    public void updateAnimation(List<Pair<Integer, Integer>> orderedPath, Boolean inActiveBattle) {
        int indexPosition = getNextIndexPositionClock(orderedPath, true);
        updateImageView(orderedPath, inActiveBattle, indexPosition, "char");
    }
    /**
     * set character's equipped weapon
     * @param equippedWeapon
     * @return
     */
    public Item setEquippedWeapon(Item equippedWeapon) {
        Item currWeapon = (Item) this.getEquippedWeapon();
        this.equippedWeapon = equippedWeapon;
        return currWeapon;
    }

    /**
     * set character's shield
     * @param newShield shield to set
     * @return if there was an old shield that get's ejected
     */
    public Item setShield(Item newShield) {
        Item gearEjected = null;
        for (Item gear: this.protectiveGears) {
            // check if any gears are same type
            if (gear instanceof Shield || 
                (gear instanceof RareItem && ((RareItem) gear).isShield())) {
                // remove gear from character's equipped and add item
                this.protectiveGears.remove(gear);
                gearEjected = gear;
                break;
            }
        }
        this.addGear(newShield);
        return gearEjected;
    }

    /**
     * set character's armour
     * @param newArmour armour to set
     * @return if there was an old armour that get's ejected
     */
    public Item setArmour(Item newArmour) {
        Item itemEjected = null;
        for (Item gear: this.protectiveGears) {
            // check if any gears are same type
            if (gear instanceof Armour && newArmour instanceof Armour) {
                // remove gear from character's equipped and add item
                this.protectiveGears.remove(gear);
                itemEjected = gear;
                break;
            }
        }
        this.addGear(newArmour);
        return itemEjected;
    }

    /**
     * set character's helemt
     * @param newHelmet helmet to set
     * @return if there was an old helmet that get's ejected
     */
    public Item setHelmet(Item newHelmet) {
        Item itemEjected = null;
        for (Item gear: this.protectiveGears) {
            // check if any gears are same type
            if (gear instanceof Helmet && newHelmet instanceof Helmet) {
                // remove gear from character's equipped and add item
                this.protectiveGears.remove(gear);
                itemEjected = gear;
                break;
            }
        }
        this.addGear(newHelmet);
        return itemEjected;
    }



    /**
     * remove any equipment that has lived it's life (ie durability)
     * to make game more difficult and itneresting
     */
    public void checkItemsToRemove() {
        // check character's weapon
        if (equippedWeapon != null) {
            if (equippedWeapon instanceof RareItem) {
                // check rounds left for rareItem
                RareItem rareItem = (RareItem) equippedWeapon;
                if (rareItem.endOfLife()) {
                    // so if no more left then destroy and remove it
                    equippedWeapon.addToGrid();
                    equippedWeapon.destroy();
                    equippedWeapon = null;
                }
            } else {
                EquippedItem weapon = (EquippedItem) equippedWeapon;
                if (weapon.getroundsLeft() <= 0) {
                    // remove weaopn
                    equippedWeapon = null;
                    weapon.addToGrid();
                    weapon.destroy();
                }
            }
        }
        if (protectiveGears != null) {
            ArrayList<Item> charGears = new ArrayList<>(protectiveGears);
            for (Item item: charGears) {
                if (item instanceof RareItem) {
                    RareItem rareItem = (RareItem) item;
                    if (rareItem.endOfLife()) {
                        // so if no more left then destroy and remove it
                        removeGear(item);
                        item.addToGrid();
                        item.destroy();
                    }
                } else {
                    EquippedItem gear = (EquippedItem) item;
                    if (gear.getroundsLeft() == 0) {
                        // remove weaopn
                        removeGear(item);
                        item.addToGrid();
                        gear.destroy();
                    }
                }
            }
        }
    }
    
    /**
     * 
     * GETTERS AND SETTERS
     */
    public void setSecondsLeftInCriticalBite(int secondsLeftInCriticalBite) {
        this.secondsLeftInCriticalBite = secondsLeftInCriticalBite;
    }

    public void addBossToKilled(Boss b) {
        bossesKilled.add(b);
    }

    public ArrayList<Boss> getBossesKilled() {
        return bossesKilled;
    }

    public int getSecondsLeftInCriticalBite() {
        return secondsLeftInCriticalBite;
    }

    public void setHealth(double newHealth) {
        this.health.set(newHealth);

    }
    public DoubleProperty getHealth() {
        return health;
    }

    public static double getFullhealth() {
        return FULL_HEALTH;
    }

    public Item getEquippedWeapon() {
        return equippedWeapon;
    }

    public void setTaxPayer(boolean isTaxPayer) {
        this.isTaxPayer = isTaxPayer;
    }
    public boolean getIsTaxPayer() {
        return isTaxPayer;
    }

    public ArrayList<Item> getProtectiveGear() {
        return protectiveGears;
    }
    public void setProtectiveGear(ArrayList<Item> protectiveGear) {
        this.protectiveGears = protectiveGear;
    }

    public IntegerProperty getCyclesCompleted() {
        return cyclesCompleted;
    }
    
    public void increaseCyclesCompleted() {
        this.cyclesCompleted.set(cyclesCompleted.getValue() + 1);
    }

    public void addGear(Item newGear) {
        this.protectiveGears.add(newGear);
    }

    public void removeGear(Item newGear) {
        this.protectiveGears.remove(newGear);
    }

    public void setDamage(int damage) {
        this.damage = damage;
    }

    public int getDamage() {
        return damage;
    }
    public IntegerProperty getMoney() {
        return money;
    }

    public void setMoney(int money) {
        this.money.set(money);
    }

    public void setSecondsStunned(int secondsStunned) {
        this.secondsStunned = secondsStunned;
    }
    public int getSecondsStunned() {
        return secondsStunned;
    }

    public void increaseMoney(int amount) {
        this.money.set(getMoney().getValue() + amount);
    }

    public void increaseDoggieCoin(int amount) {
        this.doggieCoins.set(doggieCoins.getValue() + amount);
    }

    public void decreaseMoney(int amount) {
        this.money.set(getMoney().getValue() - amount);
        if (this.money.getValue() < 0) {
            // dont get negative
            this.money.set(0);
        }
    }

    public IntegerProperty getExperience() {
        return this.experience;
    }

    public IntegerProperty getDoggieCoins() {
        return doggieCoins;
    }

    public void increaseExperience(int amount) {
        this.experience.set(getExperience().getValue() + amount);
    }

    public void setExperience(int amount) {
        this.experience.set(amount);
    }

    /**
     * reset character's health to 100
     */
    public void provideFullHealth() {
        this.health.set(FULL_HEALTH);
    }

}
