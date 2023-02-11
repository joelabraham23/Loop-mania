package unsw.loopmania;

import javafx.beans.property.SimpleIntegerProperty;

/**
 * The moving entity
 */
public abstract class MovingEntity extends Entity {

    /**
     * object holding position in the path
     */
    private PathPosition position;

    /**
     * imaeg representing the entity
     */
    private String entityImage;

    /**
     * keep track of which way an entity is moving
     */
    private Boolean currentlyMovingUp = true;


    /**
     * Create a moving entity which moves up and down the path in position
     * @param position represents the current position in the path
     */
    public MovingEntity(PathPosition position, String entityImage) {
        this.position = position;
        this.entityImage = entityImage;
    }

    /**
     * move clockwise through the path
     */
    public void moveDownPath() {
        position.moveDownPath();
        currentlyMovingUp = false;
    }

    /**
     * move anticlockwise through the path
     */
    public void moveUpPath() {
        position.moveUpPath();
        currentlyMovingUp = true;
    }

    /**
     * if  moving up then keep moving up
     * else keep moving down
     */
    public void continueInPath() {
        if (currentlyMovingUp == null) {
            position.moveUpPath();
            currentlyMovingUp = true;
        } else if (currentlyMovingUp) {
            position.moveUpPath();
            currentlyMovingUp = true;
        } else {
            position.moveDownPath();
            currentlyMovingUp = false;
        }
    }

    /**
     * if moving up then start moving down
     * if moving down start moving up
     * if not yet moving then don't move
     */
    public void reversePath() {
        if (currentlyMovingUp == null) {
            currentlyMovingUp = false;
        } else if (currentlyMovingUp) {
            position.moveDownPath();
            currentlyMovingUp = false;
        } else {
            position.moveUpPath();
            currentlyMovingUp = true;
        }
    }

    public SimpleIntegerProperty x() {
        return position.getX();
    }

    public PathPosition getEntityPath() {
        return position;
    }

    public Boolean getCurrentlyMovingUp() {
        return currentlyMovingUp;
    }

    public SimpleIntegerProperty y() {
        return position.getY();
    }

    public int getX() {
        return x().get();
    }

    public int getY() {
        return y().get();
    }
    public void setX(int x) {
        x().setValue(x);
    }

    public void setY(int y) {
        y().setValue(y);
    }

    public String getEntityImageString() {
        return entityImage;
    }
}
