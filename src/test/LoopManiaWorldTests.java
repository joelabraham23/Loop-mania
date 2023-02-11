package test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.javatuples.Pair;

import unsw.loopmania.*;
import unsw.loopmania.Character;

import org.junit.Test;

import javafx.beans.property.SimpleIntegerProperty;
import unsw.loopmania.Buildings.Barracks;
import unsw.loopmania.Buildings.Campfire;
import unsw.loopmania.Buildings.*;
import unsw.loopmania.Buildings.HeroCastle;
import unsw.loopmania.Buildings.VampireCastleBuilding;
import unsw.loopmania.Buildings.ZombiePitBuilding;
import unsw.loopmania.Cards.*;
import unsw.loopmania.Enemies.Slug;
import unsw.loopmania.Enemies.Vampire;
import unsw.loopmania.Enemies.Zombie;
import unsw.loopmania.Enemies.Bosses.Elan;
import unsw.loopmania.Goals.*;
import unsw.loopmania.Items.*;
import unsw.loopmania.RareItems.Anduril;
import unsw.loopmania.RareItems.RareItem;
import unsw.loopmania.RareItems.TreeStump;
import unsw.loopmania.GameModes.*;

public class LoopManiaWorldTests {

    TestHelper helper = new TestHelper();
    ArrayList<Pair<Integer, Integer>> orderedPath = helper.getOrderedPath();
    LoopManiaWorld game = new LoopManiaWorld(3, 3, orderedPath, new AndCompositeGoal(), new ArrayList<>(),
            new BerserkerMode());
    Character c = game.getCharacter();

    @Test
    public void testBurnOldestCard() {
    
        Card firstCard = new VampireCastleCard(new SimpleIntegerProperty(), new SimpleIntegerProperty());
        game.addToCardEntities(firstCard);
        Card secondCard = new VampireCastleCard(new SimpleIntegerProperty(), new SimpleIntegerProperty());
        game.addToCardEntities(secondCard);
        Card thirdCard = new VampireCastleCard(new SimpleIntegerProperty(), new SimpleIntegerProperty());
        game.addToCardEntities(thirdCard);
        game.burnOldestCard();
        // check that firstCard no longer here
        // check newCard is also added to world's entites
        List<Card> cards = game.getCardEntities();
        assertTrue(!cards.contains(firstCard));
        // check contains the rest of the cards
        assertTrue(cards.contains(secondCard));
        // not contain first card anymore
        assertTrue(cards.contains(thirdCard));
    }

    @Test
    public void testBurnOldestCardGivesGold() {
        int ogMoney = game.getCharacter().getMoney().getValue();
        Card firstCard = new VampireCastleCard(new SimpleIntegerProperty(), new SimpleIntegerProperty());
        game.addToCardEntities(firstCard);
        Card secondCard = new VampireCastleCard(new SimpleIntegerProperty(), new SimpleIntegerProperty());
        game.addToCardEntities(secondCard);
        
        game.burnOldestCard();
        // check game's character's money
        assertTrue(game.getCharacter().getMoney().getValue() > ogMoney );
        // check game's character's experience has increased
        assertTrue(game.getCharacter().getExperience().getValue() > 0);
    }



    @Test
    public void testConvertCardToBuildingByCoordinates() {
        Vampire v = new Vampire(null);

        Item card = v.giveCardRewardForDefeat(new Pair<>(1,1));
        // try to turn to a building

        Building res = game.convertCardToBuilding((Card) card, card.getX(), 1, 5);
        // check res is not null ie actally made a building
        assertNotNull(res); 
        // test card no longer exists
        assertTrue(!game.getCardEntities().contains(card));
    }

    @Test
    public void testGetEnemiesInSupportRadius() {
        PathPosition vampirePath = new PathPosition(1, Arrays.asList(new Pair<>(1, 1), new Pair<>(1, 2)));
        PathPosition zombiePath = new PathPosition(1, Arrays.asList(new Pair<>(1, 1), new Pair<>(1, 2)));
        PathPosition slugPath = new PathPosition(0, Arrays.asList(new Pair<>(15, 15), new Pair<>(20, 20)));

        Vampire v1 = new Vampire(vampirePath);
        game.addEnemy(v1);
        // create a zombie in support radius
        Zombie z = new Zombie(zombiePath);
        game.addEnemy(z);

        // slug not in suppport radius
        Slug s = new Slug(slugPath);
        game.addEnemy(s);
        List<BasicEnemy> enemies = game.getEnemiesInSupportRadius(v1);
        // check z in enemies
        assertTrue(enemies.contains(z));
        assertFalse(enemies.contains(s));

    }

    @Test
    public void testGetEnemyInBattleRadius() {
        // test if character can start a cheeky battle
        PathPosition vampirePath = new PathPosition(1, Arrays.asList(new Pair<>(1, 1), new Pair<>(1, 2)));
        PathPosition slugPath = new PathPosition(0, Arrays.asList(new Pair<>(15, 15), new Pair<>(20, 20)));

        Vampire v1 = new Vampire(vampirePath);
        game.addEnemy(v1);

        // slug not in suppport radius
        Slug s = new Slug(slugPath);
        game.addEnemy(s);

        // cahracter should enter battle with vampire
        BasicEnemy enemy = game.getEnemyInBattleRadius();
        assertEquals(enemy, v1);

    }

