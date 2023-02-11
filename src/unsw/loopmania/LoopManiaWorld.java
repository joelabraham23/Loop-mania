package unsw.loopmania;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import org.javatuples.Pair;

import javafx.beans.property.*;
import unsw.loopmania.Buildings.*;
import unsw.loopmania.Enemies.Boss;
import unsw.loopmania.Enemies.Bosses.Elan;
import unsw.loopmania.GameModes.State;
import unsw.loopmania.Goals.CompositeGoal;
import unsw.loopmania.Items.*;
import unsw.loopmania.RareItems.RareItem;
import unsw.loopmania.RareItems.RareItemAction;


/**
 * A backend world.
 *
 * A world can contain many entities, each occupy a square. More than one
 * entity can occupy the same square.
 */
public class LoopManiaWorld implements LoopManiaEnemies, AlliedSoldierSubject {

    public static final int unequippedInventoryWidth = 4;
    public static final int unequippedInventoryHeight = 4;

    /**
     * will be set to true if all goals reached (game won)
     */
    public BooleanProperty gameWon = new SimpleBooleanProperty(false);

    /**
     * set to true if game is lost (health <= 0)
     */
    public BooleanProperty gameLost = new SimpleBooleanProperty(false);


    /**
     * width of the world in GridPane cells
     */
    private int width;

    /**
     * height of the world in GridPane cells
     */
    private int height;

    /**
     * character of the game
     */
    private Character character;

    /**
     * allied soldiers that help character fight
     */
    private ArrayList<AlliedSoldier> alliedSoldiers;

    /**
     * enemies that character must fight if come close
     */
    private ArrayList<BasicEnemy> enemies;

    /**
     * cards that can be used to create buildings
     */
    private ArrayList<Card> cardEntities;

    /**
     * Items in unequipped inventory, character
     * does not directly benefit from them
     * except for health potions
     */
    private ArrayList<Item> unequippedInventoryItems;

    /**
     * list of rare items that character has not equipped
     */
    private ArrayList<RareItem> rareItems;

    /**
     * items on the ground for character to pickup
     */
    private ArrayList<Item> groundItems;

    /**
     * all buildings in teh game
     */
    private ArrayList<Building> buildingEntities;

    /**
     * list of entities that can create enemies
     */
    private ArrayList<EnemyCreator> enemyCreators;

    /**
     * list of entities that can create allied soldiers
     */
    private ArrayList<AlliedSoldierCreator> soldierCreators;

    /**
     * random number generation
     */
    private Random rand = new Random();

    /**
     * list of x,y coordinate pairs in the order by which moving entities traverse them
     */
    private List<Pair<Integer, Integer>> orderedPath;

    /**
     * Counts occurances of inventory changes
     * Will always start game with empty inventory
     */
    private IntegerProperty timesInventoryChanged = new SimpleIntegerProperty(0);

    /**
     * Counts number of allied soldiers
     * Will always start the game with 0
     */
    private IntegerProperty numberOfAlliedSoldiers = new SimpleIntegerProperty(0);

    /**
     * goal that must be achieved to win the game
     */
    private CompositeGoal goals;

    /**
     * list of rare items that can be spawned in
     * game
     */
    private ArrayList<String> rareItemsAllowed;

    /**
     * creates slugs
     */
    private SlugCreator slugCreator;

    /**
     * game mode: confusion/standard/survival/berserker
     */
    State state;

    /**
     * value of doggie coin at a point in time
     */
    public IntegerProperty doggieValue;

    public LoopManiaWorld(int width, int height, List<Pair<Integer, Integer>> orderedPath, CompositeGoal goals, ArrayList<String> allRareItems, State gameMode) {
        
        this.width = width;
        this.height = height;
        
        character = new Character(new PathPosition(0, Arrays.asList(new Pair<>(1, 1), new Pair<>(width, height))));
        enemies = new ArrayList<>();
        cardEntities = new ArrayList<>();
        unequippedInventoryItems = new ArrayList<>();
        alliedSoldiers = new ArrayList<>();
        this.orderedPath = orderedPath;
        buildingEntities = new ArrayList<>();
        groundItems = new ArrayList<>();
        enemyCreators = new ArrayList<>();
        soldierCreators = new ArrayList<>();
        this.goals = goals;
        this.rareItemsAllowed = allRareItems;
        this.rareItems = new ArrayList<>();
        this.state = gameMode;
        // starting value = 0
        this.doggieValue = new SimpleIntegerProperty(0);
        this.slugCreator = new SlugCreator();
        // add slugCreator to enemyCreators
        enemyCreators.add(slugCreator);
    }


    /**
     * add a new enemy, must add it to soldierCreator
     * as well so it can spawn soldiers
     * @param e new enemy to add
     */
    public void addEnemy(BasicEnemy e) {
        enemies.add(e);
        attachSoldierCreator(e);
    }

    /**
     * remove an enemy from the world
     * @param e new enemy to remove
     */
    public void removeEnemy(BasicEnemy e) {
        enemies.remove(e);
        detachSoldierCreator(e);
        if (e instanceof Elan) {
            doggieValue.setValue(doggieValue.getValue() / 1000);
        }
        e.destroy();
    }


    /**
     * remove an allied soldier
     * @param soldier to remove
     */
    public void removeAlliedSoldier(AlliedSoldier soldier) {
        soldier.destroy();   
        detachEnemyCreator(soldier);
        alliedSoldiers.remove(soldier);
        this.numberOfAlliedSoldiers.setValue(numberOfAlliedSoldiers.getValue() - 1); 
    }

    /**
     * add a new soldier, must add it to enemyCreator
     * as well so it can spawn enemies
     * @param e new enemy to add
     */
    public void addAlliedSoldier(AlliedSoldier soldier) {
        alliedSoldiers.add(soldier);
        attachEnemyCreator(soldier);
        this.numberOfAlliedSoldiers.setValue(numberOfAlliedSoldiers.getValue() + 1); 
    }


