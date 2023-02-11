package test;

import unsw.loopmania.Item;
import unsw.loopmania.Building;
import unsw.loopmania.Buildings.*;
import unsw.loopmania.Cards.BarracksCard;
import unsw.loopmania.Cards.CampfireCard;
import unsw.loopmania.Cards.TowerCard;
import unsw.loopmania.Cards.TrapCard;
import unsw.loopmania.Cards.VampireCastleCard;
import unsw.loopmania.Cards.VillageCard;
import unsw.loopmania.Cards.ZombiePitCard;
import unsw.loopmania.Items.*;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;

import org.junit.Test;

public class CardTests {
    ArrayList<Building> buildings = new ArrayList<Building>();

    ///// TESTING CORRECT BUILDING ON CORRECT CARD
    @Test
    public void testVampCastleCardCorrectBuildingCreated() {
        buildings.clear();
        VampireCastleCard castle = new VampireCastleCard(null, null);
        Building building = castle.createBuilding(null, null, buildings);
        assertTrue(building instanceof VampireCastleBuilding);
    }

    @Test
    public void testBarracksCardCorrectBuildingCreated() {
        buildings.clear();
        BarracksCard barracks = new BarracksCard(null, null);
        Building building = barracks.createBuilding(null, null, buildings);
        assertTrue(building instanceof Barracks);
    }

    @Test
    public void testCampfireCardCorrectBuildingCreated() {
        buildings.clear();
        CampfireCard campfire = new CampfireCard(null, null);
        Building building = campfire.createBuilding(null, null, buildings);
        assertTrue(building instanceof Campfire);
    }

    @Test
    public void testTowerCardCorrectBuildingCreated() {
        buildings.clear();
        TowerCard tower = new TowerCard(null, null);
        Building building = tower.createBuilding(null, null, buildings);
        assertTrue(building instanceof TowerBuilding);
    }

    @Test
    public void testTrapCardCorrectBuildingCreated() {
        buildings.clear();
        TrapCard trap = new TrapCard(null, null);
        Building building = trap.createBuilding(null, null, buildings);
        assertTrue(building instanceof Trap);
    }

    @Test
    public void testVillageCardCorrectBuildingCreated() {
        buildings.clear();
        VillageCard village = new VillageCard(null, null);
        Building building = village.createBuilding(null, null, buildings);
        assertTrue(building instanceof Village);
    }

    @Test
    public void testZombiePitCardCorrectBuildingCreated() {
        buildings.clear();
        ZombiePitCard zombiePit = new ZombiePitCard(null, null);
        Building building = zombiePit.createBuilding(null, null, buildings);
        assertTrue(building instanceof ZombiePitBuilding);
    }

    //// TESTING CARDS WITH LIMITS
    @Test
    public void testTrapCardCorrectBuildingLimits() {
        // prep buildings by adding 2 traps + 1 random
        buildings.clear();
        buildings.add(new Trap(null, null));
        buildings.add(new Trap(null, null));
        buildings.add(new Campfire(null, null));

        // make sure an extra building can be added
        // checking only building that impacts limit is traps
        TrapCard trap = new TrapCard(null, null);
        Building building1 = trap.createBuilding(null, null, buildings);
        buildings.add(building1);
        assertNotNull(building1);

        // check now that at trap limit, another cannot be made
        Building building2 = trap.createBuilding(null, null, buildings);
        assertNull(building2);
    }

    @Test
    public void testTowerCardCorrectBuildingLimits() {
        // prep buildings by adding 2 towers + 1 random
        buildings.clear();
        buildings.add(new TowerBuilding(null, null));
        buildings.add(new TowerBuilding(null, null));
        buildings.add(new Campfire(null, null));

        // make sure an extra building can be added
        // checking only building that impacts limit is towers
        TowerCard tower = new TowerCard(null, null);
        Building building1 = tower.createBuilding(null, null, buildings);
        buildings.add(building1);
        assertNotNull(building1);

        // check now that at tower limit, another cannot be made
        Building building2 = tower.createBuilding(null, null, buildings);
        assertNull(building2);
    }

