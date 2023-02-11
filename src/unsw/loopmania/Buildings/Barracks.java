package unsw.loopmania.Buildings;

import java.util.ArrayList;
import java.util.List;

import org.javatuples.Pair;

import javafx.beans.property.SimpleIntegerProperty;
import unsw.loopmania.*;
import unsw.loopmania.Character;

/**
 * Produces allied soldier to join Character when passes through
 */
public class Barracks extends Building implements AlliedSoldierCreator {
    // Maximum allied soldiers that can accompany the character
    static final private int MAX_NUMBER_ALLIED_SOLDIERS = 4;

    public Barracks(SimpleIntegerProperty x, SimpleIntegerProperty y) {
        super(x, y, "src/images/barracks.png");
    }

    /**
     * Barracks produces an ally soldier
     */
    public AlliedSoldier produceAlliedSolider(Character c, List<Pair<Integer, Integer>> orderedPath, int numberOfAlliedSoldiers) {
        if (numberOfAlliedSoldiers < MAX_NUMBER_ALLIED_SOLDIERS && c.getX() == this.getX() && c.getY() == this.getY()) {
            // want soldier to be one step behind character
            List<Pair<Integer, Integer>> orderedPathSpawnCandidates = new ArrayList<>();
            int indexPosition = orderedPath.indexOf(new Pair<Integer, Integer>(c.getX(), c.getY()));
            Pair<Integer, Integer> path = orderedPath.get((indexPosition)%orderedPath.size());
            if (indexPosition > 0) {
                // if index position is invalid, wrap around
                path = orderedPath.get((indexPosition - 1)%orderedPath.size());
            }
            orderedPathSpawnCandidates.add(path);
            // get first spawn candidate
            Pair<Integer, Integer> spawnPosition = orderedPathSpawnCandidates.get(0);
            int indexInPath = orderedPath.indexOf(spawnPosition);
            
            PathPosition soldierPath = new PathPosition(indexInPath, orderedPath);
            AlliedSoldier newSoldier = new AlliedSoldier(soldierPath);
            // move one step in reverse so that not on same block as character
            return newSoldier;
        }
        return null;
    }
}