    /**
     * once an enemy is defeated, character gets rewards
     * based on enemy type
     * @param enemies list of defeated enemies
     * @return list of items (cards + items) character gets
     */
    public ArrayList<Item> reactToEnemyDefeat(ArrayList<BasicEnemy> enemies) {
        ArrayList<Item> rewards = new ArrayList<>();
        for (BasicEnemy enemy: enemies) {

            Pair<Integer, Integer> equipmentSpot = getFirstAvailableSlotForItem();
            // add new item + set its coordinates
            Item newEquipment = enemy.giveEquipmentRewardForDefeat(equipmentSpot, rand.nextInt(2));
            if (newEquipment != null) rewards.add(newEquipment);
            addToUnequipped(newEquipment);
            
            // card cooridnates
            int cardX = cardEntities.size();
            int cardY = 0;
            Card newCard = enemy.giveCardRewardForDefeat(new Pair<>(cardX, cardY));
            if (newCard != null) {
                rewards.add(newCard);
                Item itemFromBurntCards = addToCardEntities(newCard);
                if (itemFromBurntCards != null) {
                    rewards.add(itemFromBurntCards);
                }
            }

            // give character money/dogecoin/experience
            enemy.giveCharacterStatsForDefeat(character);
        }
        return rewards;
    }

    /**
     * public function to add cards to the world
     * if too many cards, will burn oldest card and return
     * the item from the burn
     */
    public Item addToCardEntities(Card entity) {
        Item result = null;
        if (cardEntities.size() >= getWidth()){
            // burn oldest card and give character reward (gold)
            Item newItem = burnOldestCard();
            result = newItem;
            // set new value for entity
            entity.setX(new SimpleIntegerProperty(cardEntities.size()));
        }
        cardEntities.add(entity);
        return result;
    }

    /**
     * remove a particular card
     * @param card the card to be removed
     * @return the card removed
     */
    private Card removeCard(Card c){
        int x = c.getX();
        c.destroy();
        cardEntities.remove(c);
        shiftCardsDownFromXCoordinate(x);
        return c;
    }

    /**
     * Will change the current price of dogge coin
     * if elan exists then price can only go up
     */
    public void changeDoggiePrice(float chance) {
        boolean elanExists = false;
        for (BasicEnemy e: enemies) {
            if (e instanceof Elan) {
                elanExists = true;
            }
        }
        if (elanExists || chance > 0.75) {
            // go up
            doggieValue.setValue(doggieValue.getValue() + rand.nextInt(100));
        } else if (chance > 0.5) {
            doggieValue.setValue(doggieValue.getValue() - rand.nextInt(100));
            if (doggieValue.getValue() <= 0) {
                doggieValue.setValue(0);
            }
        }
    }

    public void updateAnimations(Boolean inActiveBattle) {
        character.updateAnimation(orderedPath, inActiveBattle);
        for (BasicEnemy e : enemies) {
            e.updateAnimation(orderedPath, inActiveBattle);
        }
        for (AlliedSoldier s : alliedSoldiers) {
            s.updateAnimation(orderedPath, inActiveBattle);
        }
    }


    /**
     * run moves which occur with every tick without needing to spawn anything immediately
     * @param inActiveBattle true if in an active battle
     */
    public void runTickMoves(Boolean inActiveBattle) {
        try {
            // update the animation for next tick
            updateAnimations(inActiveBattle);
        } catch (Exception e) {
            // this is needed because tests have no JavaFX
        } finally {
            // check if goal reached
            if (goals.checkGoalReached(character)) {
                gameWon.setValue(true);
            }
            // set price of doggie coin
            changeDoggiePrice(rand.nextFloat());
            // check for any equipped items that need reomving from character
            character.checkItemsToRemove();

            if (!inActiveBattle) {
                
                // check for any vampires scared of campfire
                checkForVampireNearCampfire();
                // move all moving entities
                character.moveDownPath();
                moveBasicEnemies();
                moveAlliedSoldiers();
                
                // check for village to increase health
                checkForVillage();

                // check for any tax to be paid
                checkForTaxOffice();
                
                // check for any enemies on traps
                checkForEnemiesOnTrap();
                if (character.getX() == 0 && character.getY() == 0) {
                    // this means a cycle has been completed
                    character.increaseCyclesCompleted();
                }
            }
        }
        

        
    }

    /**
     * remove an item from the unequipped inventory
     * @param item item to be removed
     */
    private void removeUnequippedInventoryItem(Entity item){
        unequippedInventoryItems.remove(item);
        timesInventoryChanged.setValue(timesInventoryChanged.getValue() + 1);
        item.destroy();
    }

     /**
     * remove an item by x,y coordinates
     * @param x x coordinate from 0 to width-1
     * @param y y coordinate from 0 to height-1
     */
    public void removeUnequippedInventoryItemByCoordinates(int x, int y){
        Entity item = getUnequippedInventoryItemEntityByCoordinates(x, y);
        removeUnequippedInventoryItem(item);
    }



    /**
     * return an unequipped inventory item by x and y coordinates
     * assumes that no 2 unequipped inventory items share x and y coordinates
     * @param x x index from 0 to width-1
     * @param y y index from 0 to height-1
     * @return unequipped inventory item at the input position
     */
    public Item getUnequippedInventoryItemEntityByCoordinates(int x, int y){
        for (Item e: unequippedInventoryItems){
            if ((e.getX() == x) && (e.getY() == y)){
                return e;
            }
        }
        return null;
    }

    /**
     * return an unequipped inventory item by x and y coordinates
     * assumes that no 2 unequipped inventory items share x and y coordinates
     * @param x x index from 0 to width-1
     * @param y y index from 0 to height-1
     * @return unequipped inventory item at the input position
     */
    public Item getRareInventoryItemEntityByCoordinates(int x, int y){
        for (RareItem e: rareItems){
            Item item = (Item) e;
            if ((item.getX() == x) && (item.getY() == y)){
                return item;
            }
        }
        return null;
    }

