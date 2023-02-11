package unsw.loopmania.Frontend;

import java.io.File;

import java.util.List;

import org.javatuples.Pair;

import javafx.animation.Animation;
import javafx.geometry.Rectangle2D;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.util.Duration;
import unsw.loopmania.MovingEntity;
import unsw.loopmania.PathPosition;

public abstract class MovingAnimatedEntity extends MovingEntity {

    private ImageView currentImageView;
    private Animation currAnimation;

    // magic numbers for animation
    static final private int SPRITE_HEIGHT = 32;
    static final private int SPRITE_WIDTH = 32;
    static final private int ANIMATION_DURATION = 1000;
    static final private int SPRITE_SHEET_OFFSET_Y = 0;
    
    public MovingAnimatedEntity(PathPosition position, String entityImage, int spriteSheetOffsetX, 
                                int spriteSheetColumns, int spriteSheetCount) {
        super(position, entityImage);
        try {
            // set the default to be the entity string image
            this.currentImageView = new ImageView(turnStringToImage(entityImage));
            getCurrentImageView().setViewport(new Rectangle2D(this.getX(), this.getY(), SPRITE_WIDTH, SPRITE_HEIGHT));
            this.currAnimation = new SpriteAnimation(
                    getCurrentImageView(),
                    Duration.millis(ANIMATION_DURATION),
                    spriteSheetCount, spriteSheetColumns,
                    spriteSheetOffsetX, SPRITE_SHEET_OFFSET_Y,
                    SPRITE_WIDTH, SPRITE_HEIGHT
            );
        } catch(Exception e) {
            // catch needed because JavaFX not initliased in backend testing
        }
    }

    /**
     * Run the animation with current image view
     */
    public void startAnimation() {
        ((SpriteAnimation) this.currAnimation).setImageView(getCurrentImageView());
        this.currAnimation.setCycleCount(Animation.INDEFINITE);
        this.currAnimation.play();
    }

    /**
     * Updates image view based on entity + direction
     * For character, it also chooses attack or walk based on inactivebattle
     * @param orderedPath game path
     * @param inActiveBattle whether the character is actively battling or not
     * @param nextIndexPosition where the entity will be on next frame
     * @param path the path to the sprite sheet
     */
    public void updateImageView(List<Pair<Integer, Integer>> orderedPath, Boolean inActiveBattle, int nextIndexPosition, String path) {
        int currX = getX();
        int currY = getY();

        Pair<Integer, Integer> nextPosition = orderedPath.get((nextIndexPosition) % orderedPath.size());
        int nextX = nextPosition.getValue0();
        int nextY = nextPosition.getValue1();

        String pathDirection = "";

        if (currX == nextX) {
            // we are moving up or down next
            if (nextY == currY + 1) { pathDirection = "down"; } 
            else { pathDirection = "up"; }
        } else {
            // we are moving left or right next
            if (nextX == currX + 1) { pathDirection = "right"; }
            else { pathDirection = "left"; }
        }

        // update to the correct animation image
        if (inActiveBattle && path.equals("char")) {
            setCurrentImageView(path + pathDirection + "attack");
        } else {
            setCurrentImageView(path + pathDirection + "walk");
        }
        // run animation
        startAnimation();
    }

    /**
     * This chooses a position based on currentlyMovingUp
     * Used for slugs and vampires
     * @param orderedPath game path
     * @return index of next position
     */
    public int getNextIndexPositionBothDirections(List<Pair<Integer, Integer>> orderedPath) {
        int indexPosition = orderedPath.indexOf(new Pair<Integer, Integer>(getX(), getY()));
        if (indexPosition == 0) { indexPosition = orderedPath.size(); }
        // choose index based on position
        if (getCurrentlyMovingUp()) { 
            indexPosition--; 
        } else {
            indexPosition++;
        }
        return indexPosition;
    }

    /**
     * Get the next position for a clockwise travelling entity
     * @param orderedPath
     * @return next index position
     */
    public int getNextIndexPositionClock(List<Pair<Integer, Integer>> orderedPath, boolean goingClockwise) {
        // find out what coords come next
        int indexPosition = orderedPath.indexOf(new Pair<Integer, Integer>(getX(), getY()));
        if (indexPosition == 0) { indexPosition = orderedPath.size(); }
        if (goingClockwise) {
            indexPosition++;
        } else {
            indexPosition--;
        }
        
        return indexPosition;
    }

    /**
     * This will update the animation of an entity
     * @param orderedPath game world path to prep next animation
     * @param inActiveBattle whether or not a fighting animation is needed
     */
    public abstract void updateAnimation(List<Pair<Integer, Integer>> orderedPath, Boolean inActiveBattle);

    public ImageView getCurrentImageView() {
        return this.currentImageView;
    }

    public void setCurrentImageView(String imageDirectionName) {
        this.currentImageView.setImage(turnStringToImage("src/images/"+ imageDirectionName + ".png"));
    }

    /**
     * Changes an image path string to an Image
     * @param stringToConvert
     * @return Image corresponding to the file at the path of stringToConvert
     */
    protected Image turnStringToImage(String stringToConvert) {
        return new Image((new File(stringToConvert)).toURI().toString());
    }
}