    @Test
    public void testGetEnemyInBattleRadiusNull() {
        // test if character can start a battle
        PathPosition slugPath = new PathPosition(0, Arrays.asList(new Pair<>(15, 15), new Pair<>(20, 20)));

        // slug not in suppport radius
        Slug s = new Slug(slugPath);
        game.addEnemy(s);

        // character not be able to start any battles
        BasicEnemy enemy = game.getEnemyInBattleRadius();
        assertNull(enemy);

    }



    @Test
    public void testMoveItemToEquipped() {
        // create 2 weapons
        Sword weapon1 = new Sword(new SimpleIntegerProperty(1), new SimpleIntegerProperty(1));
        Sword weapon2 = new Sword(new SimpleIntegerProperty(2), new SimpleIntegerProperty(2));
        game.addToUnequipped(weapon1);
        game.addToUnequipped(weapon2);
        game.moveItemToEquippedFromUnequipped(1, 1, "weapon");
        // check character
        assertEquals(game.getCharacter().getEquippedWeapon(), weapon1);
        // move weapon 2 now
        game.moveItemToEquippedFromUnequipped(2, 2, "weapon");
        assertEquals(game.getCharacter().getEquippedWeapon(), weapon2);
        // check that item 1 is back to unequipped
        assertTrue(game.getUnEquippedItems().contains(weapon1));
        assertFalse(game.getUnEquippedItems().contains(weapon2));
    }

    @Test
    public void testNumberOfAlliedSoldiers() {
        // create 2 weapons
        Character c = game.getCharacter();
        Barracks b = new Barracks(new SimpleIntegerProperty(c.getX()), new SimpleIntegerProperty(c.getY()));
       
        game.addToBuildingEntities(b);
        game.possiblyCreateAlliedSoldier();

        int numSoldiers = game.getNumberOfAlliedSoldiers().getValue();
        assertEquals(numSoldiers, 1);
    }

    @Test
    public void testTimesInventoryChanged() {
        // create 2 weapons
        Character c = game.getCharacter();
        Item newItem = game.generateRandomItem(new SimpleIntegerProperty(c.getX()), new SimpleIntegerProperty(c.getY()), 1);
        // give it to character
        game.addToUnequipped(newItem);
        int timesChanged = game.getTimesInventoryChanged().getValue();
        assertEquals(timesChanged, 1);
    }

    @Test
    public void testReactTOEnemyDefeatVampire() {
        // test if character can start a cheeky battle
        PathPosition vampirePath = new PathPosition(1, Arrays.asList(new Pair<>(1, 1), new Pair<>(1, 2)));
        Character c = game.getCharacter();
        int ogMoney = c.getMoney().getValue();
        int ogExperience = c.getExperience().getValue();
        Vampire v1 = new Vampire(vampirePath);
        game.addEnemy(v1);

        ArrayList<BasicEnemy> defeated = new ArrayList<>();
        defeated.add(v1);

        ArrayList<Item> rewards = game.reactToEnemyDefeat(defeated);
        assertTrue(ogMoney < c.getMoney().getValue());
        assertTrue(ogExperience < c.getExperience().getValue());
        // check rewards contains two item
        assertEquals(rewards.size(), 2);        

    }

    @Test
    public void testReactTOEnemyDefeatSlug() {
        // test if character can start a cheeky battle
        PathPosition sPath = new PathPosition(1, Arrays.asList(new Pair<>(1, 1), new Pair<>(1, 2)));
        Character c = game.getCharacter();
        int ogMoney = c.getMoney().getValue();
        int ogExperience = c.getExperience().getValue();
        Slug s = new Slug(sPath);
        game.addEnemy(s);

        ArrayList<BasicEnemy> defeated = new ArrayList<>();
        defeated.add(s);

        ArrayList<Item> rewards = game.reactToEnemyDefeat(defeated);
        assertTrue(ogMoney < c.getMoney().getValue());
        assertTrue(ogExperience < c.getExperience().getValue());
        // check rewards contains one item (nno equipment)
        assertEquals(rewards.size(), 1);        

    }

    @Test
    public void testRemoveUnequipedInventory() {
        // test if character can start a cheeky battle
        Item item = game.generateRandomItem(new SimpleIntegerProperty(1), new SimpleIntegerProperty(1), 1);
        Item item2 = game.generateRandomItem(new SimpleIntegerProperty(2), new SimpleIntegerProperty(2), 1);
        game.addToUnequipped(item);
        game.addToUnequipped(item2);

        game.removeUnequippedInventoryItemByCoordinates(1, 1);
        // check item no longer in inventory
        List<Item> items = game.getUnEquippedItems();

        assertTrue(items.contains(item2));
        assertFalse(items.contains(item));

    }

    @Test
    public void testCharacterMoves() {
        Character c = game.getCharacter();
        c.setX(1);
        c.setY(1);
        c.moveUpPath();
        // check character moved
        assertTrue(c.getX() != 1 && c.getY() != 1);

    }