    /**
     * get the first pair of x,y coordinates which don't have any items in it in the unequipped inventory
     * @return x,y coordinate pair
     */
    public Pair<Integer, Integer> getFirstAvailableSlotForItem(){
        // first available slot for an item...
        // IMPORTANT - have to check by y then x, since trying to find first available slot defined by looking row by row
        for (int y=0; y<unequippedInventoryHeight; y++){
            for (int x=0; x<unequippedInventoryWidth; x++){
                if (getUnequippedInventoryItemEntityByCoordinates(x, y) == null){
                    return new Pair<Integer, Integer>(x, y);
                }
            }
        }
        // if we get to here, need to remove first unequipped item and try again
        Item item = unequippedInventoryItems.get(0);
        removeUnequippedInventoryItem(item);
        // increase character's gold as they're losing an item
        character.increaseMoney(item.getValue());
        character.increaseExperience(item.getValue()/10);
        return getFirstAvailableSlotForItem();
    }

     /**
     * get the first pair of x,y coordinates which don't have any items in it in the rareItems inventory
     * @return x,y coordinate pair
     */
    public Pair<Integer, Integer> getFirstAvailableSlotForRareItem(){
        for (int x=0; x<unequippedInventoryWidth; x++){
            if (getRareInventoryItemEntityByCoordinates(x, 0) == null){
                return new Pair<Integer, Integer>(x, 0);
            }
        }
        return null;
    }

    /**
     * add an item to unequipped inventory
     * @param item item to add
     */
    public void addToUnequipped(Item item) {
        if (item != null) {
            unequippedInventoryItems.add(item);
            timesInventoryChanged.setValue(timesInventoryChanged.getValue() + 1);
        }
    }


    /**
     * shift card coordinates down starting from x coordinate
     * @param x x coordinate which can range from 0 to width-1
     */
    public void shiftCardsDownFromXCoordinate(int x){
        for (Card c: cardEntities){
            if (c.getX() >= x){
                c.x().set(c.getX()-1);
            }
        }
    }

    /**
     * move all enemies
     */
    private void moveBasicEnemies() {
        for (BasicEnemy e: enemies) {
            e.move();
        }
    }

    /**
     * move all allied soldiers one step behind character
     */
    private void moveAlliedSoldiers() {
        for (AlliedSoldier soldier: alliedSoldiers) {
            soldier.move();
        }
    }

    /**
     * get a randomly generated position which could be used to spawn a slug
     * @return null if random choice is that wont be spawning an enemy or it isn't possible, or random coordinate pair if should go ahead
     */
    public Pair<Integer, Integer> getFreePosition(){
        List<Pair<Integer, Integer>> orderedPathSpawnCandidates = new ArrayList<>();
        int indexPosition = orderedPath.indexOf(new Pair<Integer, Integer>(character.getX(), character.getY()));
        // inclusive start and exclusive end of range of positions not allowed
        int startNotAllowed = (indexPosition - 2 + orderedPath.size())%orderedPath.size();
        int endNotAllowed = (indexPosition + 3)%orderedPath.size();
        // note terminating condition has to be != rather than < since wrap around...
        for (int i=endNotAllowed; i!=startNotAllowed; i=(i+1)%orderedPath.size()){
            orderedPathSpawnCandidates.add(orderedPath.get(i));
        }

        // choose random choice
        Pair<Integer, Integer> spawnPosition = orderedPathSpawnCandidates.get(rand.nextInt(orderedPathSpawnCandidates.size()));

        // check no building on that path
        Building bOnPath = getBuildingOnPath(spawnPosition.getValue0(), spawnPosition.getValue1());
        if (bOnPath != null) {
            return getFreePosition();
        }
        // check no item on that path
        for (Item item: groundItems) {
            if (item.getX() == spawnPosition.getValue0() && item.getY() == spawnPosition.getValue1()) {
                // already occupied
                return getFreePosition();
            }
        }

        return spawnPosition;

    }

    /**
     * spawn a item in the world and return the item entity
     * @param card card to return item for
     * @return a item to be spawned in the controller as a JavaFX node
     */
    public Item addItemForCard(Card card) {
        // if card is barracks or trap then don't do this
        Pair<Integer, Integer> firstAvailableSlot = getFirstAvailableSlotForItem();
        Item newItem = card.giveItemReward(new SimpleIntegerProperty(firstAvailableSlot.getValue0()), new SimpleIntegerProperty(firstAvailableSlot.getValue1()));
        addToUnequipped(newItem);
        return newItem;
    }


    /**
     * Generate a random item based on chance
     * @param x x coordinate of where to add
     * @param y y corrdinte of where to add
     * @return random item generated
     */
    public Item generateRandomItem(SimpleIntegerProperty x, SimpleIntegerProperty y, int chance) {

        if (chance <= 5) {
            return new HealthPotion(x, y);
        } else if (chance <= 15) {
            return new Staff(x, y);
        } else if (chance <= 30) {
            return new Armour(x, y);
        } else if (chance <= 45) {
            return new Shield(x, y);
        } else if (chance <= 60) {
            return new Sword(x, y);
        } else if (chance <= 75) {
            return new Stake(x, y);
        } else {
            return new Helmet(x, y);
        }
    }


    /**
     * remove a card by its x, y coordinates and show the building associated with that card on screen
     * @param cardNodeX x index from 0 to width-1 of card to be removed
     * @param cardNodeY y index from 0 to height-1 of card to be removed
     * @param buildingNodeX x index from 0 to width-1 of building to be added
     * @param buildingNodeY y index from 0 to height-1 of building to be added
     */
    public Building convertCardToBuilding(Card card, int cardNodeX, int buildingNodeX, int buildingNodeY) {
        Building newBuilding = card.createBuilding(new SimpleIntegerProperty(buildingNodeX), new SimpleIntegerProperty(buildingNodeY), buildingEntities);
        if (newBuilding == null) {
            // some sort of requirement not met, just give user gold
            character.increaseMoney(card.getValue());
        } else {
            addToBuildingEntities(newBuilding);
        }
        removeCard(card);
        return newBuilding;
    }

