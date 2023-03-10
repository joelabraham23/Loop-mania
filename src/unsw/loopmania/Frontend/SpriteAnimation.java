package unsw.loopmania.Frontend;
 
import javafx.animation.Interpolator;
import javafx.animation.Transition;
import javafx.geometry.Rectangle2D;
import javafx.scene.image.ImageView;
import javafx.util.Duration;
 
/**
 * This basic example code was taken from
 * https://netopyr.com/2012/03/09/creating-a-sprite-animation-with-javafx/
 * 
 * Updated with setImageView function
 */
public class SpriteAnimation extends Transition {
 
    private ImageView imageView;
    private final int count;
    private final int columns;
    private final int offsetX;
    private final int offsetY;
    private final int width;
    private final int height;
 
    private int lastIndex;
 
    public SpriteAnimation(ImageView imageView, Duration duration, int count, int columns, int offsetX, int offsetY, int width, int height) {
        this.imageView = imageView;
        this.count     = count;
        this.columns   = columns;
        this.offsetX   = offsetX;
        this.offsetY   = offsetY;
        this.width     = width;
        this.height    = height;
        setCycleDuration(duration);
        setInterpolator(Interpolator.LINEAR);
    }
 
    /**
     * Updates the animation "frame" to the next blocks
     */
    protected void interpolate(double k) {
        final int index = Math.min((int) Math.floor(k * count), count - 1);
        if (index != lastIndex) {
            final int x = (index % columns) * width  + offsetX;
            final int y = (index / columns) * height + offsetY;
            imageView.setViewport(new Rectangle2D(x, y, width, height));
            lastIndex = index;
        }
    }

    /**
     * Allows the image view of the animation to be updated
     * @param imageView
     */
    public void setImageView(ImageView imageView) {
        this.imageView = imageView;
    }
}