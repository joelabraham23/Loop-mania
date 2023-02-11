package test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;

import org.javatuples.Pair;
import org.junit.Test;

import unsw.loopmania.*;
import unsw.loopmania.Character;
import unsw.loopmania.Enemies.Boss;
import unsw.loopmania.Enemies.Bosses.Doggie;
import unsw.loopmania.Enemies.Bosses.Elan;
import unsw.loopmania.Goals.*;
import unsw.loopmania.GameModes.*;


public class GoalTests {

    /**
     * world's ordered path
     */
    
    
    private LoopManiaWorld createGame() {
        TestHelper helper = new TestHelper();
        ArrayList<Pair<Integer, Integer>> orderedPath = helper.getOrderedPath();
        LoopManiaWorld game = new LoopManiaWorld(3, 3, orderedPath, new AndCompositeGoal(), new ArrayList<>(),
        new BerserkerMode());
        return game;
    }

    @Test
    public void testExperienceGoal()  {
        // create a new experience goal 
        LoopManiaWorld game = createGame();
        Character c = game.getCharacter();
        GoalComponent goal = new ExperienceGoal(100);
        assertFalse(goal.checkGoalReached(c));
        // increase character's expereince 
        c.increaseExperience(100);
        assertTrue(goal.checkGoalReached(c));
        // test getting goal description
        assertEquals("Get 100 experience ", goal.getGoalDescription().toString());
    }

    @Test
    public void testMoneyGoal()  {
        // create a new experience goal 
        LoopManiaWorld game = createGame();
        Character c = game.getCharacter();
        GoalComponent goal = new MoneyGoal(100);
        assertFalse(goal.checkGoalReached(c));
        // increase character's expereince 
        c.increaseMoney(100);
        assertTrue(goal.checkGoalReached(c));
        // test getting goal description
        assertEquals("Get 100 gold ", goal.getGoalDescription().toString());
    }

    @Test
    public void testCycleGoal()  {
        LoopManiaWorld game = createGame();
        Character c = game.getCharacter();
        // create a new experience goal 
        GoalComponent goal = new CycleGoal(100);
        assertFalse(goal.checkGoalReached(c));
        // increase character's expereince 
        for (int i = 0; i < 100; i++) {
            c.increaseCyclesCompleted();
        }
        assertTrue(goal.checkGoalReached(c));
        // test getting goal description
        assertEquals("Complete 100 cycles ", goal.getGoalDescription().toString());
    }

    @Test
    public void testCompositeGoalSimpleAND()  {
        LoopManiaWorld game = createGame();
        Character c = game.getCharacter();
        // goal is get 100 cycles AND 50 gold
        GoalComponent goal = new CycleGoal(100);
        GoalComponent goal2 = new MoneyGoal(50);
        
        CompositeGoal result = new AndCompositeGoal();
        result.addGoal(goal);
        result.addGoal(goal2);
        // goal not reached yet
        assertFalse(result.checkGoalReached(c));
        // increase character's expereince 
        for (int i = 0; i < 100; i++) {
            c.increaseCyclesCompleted();
        }
        assertFalse(result.checkGoalReached(c));
        // also increase money
        c.increaseMoney(50);
        assertTrue(result.checkGoalReached(c));
        // test getting goal description
        assertEquals("Complete 100 cycles AND Get 50 gold ", result.getGoalDescription().toString());
    }

    @Test
    public void testCompositeGoalSimpleOR()  {
        LoopManiaWorld game = createGame();
        Character c = game.getCharacter();
        // goal is get 100 cycles AND 50 gold
        GoalComponent goal = new CycleGoal(100);
        GoalComponent goal2 = new MoneyGoal(50);
        
        CompositeGoal result = new OrCompositeGoal();
        result.addGoal(goal);
        result.addGoal(goal2);
        // goal not reached yet
        assertFalse(result.checkGoalReached(c));
        // increase character's expereince 
        for (int i = 0; i < 100; i++) {
            c.increaseCyclesCompleted();
        }
        assertTrue(result.checkGoalReached(c));
        // test getting goal description
        assertEquals("Complete 100 cycles OR Get 50 gold ", result.getGoalDescription().toString());
    }

    @Test
    public void testCompositeGoalSimpleSINGLE()  {
        LoopManiaWorld game = createGame();
        Character c = game.getCharacter();
        // goal is get 100 cycles AND 50 gold
        GoalComponent goal = new MoneyGoal(50);
        
        CompositeGoal result = new AndCompositeGoal();
        result.addGoal(goal);
        // goal not reached yet
        assertFalse(result.checkGoalReached(c));
        c.increaseMoney(50);
        assertTrue(result.checkGoalReached(c));
        // test getting goal description
        assertEquals("Get 50 gold ", result.getGoalDescription().toString());
    }