    /**
     * Get card using x, y coordinates
     * @param cardNodeX x coordinate of card
     * @param cardNodeY y coordinate of card
     * @return card at coordinates x, y
     */
    public Card getCardFromPosition(int cardNodeX, int cardNodeY) {
        Card card = null;
        for (Card c: cardEntities){
            if ((c.getX() == cardNodeX) && (c.getY() == cardNodeY)){
                card = c;
                break;
            }
        }
        return card;
    }

    /**
     * A card is burnt when there's too many cards -> burn oldest card
     * Should also give reward (money) to the character from the card
     * @return the item that will be provided to compoensate for burnt card
     */
    public Item burnOldestCard() {
        // as List maintains order, so first item was the first added
        Card removed = removeCard(cardEntities.get(0));
        // 2: get experience
        character.increaseExperience(20);
        // get gold
        character.increaseMoney(removed.getValue());
        // 3: get item based on issue #33 on gitlab and add to unequipped
        // inventory
        Item newItem = addItemForCard(removed);
        return newItem;
    }  

    /**
     * get a list of enemies that are in battle with character
     * @return list of enemies in battle
     */
    public ArrayList<BasicEnemy> getEnemiesInBattle() {
        ArrayList<BasicEnemy> allEnemies = new ArrayList<>();
        BasicEnemy mainEnemy = getEnemyInBattleRadius();
        if (mainEnemy != null) {
            allEnemies.add(mainEnemy);
            List<BasicEnemy> supportEnemies = getEnemiesInSupportRadius(mainEnemy);
            for (BasicEnemy enemy: supportEnemies) {
                allEnemies.add(enemy);
            }
        }
        return allEnemies;
    }

    public ArrayList<AlliedSoldier> getAlliedSoldiers() {
        return alliedSoldiers;
    }
    

    /**
     * run the expected battle in the world, based on current world state
     * once a battle is occuring isInBattle is set to true so entities stop
     * moving, in a battle the order is:
     *  - Check if any allied soldier needs to be converted to a zombie
     *  - check if any enemies need ot be converted to allied soldiers
     *  - if character has protective gear, apply the defence (to reduce damage)
     *  - if character has weapon, apply offence (to increase damage to enemy)
     *  - Character strikes every enemy
     *  - allied character strike every enemy 
     *  - Attack from any towers in good radius
     *  - enemy strike allied characters, once all allied characters are dead can strike character
     * Once battle is done, if char wins, check if they can get rareItem
     * @param enemiesInBattle enemies battling
     * @return list of enemies which have been killed
     */
    public ArrayList<BasicEnemy> runBattles(ArrayList<BasicEnemy> enemiesInBattle) {
        
        ArrayList<BasicEnemy> defeatedEnemies = new ArrayList<BasicEnemy>();

        for (BasicEnemy e: enemiesInBattle) {
            character.attack(e, rand.nextFloat());
        }

        // soldiers attac enemy
        for (AlliedSoldier soldier: alliedSoldiers) {
            for (BasicEnemy e: enemies) {
                soldier.attack(e);
            }
        }

        // tower attack enemies
        for (Building building: buildingEntities) {
            if (building instanceof TowerBuilding) {
                TowerBuilding tower = (TowerBuilding) building;
                tower.attackEnemies(enemies);
            }
        }

        // if elon exists it can heal enemies
        for (BasicEnemy e: enemies) {
            if (e instanceof Elan) {
                Elan elan = (Elan) e;
                elan.healEnemies(enemiesInBattle, rand.nextFloat());
            }
        }

        // get a list of defeated enemies
        for (BasicEnemy e: enemiesInBattle) {
            if (e.getHealth() <= 0) {
                defeatedEnemies.add(e);
            }
        }

        for (BasicEnemy e: defeatedEnemies){
            removeEnemy(e);
            enemiesInBattle.remove(e);
            // check if enemy is a boss to add to bosses killed
            if (e instanceof Boss) {
                character.addBossToKilled((Boss) e);
            }
        }

        for (BasicEnemy e: enemiesInBattle) {
            // enemy gets to strike now
            if (alliedSoldiers.size() > 0) {
                // only attack first allied soldier
                AlliedSoldier firstSoldier = alliedSoldiers.get(0);
                e.attackAlliedSolider(alliedSoldiers.get(0), rand.nextInt());
                if (alliedSoldiers.get(0).getHealth() <= 0) {
                    removeAlliedSoldier(firstSoldier);
                }
            } else {
                // attack character
                e.attackCharacter(character, rand.nextFloat(), rand.nextInt(10));
                // if they have one ring do something
                for (RareItem item: rareItems) {
                    if (item.canHeal()) {
                        // character has been respawned, can remove ring from rareItems
                        item.doMagic(0, character, null, 0, RareItemAction.HEAL);
                        // check if heal worked
                        if (character.getHealth().getValue() == Character.getFullhealth()) {
                            rareItems.remove(item);
                            ((Item) item).destroy();
                            break;
                        }
                    }
                }
                if (character.getHealth().getValue() <= 0) {
                    gameLost.setValue(true);
                }
            } 
        }
        return defeatedEnemies;
    }

    /**
     * when character wins a battle, there is a small chance
     * that they can get a rare item (depending on waht game mode)
     * 
     * a rare item will only be given once a game 
     * @return null if no rare item to return
     */
    public RareItem reactToWinBattle(float chance) {
        Pair<Integer, Integer> slot = getFirstAvailableSlotForRareItem();
        RareItem rareItem = state.getRareItem(rareItemsAllowed, slot, chance);
        if (rareItem != null) {
            rareItems.add(rareItem);
        }
        return rareItem;
    }