    @Test
    public void testTooManySoldiersBarracks() {
        
        Character c = game.getCharacter();
        Barracks b = new Barracks(new SimpleIntegerProperty(c.getX()), new SimpleIntegerProperty(c.getY()));

        int maxAlliedSoldiers = 4; // decided in assumptions
        game.addToBuildingEntities(b);
        AlliedSoldier soldier = b.produceAlliedSolider(c, orderedPath, maxAlliedSoldiers);
        assertNull(soldier);
    }

    @Test
    public void testAlliedSoliderAtHC() {
        
        Character c = game.getCharacter();
        c.setX(0);
        c.setY(0);
        Barracks b = new Barracks(new SimpleIntegerProperty(c.getX()), new SimpleIntegerProperty(c.getY()));
        
        game.addToBuildingEntities(b);
        AlliedSoldier soldier = b.produceAlliedSolider(c, orderedPath, game.getNumberOfAlliedSoldiers().intValue());
        assertNotNull(soldier);
    }

    @Test
    public void testVampireReversesAtCampfire() {
        
        Character c = game.getCharacter();
        // get to right conditions for a vampire spawn
        c.setX(0);
        c.setY(0);
        for (int i = 0; i < 5; i++) {
            c.increaseCyclesCompleted();
        }

        // create a close and far vampire
        VampireCastleBuilding vc = new VampireCastleBuilding(new SimpleIntegerProperty(1), new SimpleIntegerProperty(1));
        Vampire v = vc.spawnEnemy(orderedPath, c, false);
        Vampire vFar = vc.spawnEnemy(orderedPath, c, false);
        vFar.setX(6);
        vFar.setY(7);

        // add a zombie to cover the instance of branch
        ZombiePitBuilding zpit = new ZombiePitBuilding(new SimpleIntegerProperty(1), new SimpleIntegerProperty(2));
        Zombie z = zpit.spawnEnemy(orderedPath, c, false);
        
        // populate enemies array
        ArrayList<BasicEnemy> enemies = new ArrayList<BasicEnemy>();
        enemies.add(z);
        enemies.add(v);
        enemies.add(vFar);
        
        // make a campfire
        Campfire fire = new Campfire(new SimpleIntegerProperty(1), new SimpleIntegerProperty(0));
        assertTrue(v.getCurrentlyMovingUp());
        assertTrue(vFar.getCurrentlyMovingUp());
        
        // test it does nothing to zombies
        fire.scareVampire(enemies);
        assertFalse(v.getCurrentlyMovingUp()); // and vampire now does in a direction
        assertTrue(vFar.getCurrentlyMovingUp());
        

        // vampire now does in opposite direction
        fire.scareVampire(enemies);
        assertTrue(v.getCurrentlyMovingUp());
        assertTrue(vFar.getCurrentlyMovingUp());
    }

    @Test
    public void testRemoveEnemies() {
        PathPosition vampirePath = new PathPosition(1, Arrays.asList(new Pair<>(1, 1), new Pair<>(1, 2)));

       
        Vampire v1 = new Vampire(vampirePath);
        game.addEnemy(v1);

        assertEquals(1, game.getEnemies().size());

        game.removeEnemy(v1);
        assertEquals(0, game.getEnemies().size());
    }

    @Test
    public void testRemoveAlliedSoldier() {
        // create new game
       
        Character c = game.getCharacter();
        Barracks b = new Barracks(new SimpleIntegerProperty(c.getX()), new SimpleIntegerProperty(c.getY()));
       
        game.addToBuildingEntities(b);
        ArrayList<AlliedSoldier> allSoldiers = game.possiblyCreateAlliedSoldier();

        int numSoldiers = game.getNumberOfAlliedSoldiers().getValue();
        assertEquals(numSoldiers, 1);

        game.removeAlliedSoldier(allSoldiers.get(0));
        
        numSoldiers = game.getNumberOfAlliedSoldiers().getValue();
        assertEquals(numSoldiers, 0);
    }

    @Test
    public void testAddHerosCastleEntity() {
        
        HeroCastle newHeroCastle = new HeroCastle(new SimpleIntegerProperty(0), new SimpleIntegerProperty(0));
        game.addToBuildingEntities(newHeroCastle);
        ArrayList<Building> allBuildings = game.getBuildingEntities();
        assertEquals(1, allBuildings.size());
    }

    @Test
    public void testPossiblySpawnSlug() {
        SlugCreator creator = new SlugCreator();
        game.attachEnemyCreator(creator);
        Slug newSlug = (Slug) creator.spawnEnemy(orderedPath, game.getCharacter(), false);
        if (newSlug != null) {
            game.addEnemy(newSlug);
            // slug will be created check that slugs added
            ArrayList<BasicEnemy> enemies = game.getEnemies();
            assertTrue(enemies.contains(newSlug));
        }

    }

