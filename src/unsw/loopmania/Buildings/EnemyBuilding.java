package unsw.loopmania.Buildings;

import java.util.List;

import org.javatuples.Pair;

import javafx.beans.property.SimpleIntegerProperty;
import unsw.loopmania.Building;
import unsw.loopmania.PathPosition;

public abstract class EnemyBuilding extends Building {

    public EnemyBuilding(SimpleIntegerProperty x, SimpleIntegerProperty y, String entityImage) {
        super(x, y, entityImage);
    }

    /**
     * Since enemy buildings are not on path, need to get the closest
     * path tile to where the enemy building is (ie adjacent)
     * @param orderedPath walkway path for creatures
     * @return index in ordered path adjacent to building
     */
    public int getClosestPosition(List<Pair<Integer, Integer>> orderedPath) {        
        // get closest position to path (x, y + 1)
        int indexPosition = orderedPath.indexOf(new Pair<Integer, Integer>(this.getX(), this.getY()));
        if (indexPosition == -1) {
            // check for other places
            for (int x = this.getX() - 1; x <= this.getX() + 1; x++) {
                for (int y = this.getY() - 1; y <= this.getY() + 1; y++) {
                    indexPosition = orderedPath.indexOf(new Pair<Integer, Integer>(x, y));
                    if (indexPosition != -1) {
                        return indexPosition;
                    }
                }
            }
        }
        return indexPosition;
    }

    /**
     * Get path of the to be created enemy
     * @param orderedPath walkway path for creatures
     * @return path for new enemy
     */
    public PathPosition getPathPosition(List<Pair<Integer, Integer>> orderedPath) {
        int indexPosition = getClosestPosition(orderedPath);
        return new PathPosition(indexPosition, orderedPath);
    }

}