    /**
     * create a tax office and add to list of buildings
     * only create if there isn't already a tax office
     * @return newly created tax office
     */
    public TaxOffice createTaxOffice() {
        // only create tax office if doesn't already exist
        for (Building b: buildingEntities) {
            if (b instanceof TaxOffice) {
                return null;
            }
        }
        if (character.getIsTaxPayer()) {
            Pair<Integer, Integer> spot = getFreePosition();
            TaxOffice taxOffice = new TaxOffice(new SimpleIntegerProperty(spot.getValue0()), new SimpleIntegerProperty(spot.getValue1()));
            addToBuildingEntities(taxOffice);
            return taxOffice;
        }
        return null;
    }
    /**
     * Once an enemy enters a battle with a character, call on this method
     * to find if there are any other enemies within it's support radius to jon battle
     * @param enemy Enemy that character begins battle with
     * @return a list of other enemys that will join battle
     */
    public List<BasicEnemy> getEnemiesInSupportRadius(BasicEnemy enemy) {
        List<BasicEnemy> supportingEnemies = new ArrayList<BasicEnemy>();

        for (BasicEnemy e: enemies){
            if (e.equals(enemy)) {
                continue;
            }
            if (Math.pow((enemy.getX()-e.getX()), 2) +  Math.pow((enemy.getY()-e.getY()), 2) <= enemy.getSupportRadius()){
                supportingEnemies.add(e);
            }
        }
        return supportingEnemies;
    }

    /**
     * checks if the characer can start a battle with any enemies,
     * @return null if no enemies that a battle can be sarted wtih
     */
    public BasicEnemy getEnemyInBattleRadius() {
        for (BasicEnemy e: enemies){
            if (Math.pow((character.getX()-e.getX()), 2) +  Math.pow((character.getY()-e.getY()), 2) <= e.getBattleRadius()){
                return e;
            }
        }
        return null;
    }

    /**
     * move an item from unqipped to equipped
     * If that item type already exists in equpped, then
     * remove existing type (move to unequpped)
     * @param itemNodeX Item node x in unequipped
     * @param itemNodeY Item node y in unequpped
     */
    public Item moveItemToEquippedFromUnequipped(int itemNodeX, int itemNodeY, String type){
        // find the item from unequipped
        Item item = getUnequippedInventoryItemEntityByCoordinates(itemNodeX, itemNodeY);
        Item itemEjected = null;
        if (type.equals("shield")) {
            itemEjected = character.setShield(item);
        } else if (type.equals("armour")) {
            itemEjected = character.setArmour(item);
        } else if (type.equals("helmet")) {
            itemEjected = character.setHelmet(item);
        } else if (type.equals("weapon")) {
            itemEjected = character.setEquippedWeapon(item);
        }
        // remove item from unequipped since it's added to character
        removeUnequippedInventoryItem(item);
        
        if (itemEjected != null && itemEjected instanceof RareItem) {
            // add item to rare items
            moveItemToRare(itemEjected);
        } else {
            moveItemToUnequipped(itemEjected);
        }
        return itemEjected;
    }

    /**
     * move an item from rare to equipped
     * If that item type already exists in equpped, then
     * remove existing type (move to rare)
     * @param itemNodeX Item node x in rare
     * @param itemNodeY Item node y in rare
     */
    public Item moveItemToEquippedFromRare(int itemNodeX, int itemNodeY, String type){
        // find the item from unequipped
        Item item = getRareInventoryItemEntityByCoordinates(itemNodeX, itemNodeY);

        Item itemEjected = null;
        if (type.equals("shield")) {
            itemEjected = character.setShield(item);
        } else if (type.equals("weapon")) {
            itemEjected = character.setEquippedWeapon(item);
        }
        
        // remove item from rare since it's added to character
        rareItems.remove((RareItem) item);
        if (itemEjected instanceof RareItem) {
            // add item to rare items
            moveItemToRare(itemEjected);
        } else {
            moveItemToUnequipped(itemEjected);
        }
        
        return itemEjected;
    }

    /**
     * move an item from eqipped to unequipped
     * If that item type already exists in equpped, then
     * remove existing type (move to unequpped)
     * @param itemNodeX Item node x in equipped
     * @param itemNodeY Item node y in equpped
     */
    public void moveItemToUnequipped(Item item){
        if (item != null) {
            addToUnequipped(item);
            // update its location
            Pair<Integer, Integer> firstAvailableSlot = getFirstAvailableSlotForItem();
            // set item's new values
            item.setX(new SimpleIntegerProperty(firstAvailableSlot.getValue0()));
            item.setY(new SimpleIntegerProperty(firstAvailableSlot.getValue1()));
        }

    }

    public void moveItemToRare(Item item) {
        rareItems.add((RareItem) item);
        Pair<Integer, Integer> firstAvailableSlot = getFirstAvailableSlotForRareItem();
        item.setX(new SimpleIntegerProperty(firstAvailableSlot.getValue0()));
            item.setY(new SimpleIntegerProperty(firstAvailableSlot.getValue1()));
    }

    /**
     * return the building that's at coordinates x, y
     * @param x x coordinate of building
     * @param y y coordinate of building
     * @return null of no buildings, otherwise will return the building at that position
     */
    public Building getBuildingOnPath(int x, int y) {
        for (Building b: buildingEntities) {
            if (b.getX() == x && b.getY() == y) {
                return b;
            }
        }
        return null;
    }


