package unsw.loopmania.Goals;

import java.util.ArrayList;

import unsw.loopmania.Character;
import unsw.loopmania.GoalComponent;

/**
 * Utilises composite pattern for goal implementation.
 */
public class OrCompositeGoal extends CompositeGoal {

    public OrCompositeGoal() {}

    
    /**
     * Checks if goal criteria has been met
     */
    public Boolean checkGoalReached(Character c) {
        for (GoalComponent g: this.getGoals()) {
            if (g.checkGoalReached(c)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Obtains the goal description for respective goal and appends
     * it to our StringBuffer "result".
     */
    public StringBuffer getGoalDescription() {
        StringBuffer result = new StringBuffer(""); 
        ArrayList<GoalComponent> goals = this.getGoals();
        // Need to know which - check relationship
        for (int i = 0; i < goals.size(); i++) {
            GoalComponent g = goals.get(i);
            result.append(g.getGoalDescription());
            // Append to the last goal
            if (i != goals.size() - 1) {
                result.append( "OR ");
            }
        }
        return result;
    }
}