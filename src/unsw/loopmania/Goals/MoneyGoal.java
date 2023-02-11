package unsw.loopmania.Goals;

import unsw.loopmania.Character;
import unsw.loopmania.GoalComponent;

/**
 * Character needs to amass a specified amount of gold.
 */
public class MoneyGoal implements GoalComponent {
    // Flag value to complete gold goal
    private int requiredValue;

    /**
     * @param requiredValue for money/gold goal to be considered "complete"
     */
    public MoneyGoal(int requiredValue) {
        this.requiredValue = requiredValue;
    }

    /**
     * Checks if character has completed the money/gold goal
     */
    public Boolean checkGoalReached(Character c) {
        if (c.getMoney().getValue() >= requiredValue) return true;
        return false;
    }

    /**
     * Obtains the money/gold goal description for character 
     * to know what to do to complete this goal.
     */
    public StringBuffer getGoalDescription() {
        StringBuffer result = new StringBuffer("Get " + requiredValue + " gold "); 
        return result;
    }
}