    /**
     * Check if there is an item on the path and pick it up
     * ie. remove item from groundItems and add item to unequippedInventory
     * unless it is gold, then add value to user's gold
     * @param x
     * @param y
     * @return list of items picked up to add to inventory
     */
    public ArrayList<Item> pickUpItemOnPath() {
        ArrayList<Item> originalItems = new ArrayList<>(groundItems);
        ArrayList<Item> pickedUpItems = new ArrayList<>();
        int x = character.getX();
        int y = character.getY();

        for (Item item: originalItems) {
            if (item.getX() == x && item.getY() == y) {
                // if it's gold then increase char's gold
                if (item instanceof Gold) {
                    character.increaseMoney(item.getValue());
                    item.destroy();
                    item.addToGrid();
                } else {
                    // add to user's unquipped inventory
                    Pair<Integer, Integer> firstAvailableSlot = getFirstAvailableSlotForItem();
                    // set item's new values
                    item.setX(new SimpleIntegerProperty(firstAvailableSlot.getValue0()));
                    item.setY(new SimpleIntegerProperty(firstAvailableSlot.getValue1()));
                    addToUnequipped(item);
                }
                groundItems.remove(item);
                pickedUpItems.add(item);
                item.destroy();
                item.addToGrid();
            }
        }
        return pickedUpItems;
    }

    /**
     * for every enemy, check if they are stepping on a trap
     * if so, apply trap's damage to them and kill if damage <= 0
     */
    public void checkForEnemiesOnTrap() {
        for (BasicEnemy e: enemies) {
            Building potentialBuilding = getBuildingOnPath(e.getX(), e.getY());
            if (potentialBuilding instanceof Trap) {
                // apply trap's damage
                Trap trap = (Trap) potentialBuilding;
                trap.damageEnemy(e);
                // remove trap
                buildingEntities.remove(trap);
                trap.destroy();
            }
        }
    }

    /**
     * for every enemy, check if they are stepping on a trap
     * if so, apply trap's damage to them and kill if damage <= 0
     */
    public void checkForVampireNearCampfire() {
        for (Building b: buildingEntities) {
            if (b instanceof Campfire) {
                Campfire campfire = (Campfire) b;
                campfire.scareVampire(enemies);
            }
        }
    }

    /**
     * for every enemy, check if they are stepping on a trap
     * if so, apply trap's damage to them and kill if damage <= 0
     */
    public void checkForTaxOffice() {
        Building potentialBuilding = getBuildingOnPath(character.getX(), character.getY());
        if (potentialBuilding instanceof TaxOffice) {
            TaxOffice ato = (TaxOffice) potentialBuilding;
                ato.taxCharacter(character);
        }
    }


    /**
     * add to building entiteis, mostly for testing
     * @param building the building to add
     */
    public void addToBuildingEntities(Building building) {
        buildingEntities.add(building);
        if (building instanceof Barracks) {
            attachSoldierCreator((Barracks) building);
        } else if (building instanceof EnemyCreator) {
            attachEnemyCreator((EnemyCreator) building);
        }
    }

    /**
     * check if character is stepping on barracks, create
     * an allied soldier if so
     */
    public void checkForVillage() {
        Building potentialBuilding = getBuildingOnPath(character.getX(), character.getY());
        if (potentialBuilding instanceof Village) {
            // apply trap's damage
            Village village = (Village) potentialBuilding;
            village.regainHealth(character);
        }
    }

    /**
     * possibly spawn a new item and add
     * to ground items
     * @return newly added items
     */
    public ArrayList<Item> possibleSpawnItem() {
        ArrayList<Item> newlyAddedItems = new ArrayList<>();
        int maxItemsOnGround = 2;
        int maxItems = maxItemsOnGround - groundItems.size();
        for (int i = 0; i < maxItems; i++) {
            Pair<Integer, Integer> pathPoint = getFreePosition();
            if (pathPoint == null) {
                continue;
            }
            SimpleIntegerProperty x = new SimpleIntegerProperty(pathPoint.getValue0()); 
            SimpleIntegerProperty y = new SimpleIntegerProperty(pathPoint.getValue1()); 
            Item newItem = generateRandomItem(x, y, rand.nextInt(100));
            groundItems.add(newItem);
            newlyAddedItems.add(newItem);
        }
        return newlyAddedItems;
    }


    /**
     * notify enemy buildings to possibly spwan enemies
     * this will call the buildin's spwanEnemey
     * @param inBattle if in active battle set to true
     * @return list of new enemies that will spawm
     */
    public List<BasicEnemy> spawnEnemies(Boolean inBattle) {

        List<BasicEnemy> newEnemies = new ArrayList<>();

        ArrayList<EnemyCreator> copyOfCreators = new ArrayList<>(enemyCreators);
        for (EnemyCreator creator: copyOfCreators) {
            
            BasicEnemy newEnemy = creator.spawnEnemy(orderedPath, character, inBattle);
            if (newEnemy == null &&  
            creator instanceof AlliedSoldier) {
                AlliedSoldier soldier = (AlliedSoldier) creator;
                if (soldier.getPrevEnemy() != null) {
                    detachEnemyCreator(creator);
                    removeAlliedSoldier(soldier);
                }
            } else if (newEnemy != null) {
                // if Elan then fluctuates price of doggie so add
                if (newEnemy instanceof Elan) {
                    doggieValue.setValue(doggieValue.getValue() * 1000);
                }
                addEnemy(newEnemy);
                newEnemies.add(newEnemy);
            }
        }
        return newEnemies;
    }
 

    /**
     * check if a new allied soldier can be created
     * @return empty array if no new soldiers can be created
     */
    public ArrayList<AlliedSoldier> possiblyCreateAlliedSoldier() {
        ArrayList<AlliedSoldier> newSoldiers = new ArrayList<>();
        ArrayList<AlliedSoldierCreator> initialCreators = new ArrayList<>(soldierCreators);
        // check for soldier from enemies
        for (AlliedSoldierCreator creator: initialCreators) {
            AlliedSoldier soldier = creator.produceAlliedSolider(character, orderedPath, numberOfAlliedSoldiers.getValue());
            if (soldier != null) {
                addAlliedSoldier(soldier);
                newSoldiers.add(soldier);      
                // if it's an enemy then remove the enemy
                if (creator instanceof BasicEnemy) {
                    BasicEnemy enemy = (BasicEnemy) creator;
                    removeEnemy(enemy);
                }   
            }
        }
        
        return newSoldiers;
    }


