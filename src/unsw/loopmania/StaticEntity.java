package unsw.loopmania;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;


/**
 * represents a non-moving entity
 * unlike the moving entities, this can be placed anywhere on the game map
 */
public abstract class StaticEntity extends Entity {
    /**
     * x and y coordinates represented by IntegerProperty, so ChangeListeners can be added
     */
    private IntegerProperty x, y;
    private String entityImage;

    public StaticEntity(SimpleIntegerProperty x, SimpleIntegerProperty y, String entityImage) {
        this.x = x;
        this.y = y;
        this.entityImage = entityImage;
    }

    public IntegerProperty x() {
        return x;
    }

    public IntegerProperty y() {
        return y;
    }

    public int getX() {
        return x().get();
    }

    public int getY() {
        return y().get();
    }

    public void setX(IntegerProperty x) {
        this.x = x;
    }
    
    public void setY(IntegerProperty y) {
        this.y = y;
    }

    public String getEntityImageString() {
        return entityImage;
    }

}