    @Test
    public void testBarracksCardCorrectBuildingLimits() {
        // prep buildings by adding 1 barrack + 1 random
        buildings.clear();
        buildings.add(new Barracks(null, null));
        buildings.add(new Campfire(null, null));

        // make sure an extra building can be added
        // checking only building that impacts limit is barracks
        BarracksCard barracks = new BarracksCard(null, null);
        Building building1 = barracks.createBuilding(null, null, buildings);
        buildings.add(building1);
        assertNotNull(building1);

        // check now that at barracks limit, another cannot be made
        Building building2 = barracks.createBuilding(null, null, buildings);
        assertNull(building2);
    }

    @Test
    public void testCampfireCardCorrectBuildingLimits() {
        buildings.clear();
        // prep buildings by adding 1 campfires + one random
        buildings.add(new Campfire(null, null));
        buildings.add(new Barracks(null, null));

        // make sure an extra building can be added
        // checking only building that impacts limit is campfire
        CampfireCard fire = new CampfireCard(null, null);
        Building building1 = fire.createBuilding(null, null, buildings);
        buildings.add(building1);
        assertNotNull(building1);

        // check now that at campfire limit, another cannot be made
        Building building2 = fire.createBuilding(null, null, buildings);
        assertNull(building2);
    }

    //// TESTING CARD ITEM REWARDS ARE OF THE RIGHT TYPE

    @Test
    public void testTrapCardCorrectRewards() {
        // make sure correct reward is given
        TrapCard trap = new TrapCard(null, null);
        Item rewards = trap.giveItemReward(null, null);
        assertTrue(rewards instanceof Armour);
    }

    @Test
    public void testTowerCardCorrectRewards() {
        // make sure correct reward is given
        TowerCard tower = new TowerCard(null, null);
        Item rewards = tower.giveItemReward(null, null);
        assertTrue(rewards instanceof Shield);
    }

    @Test
    public void testBarracksCardCorrectRewards() {
       // make sure correct reward is given
       BarracksCard barracks = new BarracksCard(null, null);
       Item rewards = barracks.giveItemReward(null, null);
       assertTrue(rewards instanceof Staff);
    }

    @Test
    public void testCampfireCardCorrectRewards() {
        // make sure correct reward is given
        CampfireCard fire = new CampfireCard(null, null);
        Item rewards = fire.giveItemReward(null, null);
        assertTrue(rewards instanceof Helmet);
    }

    @Test
    public void testVillageCardCorrectRewards() {
        // make sure correct reward is given
        VillageCard village = new VillageCard(null, null);
        Item rewards = village.giveItemReward(null, null);
        assertTrue(rewards instanceof HealthPotion);
    }

    @Test
    public void testZombiePitCardCorrectRewards() {
        // make sure correct reward is given
        ZombiePitCard zpit = new ZombiePitCard(null, null);
        Item rewards = zpit.giveItemReward(null, null);
        assertTrue(rewards instanceof Stake);
    }

    //// TEST CORRECT CAN BE DROPPED ON PATH

    @Test
    public void testVampCastleCardCorrectDroppedLocation() {
        VampireCastleCard castle = new VampireCastleCard(null, null);
        assertFalse(castle.canBeDroppedOnPath());
    }

    @Test
    public void testBarracksCardCorrectDroppedLocation() {
        BarracksCard barracks = new BarracksCard(null, null);
        assertTrue(barracks.canBeDroppedOnPath());
    }

    @Test
    public void testCampfireCardCorrectDroppedLocation() {
        CampfireCard campfire = new CampfireCard(null, null);
        assertFalse(campfire.canBeDroppedOnPath());
    }

    @Test
    public void testTowerCardCorrectDroppedLocation() {
        TowerCard tower = new TowerCard(null, null);
        assertFalse(tower.canBeDroppedOnPath());
    }

    @Test
    public void testTrapCardCorrectDroppedLocation() {
        TrapCard trap = new TrapCard(null, null);
        assertTrue(trap.canBeDroppedOnPath());
    }

    @Test
    public void testVillageCardCorrectDroppedLocation() {
        VillageCard village = new VillageCard(null, null);
        assertTrue(village.canBeDroppedOnPath());
    }

    @Test
    public void testZombiePitCardCorrectDroppedLocation() {
        ZombiePitCard zombiePit = new ZombiePitCard(null, null);
        assertFalse(zombiePit.canBeDroppedOnPath());
    }

}
