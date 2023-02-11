package unsw.loopmania.Goals;

import java.util.ArrayList;

import unsw.loopmania.Character;
import unsw.loopmania.GoalComponent;
import unsw.loopmania.Enemies.Boss;
import unsw.loopmania.Enemies.Bosses.Doggie;
import unsw.loopmania.Enemies.Bosses.Elan;

/**
 * Character needs to obtain a specified level of experience.
 */
public class KillBosses implements GoalComponent {
    // Flag value to complete experience goal
    private ArrayList<String> bossTypes;

    /**
     * @param bossTypes types of bosses that exist, must kill all to compolete goal
     */
    public KillBosses(ArrayList<String> bossTypes) {
        this.bossTypes = bossTypes;
    }

    /**
     * Checks if character has completed the experience goal
     */
    public Boolean checkGoalReached(Character c) {
        // get copy to not modify original array
        ArrayList<String> bossType = new ArrayList<>(bossTypes);
        for (Boss b: c.getBossesKilled()) {
            if (b instanceof Elan) {
                // remove elan from boss types
                bossType.remove("Elan");
            } else if (b instanceof Doggie) {
                bossType.remove("Doggie");
            }
        }
        if (bossType.size() == 0) return true;
        return false;
    }

    /**
     * Obtains the experience goal description for character 
     * to know what to do to complete this goal.
     */
    public StringBuffer getGoalDescription() {
        StringBuffer result = new StringBuffer("Kill all bosses "); 
        return result;
    }
}