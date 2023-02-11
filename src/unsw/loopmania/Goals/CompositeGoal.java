package unsw.loopmania.Goals;

import java.util.ArrayList;

import unsw.loopmania.Character;
import unsw.loopmania.GoalComponent;

/**
 * Utilises composite pattern for goal implementation.
 */
public abstract class CompositeGoal implements GoalComponent {
    // ArrayList of simple goals that user needs to achieve
    private ArrayList<GoalComponent> goals = new ArrayList<>();
    
    /**
     * 
     * @param goal to achieve in the World.
     */
    public void addGoal(GoalComponent goal) {
        goals.add(goal);
    }

    /**
     * 
     * @param goal to add to the Composite Goals.
     */
    public void addGoal(CompositeGoal goal) {
        goals.add(goal);
    }

    /**
     * Checks if goal criteria has been met
     */
    public abstract Boolean checkGoalReached(Character c);

    /**
     * Obtains the goal description for respective goal and appends
     * it to our StringBuffer "result".
     */
    public abstract StringBuffer getGoalDescription();

    /**
     * @return goals to achieve
     */
    public ArrayList<GoalComponent> getGoals() {
        return goals;
    }
}