    // BUYING AND SELLNG FROM HERO CASTLE

    /**
     * buy an armour from store,
     * @return newly created armour
     */
    public Armour buyArmour() {

        if (character.getMoney().getValue() < Armour.getCost()) {
            return null;
        }
        Pair<Integer, Integer> firstAvailableSlot = getFirstAvailableSlotForItem();
        Armour newArmour =  new Armour(new SimpleIntegerProperty(firstAvailableSlot.getValue0()), new SimpleIntegerProperty(firstAvailableSlot.getValue1()));
        addToUnequipped(newArmour);
        
        character.decreaseMoney(newArmour.getValue());
        return newArmour;
    }


    /**
     * buy a helmet from store,
     * @return newly created helmet
     */
    public Helmet buyHelmet() {
        if (character.getMoney().getValue() < Helmet.getCost()) {
            return null;
        }
        Pair<Integer, Integer> firstAvailableSlot = getFirstAvailableSlotForItem();
        Helmet newHelmet =  new Helmet(new SimpleIntegerProperty(firstAvailableSlot.getValue0()), new SimpleIntegerProperty(firstAvailableSlot.getValue1()));
        addToUnequipped(newHelmet);
        character.decreaseMoney(newHelmet.getValue());
        return newHelmet;
    }

    /**
     * buy a helmet from store,
     * @return newly created helmet
     */
    public Shield buyShield() {
        if (character.getMoney().getValue() < Shield.getCost()) {
            return null;
        }
        Pair<Integer, Integer> firstAvailableSlot = getFirstAvailableSlotForItem();
        Shield newShield =  new Shield(new SimpleIntegerProperty(firstAvailableSlot.getValue0()), new SimpleIntegerProperty(firstAvailableSlot.getValue1()));
        addToUnequipped(newShield);
        character.decreaseMoney(newShield.getValue());
        return newShield;
    }

    /**
     * buy a sword from store,
     * @return newly created sword
     */
    public Sword buySword() {
        if (character.getMoney().getValue() < Sword.getCost()) {
            return null;
        }
        Pair<Integer, Integer> firstAvailableSlot = getFirstAvailableSlotForItem();
        Sword newSword =  new Sword(new SimpleIntegerProperty(firstAvailableSlot.getValue0()), new SimpleIntegerProperty(firstAvailableSlot.getValue1()));
        addToUnequipped(newSword);
        character.decreaseMoney(newSword.getValue());
        return newSword;
    }

    /**
     * buy a sword from store,
     * @return newly created sword
     */
    public Stake buyStake() {
        if (character.getMoney().getValue() < Stake.getCost()) {
            return null;
        }
        Pair<Integer, Integer> firstAvailableSlot = getFirstAvailableSlotForItem();
        Stake newStake =  new Stake(new SimpleIntegerProperty(firstAvailableSlot.getValue0()), new SimpleIntegerProperty(firstAvailableSlot.getValue1()));
        addToUnequipped(newStake);
        character.decreaseMoney(newStake.getValue());
        return newStake;
    }

    /**
     * buy a sword from store,
     * @return newly created sword
     */
    public Staff buyStaff() {
        if (character.getMoney().getValue() < Staff.getCost()) {
            return null;
        }
        Pair<Integer, Integer> firstAvailableSlot = getFirstAvailableSlotForItem();
        Staff newStaff =  new Staff(new SimpleIntegerProperty(firstAvailableSlot.getValue0()), new SimpleIntegerProperty(firstAvailableSlot.getValue1()));
        addToUnequipped(newStaff);
        character.decreaseMoney(newStaff.getValue());
        return newStaff;
    }

    /**
     * buy a health potion from store,
     * @param x x coordinate of first available spot in unequipped inventory
     * @param y y coordinate of first available spot in unequipped inventory
     * @return newly created health potion
     */
    public HealthPotion buyHealthPotion() {
        if (character.getMoney().getValue() < HealthPotion.getCost()) {
            return null;
        }
        Pair<Integer, Integer> firstAvailableSlot = getFirstAvailableSlotForItem();
        HealthPotion newHealthPotion =  new HealthPotion(new SimpleIntegerProperty(firstAvailableSlot.getValue0()), new SimpleIntegerProperty(firstAvailableSlot.getValue1()));
        addToUnequipped(newHealthPotion);
        character.decreaseMoney(newHealthPotion.getValue());
        return newHealthPotion;
    }

    /**
     * Returns the first item in unequipped inventory
     * that matches the obj type
     * 
     * @param obj an object of the type to get instance of
     * @return first object found, or null if not found
     */
    public Item getFirstUnequippedItemOfType(Object obj) {
        for (Item item : unequippedInventoryItems) {
            if (item.getClass().equals(obj.getClass())) {
                return item;
            }
        }
        return null;
    }

    /**
     * This function sells the first health potion in unequipped inventory
     * and gives the character compensation.
     * 
     * It will never not find a health potion,
     * so no error checking in this function
     */
    public void sellHealthPotion() {
        // get the first potion in uneqipped inventory
        // delete potion will never be null
        // as button will be disabled
        HealthPotion deletePotion = (HealthPotion) getFirstUnequippedItemOfType(new HealthPotion(null, null));

        // compensate character and remove from inventory
        character.increaseMoney(deletePotion.getValue());
        removeUnequippedInventoryItem(deletePotion);
    }

