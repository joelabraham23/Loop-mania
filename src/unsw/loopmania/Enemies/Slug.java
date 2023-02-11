package unsw.loopmania.Enemies;

import java.util.Random;
import java.util.List;

import org.javatuples.Pair;

import unsw.loopmania.BasicEnemy;
import unsw.loopmania.Card;
import unsw.loopmania.Character;
import unsw.loopmania.Item;
import unsw.loopmania.PathPosition;

/**
 * A standard enemy type. Low health and low damage. 
 * The battle radius is the same as the support radius for a slug.
 */
public class Slug extends BasicEnemy {

    static final public int BATTLE_RADIUS = 1;
    static final public int SUPPORT_RADIUS = 1;
    static final public int SLUG_DAMAGE = 2;
    static final public int SLUG_EXP = 100;
    static final public int SLUG_GOLD_AWARDED = 20;
    static final public int SLUG_HEALTH = 20;

    static final private int SPRITE_SHEET_OFFSET_X = 0;

    // default choice
    private int directionChoice = -1;
    
    public Slug(PathPosition position) {
        super(position, SLUG_HEALTH, BATTLE_RADIUS, SUPPORT_RADIUS, 
            SLUG_DAMAGE, SLUG_EXP, SLUG_GOLD_AWARDED, "src/images/slugrightwalk.png", SPRITE_SHEET_OFFSET_X);
    }

    public void move() {
        if (this.directionChoice == -1) {
            // this only happens if animate doesn't run first e.g. in testing
            this.directionChoice = (new Random()).nextInt(2);
        }
        if (this.directionChoice == 0) {moveUpPath();}
        else if (this.directionChoice == 1){ moveDownPath();}
    }

    public Card giveCardRewardForDefeat(Pair<Integer, Integer> cardSpot) {
        int chance = new Random().nextInt(100);
        return this.generateRandomCard(chance, cardSpot.getValue0());
    }

    public Item giveEquipmentRewardForDefeat(Pair<Integer, Integer> itemSpot, int chance) {
        return null;
    }

    public void giveCharacterStatsForDefeat(Character c) {
        c.increaseExperience(100);
        c.increaseMoney(20);
    }

    @Override
    public void updateAnimation(List<Pair<Integer, Integer>> orderedPath, Boolean inActiveBattle) {
        this.directionChoice = (new Random()).nextInt(2);
        // get animation
        int indexPosition = getNextIndexPositionBothDirections(orderedPath);
        updateImageView(orderedPath, inActiveBattle, indexPosition, "slug");
    }
    
}