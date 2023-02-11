package unsw.loopmania;

/**
 * Goal interface to access the goals.
 */
public interface GoalComponent {

    /**
     * Based on the character's existing stats, check
     * if the character has achieved the goal.
     * 
     * @param character main character
     * @return true if goal achieved or false otherwise.
     */
    Boolean checkGoalReached(Character character);

    /**
     * The goal description for the character to know what to do!
     */
    StringBuffer getGoalDescription();

}