    @Test
    public void testReactTOEnemyDefeatZombie() {
        // test if character can start a cheeky battle
        PathPosition zombiePath = new PathPosition(1, Arrays.asList(new Pair<>(1, 1), new Pair<>(1, 2)));
        Character c = game.getCharacter();
        int ogMoney = c.getMoney().getValue();
        int ogExperience = c.getExperience().getValue();
        Zombie z = new Zombie(zombiePath);
        game.addEnemy(z);

        ArrayList<BasicEnemy> defeated = new ArrayList<>();
        defeated.add(z);

        ArrayList<Item> rewards = game.reactToEnemyDefeat(defeated);
        assertTrue(ogMoney < c.getMoney().getValue());
        assertTrue(ogExperience < c.getExperience().getValue());
        // check rewards contains two item
        assertEquals(rewards.size(), 2);        

    }


    @Test
    public void testRunTickMovesCheckActive() {
        
        HeroCastle castle = new HeroCastle(new SimpleIntegerProperty(0), new SimpleIntegerProperty(0));
        game.addToBuildingEntities(castle);
        Character c = game.getCharacter();
        // starts with 0 cycles
        assertEquals(0, c.getCyclesCompleted().getValue());
        game.runTickMoves(true);
        // should not add a cycle because in active battle
        assertEquals(0, c.getCyclesCompleted().getValue());
    }

    @Test
    public void testGetUnequippedInventoryItemEntityByCoordinates() {
       
        Sword s = new Sword(new SimpleIntegerProperty(1), new SimpleIntegerProperty(1));
        game.addToUnequipped(s);
        // look in all the wrong places for sword
        assertNull(game.getUnequippedInventoryItemEntityByCoordinates(1, 2));
        assertNull(game.getUnequippedInventoryItemEntityByCoordinates(2, 2));
        assertNull(game.getUnequippedInventoryItemEntityByCoordinates(2, 1));
    }