    /**
     * This function sells the first stake in unequipped inventory
     * and gives the character compensation.
     * 
     * It will never not find a stake,
     * so no error checking in this function
     */
    public void sellStake() {
        // get the first potion in uneqipped inventory
        // delete stake will never be null
        // as button will be disabled
        Stake deleteStake = (Stake) getFirstUnequippedItemOfType(new Stake(null, null));

        // compensate character and remove from inventory
        character.increaseMoney(deleteStake.getValue());
        removeUnequippedInventoryItem(deleteStake);
    }

    /**
     * This function sells the first Sword in unequipped inventory
     * and gives the character compensation.
     * 
     * It will never not find a Sword,
     * so no error checking in this function
     */
    public void sellSword() {
        // get the first potion in uneqipped inventory
        // delete Sword will never be null
        // as button will be disabled
        Sword deleteSword = (Sword) getFirstUnequippedItemOfType(new Sword(null, null));

        // compensate character and remove from inventory
        character.increaseMoney(deleteSword.getValue());
        removeUnequippedInventoryItem(deleteSword);
    }

    /**
     * This function sells the first Staff in unequipped inventory
     * and gives the character compensation.
     * 
     * It will never not find a Staff,
     * so no error checking in this function
     */
    public void sellStaff() {
        // get the first potion in uneqipped inventory
        // delete Staff will never be null
        // as button will be disabled
        Staff deleteStaff = (Staff) getFirstUnequippedItemOfType(new Staff(null, null));

        // compensate character and remove from inventory
        character.increaseMoney(deleteStaff.getValue());
        removeUnequippedInventoryItem(deleteStaff);
    }

    /**
     * This function sells the first Helmet in unequipped inventory
     * and gives the character compensation.
     * 
     * It will never not find a Helmet,
     * so no error checking in this function
     */
    public void sellHelmet() {
        // get the first potion in uneqipped inventory
        // delete Helmet will never be null
        // as button will be disabled
        Helmet deleteHelmet = (Helmet) getFirstUnequippedItemOfType(new Helmet(null, null));

        // compensate character and remove from inventory
        character.increaseMoney(deleteHelmet.getValue());
        removeUnequippedInventoryItem(deleteHelmet);
    }

    /**
     * This function sells the first Armour in unequipped inventory
     * and gives the character compensation.
     * 
     * It will never not find a Armour,
     * so no error checking in this function
     */
    public void sellArmour() {
        // get the first potion in uneqipped inventory
        // delete Armour will never be null
        // as button will be disabled
        Armour deleteArmour = (Armour) getFirstUnequippedItemOfType(new Armour(null, null));
        // compensate character and remove from inventory
        character.increaseMoney(deleteArmour.getValue());
        removeUnequippedInventoryItem(deleteArmour);
    }

    /**
     * sell dogge coin at the shop 
     * will get the current exhcange rate for doggie coin to 
     * figure out how much gold to add to character
     * @param amount amount of doggie coin to sell
     */
    public void sellDoggie(int amount) {
        int moneyAmount = doggieValue.getValue() * amount;
        character.increaseMoney(moneyAmount);
        character.increaseDoggieCoin(-1 * (amount));
    }


    /**
     * This function sells the first Shield in unequipped inventory
     * and gives the character compensation.
     * 
     * It will never not find a Shield,
     * so no error checking in this function
     */
    public void sellShield() {
        // get the first potion in uneqipped inventory
        // delete Shield will never be null
        // as button will be disabled
        Shield deleteShield = (Shield) getFirstUnequippedItemOfType(new Shield(null, null));

        // compensate character and remove from inventory
        character.increaseMoney(deleteShield.getValue());
        removeUnequippedInventoryItem(deleteShield);
    }

     /**
     * check if coordinates is on a path or
     * not
     * @param x x coordinate
     * @param y y coordinate
     * @return true if on character's walking path or false otherwise
     */
    public Boolean isOnPath(int x, int y) {
        for (Pair<Integer, Integer> coordinate: orderedPath) {
            if (coordinate.getValue0() == x && coordinate.getValue1() == y) {
                return true;
            }
        }
        return false;
    }

    public void drinkHealthPotion() {
        // get the first potion in unequipped inventory
        HealthPotion potionToDrink = (HealthPotion) getFirstUnequippedItemOfType(new HealthPotion(null, null));
        // checking there is a potion in inventory
        if (potionToDrink != null) {
            // give character back health
            potionToDrink.regainHealth(character);
            // remove from inventory
            removeUnequippedInventoryItem(potionToDrink);
        }
    }

    
    public void attachEnemyCreator(EnemyCreator e) {
        enemyCreators.add(e);
        
    }

    public void detachEnemyCreator(EnemyCreator e) {
        enemyCreators.remove(e);
    }

    public void attachSoldierCreator(AlliedSoldierCreator creator) {
        soldierCreators.add(creator);
    }

    public void detachSoldierCreator(AlliedSoldierCreator creator) {
        soldierCreators.remove(creator);
    }


    /**
     * GETTERS AND SETTERS
     */

    public ArrayList<BasicEnemy> getEnemies() {
        return this.enemies;
    }

    public IntegerProperty getTimesInventoryChanged() {
        return this.timesInventoryChanged;
    }
    
    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public ArrayList<Item> getGroundItems() {
        return groundItems;
    }

    public void setCharacter(Character character) {
        this.character = character;
    }

    public Character getCharacter() {
        return character;
    }

    public List<Card> getCardEntities() {
        return cardEntities;
    }

    public List<Item> getUnEquippedItems() {
        return unequippedInventoryItems;
     }


    public IntegerProperty getNumberOfAlliedSoldiers() {
        return this.numberOfAlliedSoldiers;
    }

    public String getGoalString() {
        return goals.getGoalDescription().toString();
    }

    public ArrayList<Building> getBuildingEntities() {
        return this.buildingEntities;
    }

    public State getState() {
        return this.state;
    }

    public void addToRareItem(RareItem e) {
        this.rareItems.add(e);
    }

    public ArrayList<RareItem> getRareItems() {
        return rareItems;
    }

}
