package unsw.loopmania;

import java.util.List;

import org.javatuples.Pair;

/**
 * intface part of the observer/observable method
 * for creating new allied soldiers
 */
public interface AlliedSoldierCreator {
    /**
     * create a new allied soldier that walks close to character
     * @param c character of the game 
     * @param orderedPath the path charcters can wlak in game
     * @param numberAlliedSoldiers number of existing (to keep them capped at 4)
     * @return null if conditions do not permit a new soldier
     */
    AlliedSoldier produceAlliedSolider(Character c, List<Pair<Integer, Integer>> orderedPath, int numberOfAlliedSoldiers);
}