    @Test
    public void testGetFirstAvailableSlotForItem() {
        
        // fill up inventory
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                Sword s = new Sword(new SimpleIntegerProperty(i), new SimpleIntegerProperty(j));
                game.addToUnequipped(s);
            }
        }
        // make sure it still generates an item slot
        Pair<Integer, Integer> itemSlot = game.getFirstAvailableSlotForItem();
        assertNotNull(itemSlot);
    }

    @Test
    public void testShiftCardsDownFromXCoordinate() {
       
        ZombiePitCard newCard = new ZombiePitCard(new SimpleIntegerProperty(0), new SimpleIntegerProperty(0));
        int oldX = newCard.getX();
        game.addToCardEntities(newCard);
        game.shiftCardsDownFromXCoordinate(3);
        assertEquals(oldX, newCard.getX());
    }

    @Test
    public void testConvertCardToBuildingEnemyCreator() {
        
        // add a new vampire castle in
        VampireCastleCard vc = new VampireCastleCard(new SimpleIntegerProperty(0), new SimpleIntegerProperty(1));
        assertNotNull(game.convertCardToBuilding(vc, 0, 0, 1));
    }

    @Test
    public void testConvertCardToBuildingRewards() {
       
        Character c = game.getCharacter();
        int oldGold = c.getMoney().getValue();
        // make sure money is given for null buildings
        // such as already having 2 campfires and making another
        Campfire fire = new Campfire(new SimpleIntegerProperty(2), new SimpleIntegerProperty(2));
        Campfire fire1 = new Campfire(new SimpleIntegerProperty(3), new SimpleIntegerProperty(3));
        game.addToBuildingEntities(fire);
        game.addToBuildingEntities(fire1);

        CampfireCard fireCard = new CampfireCard(new SimpleIntegerProperty(2), new SimpleIntegerProperty(2));
        game.convertCardToBuilding(fireCard, 0, 0, 1);
        assertEquals(oldGold + fireCard.getValue(), c.getMoney().getValue());
    }

    @Test
    public void testConvertCardToBuildingASCreator() {
       
        // add a new barracks
        BarracksCard barracks = new BarracksCard(new SimpleIntegerProperty(0), new SimpleIntegerProperty(1));
        assertNotNull(game.convertCardToBuilding(barracks, 0, 0, 1));
    }

    @Test
    public void testGetCardFromPosition() {
        
        // add a new barracks in
        BarracksCard barracks = new BarracksCard(new SimpleIntegerProperty(0), new SimpleIntegerProperty(1));
        game.addToCardEntities(barracks);
        // check both x and y differences
        assertNull(game.getCardFromPosition(0, 2));
        assertNull(game.getCardFromPosition(1, 0));
        assertNull(game.getCardFromPosition(2, 2));
        assertNotNull(game.getCardFromPosition(0, 1));
    }
    

    @Test
    public void testGetEnemiesInBattle() {
       
        PathPosition zombiePath = new PathPosition(1, Arrays.asList(new Pair<>(1, 1), new Pair<>(1, 2)));
        PathPosition vampirePath = new PathPosition(1, Arrays.asList(new Pair<>(1, 1), new Pair<>(1, 2)));

        Vampire v1 = new Vampire(vampirePath);
        game.addEnemy(v1);
        // create a zombie in support radius
        Zombie z = new Zombie(zombiePath);
        game.addEnemy(z);
        
        Character c = game.getCharacter();
        c.setX(1);
        c.setY(1);
        // checks the zombie amd vampire is in battle
        assertEquals(2, game.getEnemiesInBattle().size());

        // move character away
        c.setX(13);
        c.setY(13);
        assertEquals(0, game.getEnemiesInBattle().size());
    }

    @Test
    public void testMoveItemToUnequipped() {
 
        Helmet helmet1 = new Helmet(new SimpleIntegerProperty(1), new SimpleIntegerProperty(1));
        game.addToUnequipped(helmet1);
        game.moveItemToEquippedFromUnequipped(1, 1, "helmet");
        game.moveItemToUnequipped(helmet1);
        assertTrue(game.getUnEquippedItems().contains(helmet1));
    }

    @Test
    public void testGetBuildingOnPath() {

        VampireCastleBuilding vcb = new VampireCastleBuilding(new SimpleIntegerProperty(1), new SimpleIntegerProperty(1));
        game.addToBuildingEntities(vcb);
        assertNull(game.getBuildingOnPath(0, 1));
        assertNull(game.getBuildingOnPath(1, 0));
        assertNull(game.getBuildingOnPath(2, 2));
    }

    @Test
    public void testPickUpItemOnPath() {
 
        Character c = game.getCharacter();
        ArrayList<Item> items = game.possibleSpawnItem();
        Item targetItem = items.get(0);
        c.setX(targetItem.getX());
        c.setY(targetItem.getY());
        assertTrue(game.pickUpItemOnPath().contains(targetItem));
    }

    @Test
    public void testCheckForEnemiesOnTrap() {

        Character c = game.getCharacter();
        c.setX(10);        
        c.setY(10);        

        // make a vampire and move to non-trap
        PathPosition vampirePath = new PathPosition(1, Arrays.asList(new Pair<>(1, 1), new Pair<>(1, 2)));
        Vampire v1 = new Vampire(vampirePath);
        v1.setX(0);
        v1.setY(1);
        game.addEnemy(v1);
        double oldHealth = v1.getHealth();

        // make a random building to confirm that it only cares about traps
        Village barracks = new Village(new SimpleIntegerProperty(0), new SimpleIntegerProperty(1));
        game.addToBuildingEntities(barracks);
        game.checkForEnemiesOnTrap();
        assertEquals(oldHealth, v1.getHealth());

        // make a trap
        Trap trap = new Trap(new SimpleIntegerProperty(1), new SimpleIntegerProperty(1));
        game.addToBuildingEntities(trap);

        // move vampire to trap 
        v1.setX(1);
        v1.setY(1);
        game.checkForEnemiesOnTrap();

        // make sure enemy lost health & trap was destroyed
        assertEquals(oldHealth - trap.getDamage(), v1.getHealth());
        assertFalse(game.getBuildingEntities().contains(trap));

    }

    @Test
    public void testCheckVampireNearCampfire() {

        Character c = game.getCharacter();
        c.setX(10);        
        c.setY(10);        

        // make a vampire and move to non-trap
        PathPosition vampirePath = new PathPosition(1, Arrays.asList(new Pair<>(1, 1), new Pair<>(1, 2)));
        Vampire v1 = new Vampire(vampirePath);
        v1.setX(0);
        v1.setY(1);
        Boolean oldDirection = v1.getCurrentlyMovingUp();
        game.addEnemy(v1);

        // make a random building to confirm that it only cares about campfires
        Village barracks = new Village(new SimpleIntegerProperty(0), new SimpleIntegerProperty(1));
        game.addToBuildingEntities(barracks);
        // make a campfire
        Campfire fire = new Campfire(new SimpleIntegerProperty(1), new SimpleIntegerProperty(1));
        game.addToBuildingEntities(fire);

        game.checkForVampireNearCampfire();
        assertNotEquals(oldDirection, v1.getCurrentlyMovingUp());
    }

    @Test
    public void testDrinkHealthPotion() {
        // create health potion
        // drinkpotion when nulll
        c.setHealth(45);
        game.drinkHealthPotion();
        assertEquals(45, c.getHealth().getValue());
        HealthPotion potion = new HealthPotion(new SimpleIntegerProperty(1), new SimpleIntegerProperty(2));
        game.addToUnequipped(potion);
        // drink potion
        game.drinkHealthPotion();

        // check char health
        assertEquals(65, c.getHealth().getValue());
        // check it's removed
        assertFalse(game.getUnEquippedItems().contains(potion));
    }

    @Test
    public void testIsOnPath() {
        // expect 0, 0 to be on path 
        assertTrue(game.isOnPath(0, 0));
        assertFalse(game.isOnPath(0, 1));
    }

    @Test
    public void testSellShield() {
        int ogMoney = c.getMoney().getValue();
        Shield newItem = new Shield(new SimpleIntegerProperty(1), new SimpleIntegerProperty(1));
        game.addToUnequipped(newItem);
        // sell shield
        game.sellShield();
        assertEquals(game.getUnEquippedItems().size(), 0);
        // check character money icnreased
        assertEquals(ogMoney + newItem.getValue(), c.getMoney().getValue());
    }

    @Test
    public void testSellArmour() {
        int ogMoney = c.getMoney().getValue();
        Armour newItem = new Armour(new SimpleIntegerProperty(1), new SimpleIntegerProperty(1));
        game.addToUnequipped(newItem);
        // sell shield
        game.sellArmour();
        assertEquals(game.getUnEquippedItems().size(), 0);
        // check character money icnreased
        assertEquals(ogMoney + newItem.getValue(), c.getMoney().getValue());
    }

    @Test
    public void testSellSword() {
        int ogMoney = c.getMoney().getValue();
        Sword newItem = new Sword(new SimpleIntegerProperty(1), new SimpleIntegerProperty(1));
        game.addToUnequipped(newItem);
        // sell shield
        game.sellSword();
        assertEquals(game.getUnEquippedItems().size(), 0);
        // check character money icnreased
        assertEquals(ogMoney + newItem.getValue(), c.getMoney().getValue());
    }

    @Test
    public void testSellHelmet() {
        int ogMoney = c.getMoney().getValue();
        Helmet newItem = new Helmet(new SimpleIntegerProperty(1), new SimpleIntegerProperty(1));
        game.addToUnequipped(newItem);
        // sell shield
        game.sellHelmet();
        assertEquals(game.getUnEquippedItems().size(), 0);
        // check character money icnreased
        assertEquals(ogMoney + newItem.getValue(), c.getMoney().getValue());
    }

    @Test
    public void testSellPotion() {
        int ogMoney = c.getMoney().getValue();
        HealthPotion newItem = new HealthPotion(new SimpleIntegerProperty(1), new SimpleIntegerProperty(1));
        game.addToUnequipped(newItem);
        // sell shield
        game.sellHealthPotion();
        assertEquals(game.getUnEquippedItems().size(), 0);
        // check character money icnreased
        assertEquals(ogMoney + newItem.getValue(), c.getMoney().getValue());
    }

    @Test
    public void testSellStaff() {
        int ogMoney = c.getMoney().getValue();
        Staff newItem = new Staff(new SimpleIntegerProperty(1), new SimpleIntegerProperty(1));
        game.addToUnequipped(newItem);
        // sell shield
        game.sellStaff();
        assertEquals(game.getUnEquippedItems().size(), 0);
        // check character money icnreased
        assertEquals(ogMoney + newItem.getValue(), c.getMoney().getValue());
    }

    @Test
    public void testSellStake() {
        int ogMoney = c.getMoney().getValue();
        Stake newItem = new Stake(new SimpleIntegerProperty(1), new SimpleIntegerProperty(1));
        game.addToUnequipped(newItem);
        // sell shield
        game.sellStake();
        assertEquals(game.getUnEquippedItems().size(), 0);
        // check character money icnreased
        assertEquals(ogMoney + newItem.getValue(), c.getMoney().getValue());
    }

    @Test
    public void testBuyArmourEnoughMoney() {
        c.increaseMoney(Armour.getCost());
        Item result = game.buyArmour();
        assertTrue(game.getUnEquippedItems().contains(result));
        assertEquals(0, c.getMoney().getValue());
        
    }

    @Test
    public void testBuyArmourNoMoney() {
        game.buyArmour();
        assertEquals(0, c.getMoney().getValue());
        assertTrue(game.getUnEquippedItems().size() == 0);
    
    }

    @Test
    public void testBuyShieldEnoughMoney() {
        c.increaseMoney(Shield.getCost());
        Item result = game.buyShield();
        assertTrue(game.getUnEquippedItems().contains(result));
        assertEquals(0, c.getMoney().getValue());
        
    }

    @Test
    public void testBuyShieldNoMoney() {
        game.buyShield();
        assertEquals(0, c.getMoney().getValue());
        assertTrue(game.getUnEquippedItems().size() == 0);
    
    }

    @Test
    public void testBuyhelemtEnoughMoney() {
        c.increaseMoney(Helmet.getCost());
        Item result = game.buyHelmet();
        assertTrue(game.getUnEquippedItems().contains(result));
        assertEquals(0, c.getMoney().getValue());
        
    }

    @Test
    public void testBuyHelemtNoMoney() {
        game.buyHelmet();
        assertEquals(0, c.getMoney().getValue());
        assertTrue(game.getUnEquippedItems().size() == 0);
    
    }

    @Test
    public void testBuyStakeEnoughMoney() {
        c.increaseMoney(Stake.getCost());
        Item result = game.buyStake();
        assertTrue(game.getUnEquippedItems().contains(result));
        assertEquals(0, c.getMoney().getValue());
        
    }

    @Test
    public void testBuyStakeNoMoney() {
        game.buyStake();
        assertEquals(0, c.getMoney().getValue());
        assertTrue(game.getUnEquippedItems().size() == 0);
    
    }

    @Test
    public void testBuyStaffEnoughMoney() {
        c.increaseMoney(Staff.getCost());
        Item result = game.buyStaff();
        assertTrue(game.getUnEquippedItems().contains(result));
        assertEquals(0, c.getMoney().getValue());
        
    }

    @Test
    public void testBuyStaffNoMoney() {
        game.buyStaff();
        assertEquals(0, c.getMoney().getValue());
        assertTrue(game.getUnEquippedItems().size() == 0);
    
    }

    @Test
    public void testBuySwordEnoughMoney() {
        c.increaseMoney(Sword.getCost());
        Item result = game.buySword();
        assertTrue(game.getUnEquippedItems().contains(result));
        assertEquals(0, c.getMoney().getValue());
        
    }

    @Test
    public void testBuySwordNoMoney() {
        game.buySword();
        assertEquals(0, c.getMoney().getValue());
        assertTrue(game.getUnEquippedItems().size() == 0);
    
    }

    @Test
    public void testBuyHealthPotionEnoughMoney() {
        c.increaseMoney(HealthPotion.getCost());
        Item result = game.buyHealthPotion();
        assertTrue(game.getUnEquippedItems().contains(result));
        assertEquals(0, c.getMoney().getValue());
        
    }

    @Test
    public void testBuyHealthPotionNoMoney() {
        game.buyHealthPotion();
        assertEquals(0, c.getMoney().getValue());
        assertTrue(game.getUnEquippedItems().size() == 0);
    
    }
    

    @Test
    public void testEnemyCreateSoldier() {
        ZombiePitBuilding z = new ZombiePitBuilding(new SimpleIntegerProperty(1), new SimpleIntegerProperty(1));
        c.setX(0);
        c.setY(0);
        Zombie zombie = z.spawnEnemy(orderedPath, c, false);
        zombie.setIsAlliedSolider(3);
        game.addEnemy(zombie);
        game.possiblyCreateAlliedSoldier();
        // assert that zombie is destroyed
        assertFalse(game.getEnemies().contains(zombie));
    }

    @Test
    public void testspawnEnemiesZombiePit() {
        ZombiePitBuilding z = new ZombiePitBuilding(new SimpleIntegerProperty(1), new SimpleIntegerProperty(1));
        c.setX(0);
        c.setY(0);
        game.addToBuildingEntities(z);
        game.spawnEnemies(false);
        // check size of enemies
        assertTrue(game.getEnemies().size() >= 1);
    }

    @Test
    public void testspawnEnemiesAlliedSoldier() {
        ZombiePitBuilding z = new ZombiePitBuilding(new SimpleIntegerProperty(1), new SimpleIntegerProperty(1));
        c.setX(0);
        c.setY(0);
        Zombie zombie = z.spawnEnemy(orderedPath, c, false);
        zombie.setIsAlliedSolider(3);
        game.addEnemy(zombie);
        ArrayList<AlliedSoldier> soldiers = game.possiblyCreateAlliedSoldier();
        game.spawnEnemies(false);

        assertFalse(game.getAlliedSoldiers().contains(soldiers.get(0)));
        // assert that zombie is destroyed
    }

    @Test
    public void testPossibleSpawnItem() {
        // if less than 2 items on ground
        game.possibleSpawnItem();
        // check that we have 2 items
        assertTrue(game.getGroundItems().size() == 2);
    }

    @Test
    public void testCheckForVillage() {
        Village v = new Village(new SimpleIntegerProperty(0), new SimpleIntegerProperty(0));
        game.addToBuildingEntities(v);
        c.setX(0);
        c.setY(0);
        c.setHealth(50);
        game.checkForVillage();
        assertEquals(50 + v.getIncreaseInHealth(), c.getHealth().getValue());
    }

    @Test
    public void testMoveItemFromRare() {
        RareItem anduril = new Anduril(new SimpleIntegerProperty(0), new SimpleIntegerProperty(0));

        game.addToRareItem(anduril);
        Item res = game.moveItemToEquippedFromRare(0, 0, "weapon");
        assertFalse(game.getRareItems().contains(anduril));
        assertEquals(c.getEquippedWeapon(), anduril);
        assertNull(res);
    }

    @Test
    public void testMoveItemFromRareEjectedNull() {
        RareItem anduril = new Anduril(new SimpleIntegerProperty(0), new SimpleIntegerProperty(0));

        game.addToRareItem(anduril);
        game.moveItemToEquippedFromRare(0, 0, "weapon");
        RareItem anduril2 = new Anduril(new SimpleIntegerProperty(1), new SimpleIntegerProperty(0));
        game.addToRareItem(anduril2);
        Item res = game.moveItemToEquippedFromRare(1, 0, "weapon");

        assertFalse(game.getRareItems().contains(anduril2));
        assertTrue(game.getRareItems().contains(anduril));
        assertEquals(c.getEquippedWeapon(), anduril2);
        assertEquals(res, anduril);
    }

    @Test
    public void testMoveItemFromRareShield() {
        TreeStump stump = new TreeStump(new SimpleIntegerProperty(0), new SimpleIntegerProperty(0));

        game.addToRareItem(stump);
        Item res = game.moveItemToEquippedFromRare(0, 0, "shield");

        assertFalse(game.getRareItems().contains(stump));
        assertNull(res);
        assertTrue(c.getProtectiveGear().contains((Item) stump));
    }

    @Test
    public void testCreateTaxOfficeFirst() {
        c.setTaxPayer(true);
        TaxOffice res = game.createTaxOffice();
        assertNotNull(res);
        assertTrue(game.getBuildingEntities().contains(res));
    }

    @Test
    public void testCreateTaxOfficeSecond() {
        c.setTaxPayer(true);
        game.createTaxOffice();
        TaxOffice res = game.createTaxOffice();
        assertNull(res);
        assertFalse(game.getBuildingEntities().contains(res));
    }

    @Test
    public void testCreateTaxOfficeNotTaxPayer() {
        TaxOffice res = game.createTaxOffice();
        assertNull(res);
        assertFalse(game.getBuildingEntities().contains(res));
    }

    @Test
    public void testReactToWinBattleNoItemsAllowed() {
        // pass 0 so always returns rare item
        RareItem res = game.reactToWinBattle(0);
        assertNull(res);
        assertFalse(game.getRareItems().contains(res));
       
    }

    @Test
    public void testReactToWinBattleItemsAllowed() {
        LoopManiaWorld game = new LoopManiaWorld(3, 3, orderedPath, new AndCompositeGoal(), new ArrayList<>(Arrays.asList("the_one_ring")),
                new BerserkerMode());
        // pass 0 so always returns rare item
        RareItem res = game.reactToWinBattle(0);
        assertNotNull(res);
        assertTrue(game.getRareItems().contains(res));
       
    }

    @Test
    public void testChangeDoggiePriceNoElanIncrease() {
        int currPrice = game.doggieValue.getValue();
        game.changeDoggiePrice(1);
        // should increase price
        assertTrue(game.doggieValue.getValue() >= currPrice);
    }

    @Test
    public void testChangeDoggiePriceNoElanDecrease() {
        int currPrice = game.doggieValue.getValue();
        game.changeDoggiePrice(0.6f);
        // should increase price
        assertTrue(game.doggieValue.getValue() <= currPrice);
    }

    @Test
    public void testChangeDoggiePriceElan() {
        int currPrice = game.doggieValue.getValue();
        Elan elan = new Elan(null);
        game.addEnemy(elan);
        game.changeDoggiePrice(0);
        // should increase price
        assertTrue(game.doggieValue.getValue() > currPrice);
    }

    @Test
    public void testAddTOCardEntitiesTooManyCards() {
        for (int i = 1; i < game.getWidth() + 1; i++) {
            game.addToCardEntities(new VampireCastleCard(new SimpleIntegerProperty(i), new SimpleIntegerProperty(i)));
        }
        // this card should reject the first card
        Card finalCard = new VampireCastleCard(new SimpleIntegerProperty(5), new SimpleIntegerProperty(5));
        Item res = game.addToCardEntities(finalCard);
        assertEquals(game.getWidth() - 1, finalCard.getX());
        assertNotNull(res);

    }

    @Test
    public void testSellDoggie() {
        game.doggieValue.set(50);
        c.increaseDoggieCoin(1);
        game.sellDoggie(1);
        assertEquals(50, c.getMoney().getValue());
        assertEquals(0, c.getDoggieCoins().getValue());

    }

    @Test
    public void testGenerateRandomLessThan60() {
        Item res = game.generateRandomItem(new SimpleIntegerProperty(5), new SimpleIntegerProperty(5), 60);
        assertTrue(res instanceof Sword);

    }

    @Test
    public void testGenerateRandomLessThan75() {
        Item res = game.generateRandomItem(new SimpleIntegerProperty(5), new SimpleIntegerProperty(5), 70);
        assertTrue(res instanceof Stake);

    }

    @Test
    public void testGenerateRandomLessThan90() {
        Item res = game.generateRandomItem(new SimpleIntegerProperty(5), new SimpleIntegerProperty(5), 90);
        assertTrue(res instanceof Helmet);
        res = game.generateRandomItem(new SimpleIntegerProperty(5), new SimpleIntegerProperty(5), 10);
        assertTrue(res instanceof Staff);
        res = game.generateRandomItem(new SimpleIntegerProperty(5), new SimpleIntegerProperty(5), 20);
        assertTrue(res instanceof Armour);
        res = game.generateRandomItem(new SimpleIntegerProperty(5), new SimpleIntegerProperty(5), 35);
        assertTrue(res instanceof Shield);
        

    }

    @Test
    public void testMoveItemFromUnequipped() {

        Armour armour = new Armour(new SimpleIntegerProperty(0), new SimpleIntegerProperty(0));
        game.addToUnequipped(armour);
        Item res = game.moveItemToEquippedFromUnequipped(0, 0,"armour");
        assertFalse(game.getUnEquippedItems().contains(armour));
        assertTrue(game.getCharacter().getProtectiveGear().contains(armour));
        assertNull(res);
    }

    @Test
    public void testMoveItemFromUnequippedEjectedNull() {
        TreeStump stump = new TreeStump(new SimpleIntegerProperty(0), new SimpleIntegerProperty(0));

        game.addToRareItem(stump);
        game.moveItemToEquippedFromRare(0, 0, "shield");
        Shield shield = new Shield(new SimpleIntegerProperty(1), new SimpleIntegerProperty(0));
       game.addToUnequipped(shield);
        Item res = game.moveItemToEquippedFromUnequipped(1, 0, "shield");

        assertTrue(game.getRareItems().contains(stump));
        assertTrue(game.getCharacter().getProtectiveGear().contains(shield));

        assertEquals(res, stump);
    }


}