package unsw.loopmania.Goals;

import unsw.loopmania.Character;
import unsw.loopmania.GoalComponent;

/**
 * Character needs to obtain a specified level of experience.
 */
public class ExperienceGoal implements GoalComponent {
    // Flag value to complete experience goal
    private int requiredValue;

    /**
     * @param requiredValue for experience goal to be considered "complete"
     */
    public ExperienceGoal(int requiredValue) {
        this.requiredValue = requiredValue;
    }

    /**
     * Checks if character has completed the experience goal
     */
    public Boolean checkGoalReached(Character c) {
        if (c.getExperience().getValue() >= requiredValue) return true;
        return false;
    }

    /**
     * Obtains the experience goal description for character 
     * to know what to do to complete this goal.
     */
    public StringBuffer getGoalDescription() {
        StringBuffer result = new StringBuffer("Get " + requiredValue + " experience "); 
        return result;
    }
}