package unsw.loopmania;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.javatuples.Pair;

import unsw.loopmania.Enemies.Slug;

public class SlugCreator implements EnemyCreator {

    private Random rand = new Random();

    /**
     * get a randomly generated position which could be used to spawn a slug
     * @param choice randomly generated to decide if a positioon is returned or not
     * @return null if random choice is that wont be spawning an enemy or it isn't possible, or random coordinate pair if should go ahead
     */
    public Pair<Integer, Integer> possiblyGetFreePosition(List<Pair<Integer, Integer>> orderedPath, Character character, int choice){
        if (choice == 0) {
            List<Pair<Integer, Integer>> orderedPathSpawnCandidates = new ArrayList<>();
            int indexPosition = orderedPath.indexOf(new Pair<Integer, Integer>(character.getX(), character.getY()));
            // inclusive start and exclusive end of range of positions not allowed
            int startNotAllowed = (indexPosition - 2 + orderedPath.size())%orderedPath.size();
            int endNotAllowed = (indexPosition + 3)%orderedPath.size();
            // note terminating condition has to be != rather than < since wrap around...
            for (int i=endNotAllowed; i!=startNotAllowed; i=(i+1)%orderedPath.size()){
                orderedPathSpawnCandidates.add(orderedPath.get(i));
            }
    
            // choose random choice
            Pair<Integer, Integer> spawnPosition = orderedPathSpawnCandidates.get(rand.nextInt(orderedPathSpawnCandidates.size()));
            return spawnPosition;
        }
        return null;
    }

    /**
     * 1/15 chance a slug will be created
     */
    @Override
    public BasicEnemy spawnEnemy(List<Pair<Integer, Integer>> orderedPath, Character character, Boolean inBattle) {
        Pair<Integer, Integer> pos = possiblyGetFreePosition(orderedPath, character, rand.nextInt(15));
        if (pos != null) {
            int indexInPath = orderedPath.indexOf(pos);
            Slug enemy = new Slug(new PathPosition(indexInPath, orderedPath));
            return enemy;
        }
        return null;
    }
    
}