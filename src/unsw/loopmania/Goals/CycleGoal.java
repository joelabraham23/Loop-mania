package unsw.loopmania.Goals;

import unsw.loopmania.Character;
import unsw.loopmania.GoalComponent;

/**
 * Character needs to complete a specified number of cycles of the path.
 */
public class CycleGoal implements GoalComponent {
    // Flag value to complete cycle goal
    private int requiredValue;

    /**
     * @param requiredValue for cycle goal to be considered "complete"
     */
    public CycleGoal(int requiredValue) {
        this.requiredValue = requiredValue;
    }

    /**
     * Checks if character has completed the cycle goal
     */
    public Boolean checkGoalReached(Character c) {
        if (c.getCyclesCompleted().getValue() >= requiredValue) return true;
        return false;
    }

    /**
    * Obtains the cycle goal description for character 
    * to know what to do to complete this goal.
    */
    public StringBuffer getGoalDescription() {
        StringBuffer result = new StringBuffer("Complete " + requiredValue + " cycles "); 
        return result;
    }
}