    @Test
    public void test3Ors()  {
        // (get 50 gold or 50 experience or 100 cycles)
        LoopManiaWorld game = createGame();
        Character c = game.getCharacter();
        // goal is get 100 cycles AND 50 gold
        GoalComponent goal = new MoneyGoal(50);
        GoalComponent goal2 = new ExperienceGoal(50);
        GoalComponent goal3 = new CycleGoal(100);
        
        CompositeGoal result = new OrCompositeGoal();
        result.addGoal(goal);
        result.addGoal(goal2);
        result.addGoal(goal3);
        // goal not reached yet
        assertFalse(result.checkGoalReached(c));
        c.increaseMoney(50);
        assertTrue(result.checkGoalReached(c));
        // test getting goal description
        assertEquals("Get 50 gold OR Get 50 experience OR Complete 100 cycles ", result.getGoalDescription().toString());
    }

    @Test
    public void testGoalinGoal()  {
        // (get 50 gold or 50 experience) and 100 cycles)
        LoopManiaWorld game = createGame();
        Character c = game.getCharacter();
        // goal is get 100 cycles AND 50 gold
        GoalComponent goal = new MoneyGoal(50);
        GoalComponent goal2 = new ExperienceGoal(50);
        GoalComponent goal3 = new CycleGoal(100);
        
        CompositeGoal result1 = new OrCompositeGoal();
        result1.addGoal(goal);
        result1.addGoal(goal2);
        CompositeGoal result2 = new AndCompositeGoal();
        result2.addGoal(goal3);
        result2.addGoal(result1);

        assertFalse(result2.checkGoalReached(c));
        c.increaseMoney(50);
        assertFalse(result2.checkGoalReached(c));
        // get 100 cycles
        for (int i = 0; i < 100; i++) {
            c.increaseCyclesCompleted();
        }
        assertTrue(result2.checkGoalReached(c));

        // test getting goal description
        assertEquals("Complete 100 cycles AND Get 50 gold OR Get 50 experience ", result2.getGoalDescription().toString());
    }

    @Test
    public void testBossGoalFail()  {
        LoopManiaWorld game = createGame();
        Character c = game.getCharacter();
        ArrayList<String> allBosses  =new ArrayList<>(Arrays.asList("Elan"));
        GoalComponent goal = new KillBosses(allBosses);
        assertFalse(goal.checkGoalReached(c));
    }

    @Test
    public void testBossGoalSuccessOne()  {
        Boss boss1 = new Elan(null);
        LoopManiaWorld game = createGame();
        Character c = game.getCharacter();
        c.addBossToKilled(boss1);
        ArrayList<String> allBosses  =new ArrayList<>(Arrays.asList("Elan"));
        GoalComponent goal = new KillBosses(allBosses);
        assertTrue(goal.checkGoalReached(c));
    }

    @Test
    public void testBossGoalSuccessTwo()  {
        Boss boss1 = new Elan(null);
        Boss boss2 = new Elan(null);
        LoopManiaWorld game = createGame();
        Character c = game.getCharacter();
        c.addBossToKilled(boss1);
        c.addBossToKilled(boss2);
        ArrayList<String> allBosses  =new ArrayList<>(Arrays.asList("Elan"));
        GoalComponent goal = new KillBosses(allBosses);
        assertTrue(goal.checkGoalReached(c));
    }

    @Test
    public void testBossGoalSuccessThreeFail()  {
        Boss boss1 = new Elan(null);
        Boss boss2 = new Elan(null);
        LoopManiaWorld game = createGame();
        Character c = game.getCharacter();
        c.addBossToKilled(boss1);
        c.addBossToKilled(boss2);
        ArrayList<String> allBosses  =new ArrayList<>(Arrays.asList("Elan", "Doggie"));
        GoalComponent goal = new KillBosses(allBosses);
        assertFalse(goal.checkGoalReached(c));
    }

    @Test
    public void testBossGoalSuccessThreeSuccess()  {
        Boss boss1 = new Elan(null);
        Boss boss2 = new Elan(null);
        Boss boss3 = new Doggie(null);
        LoopManiaWorld game = createGame();
        Character c = game.getCharacter();
        c.addBossToKilled(boss1);
        c.addBossToKilled(boss2);
        c.addBossToKilled(boss3);
        ArrayList<String> allBosses  =new ArrayList<>(Arrays.asList("Elan", "Doggie"));
        GoalComponent goal = new KillBosses(allBosses);
        assertTrue(goal.checkGoalReached(c));
    }

    @Test
    public void testBossGoalDescription()  {
        
        ArrayList<String> allBosses  =new ArrayList<>(Arrays.asList("Elan", "Doggie"));
        GoalComponent goal = new KillBosses(allBosses);
        assertEquals("Kill all bosses ", goal.getGoalDescription().toString());
    }

}