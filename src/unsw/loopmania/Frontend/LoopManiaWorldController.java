package unsw.loopmania.Frontend;

import unsw.loopmania.AlliedSoldier;
import unsw.loopmania.BasicEnemy;
import unsw.loopmania.Building;
import unsw.loopmania.Card;
import unsw.loopmania.DragIcon;
import unsw.loopmania.Entity;
import unsw.loopmania.Item;
import unsw.loopmania.LoopManiaWorld;
import unsw.loopmania.StaticEntity;
import unsw.loopmania.Buildings.TaxOffice;
import unsw.loopmania.Enemies.Zombie;
import unsw.loopmania.Items.*;
import unsw.loopmania.RareItems.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.codefx.libfx.listener.handle.ListenerHandle;
import org.codefx.libfx.listener.handle.ListenerHandles;

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Point2D;
import javafx.geometry.Rectangle2D;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaPlayer.Status;
import javafx.scene.paint.Color;
import javafx.scene.shape.*;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.util.Duration;

import java.util.EnumMap;
import java.util.HashMap;
import java.io.File;
import java.io.IOException;

/**
 * the draggable types. If you add more draggable types, add an enum value here.
 * This is so we can see what type is being dragged.
 */
enum DRAGGABLE_TYPE {
    CARD, ITEM, RARE_ITEM
}

/**
 * A JavaFX controller for the world.
 * 
 * All event handlers and the timeline in JavaFX run on the JavaFX application
 * thread:
 * https://examples.javacodegeeks.com/desktop-java/javafx/javafx-concurrency-example/
 * Note in
 * https://openjfx.io/javadoc/11/javafx.graphics/javafx/application/Application.html
 * under heading "Threading", it specifies animation timelines are run in the
 * application thread. This means that the starter code does not need locks
 * (mutexes) for resources shared between the timeline KeyFrame, and all of the
 * event handlers (including between different event handlers). This will make
 * the game easier for you to implement. However, if you add time-consuming
 * processes to this, the game may lag or become choppy.
 * 
 * If you need to implement time-consuming processes, we recommend: using Task
 * https://openjfx.io/javadoc/11/javafx.graphics/javafx/concurrent/Task.html by
 * itself or within a Service
 * https://openjfx.io/javadoc/11/javafx.graphics/javafx/concurrent/Service.html
 * 
 * Tasks ensure that any changes to public properties, change notifications for
 * errors or cancellation, event handlers, and states occur on the JavaFX
 * Application thread, so is a better alternative to using a basic Java Thread:
 * https://docs.oracle.com/javafx/2/threads/jfxpub-threads.htm The Service class
 * is used for executing/reusing tasks. You can run tasks without Service,
 * however, if you don't need to reuse it.
 *
 * If you implement time-consuming processes in a Task or thread, you may need
 * to implement locks on resources shared with the application thread (i.e.
 * Timeline KeyFrame and drag Event handlers). You can check whether code is
 * running on the JavaFX application thread by running the helper method
 * printThreadingNotes in this class.
 * 
 * NOTE: http://tutorials.jenkov.com/javafx/concurrency.html and
 * https://www.developer.com/design/multithreading-in-javafx/#:~:text=JavaFX%20has%20a%20unique%20set,in%20the%20JavaFX%20Application%20Thread.
 * 
 * If you need to delay some code but it is not long-running, consider using
 * Platform.runLater
 * https://openjfx.io/javadoc/11/javafx.graphics/javafx/application/Platform.html#runLater(java.lang.Runnable)
 * This is run on the JavaFX application thread when it has enough time.
 */
public class LoopManiaWorldController {

    /**
     * squares gridpane includes path images, enemies, character, empty grass,
     * buildings
     */
    @FXML
    private GridPane squares;

    /**
     * cards gridpane includes cards and the ground underneath the cards
     */
    @FXML
    private GridPane cards;

    /**
     * anchorPaneRoot is the "background". It is useful since anchorPaneRoot
     * stretches over the entire game world, so we can detect dragging of
     * cards/items over this and accordingly update DragIcon coordinates
     */
    @FXML
    private AnchorPane anchorPaneRoot;

    /**
     * equippedItems gridpane is for equipped items (e.g. swords, shield, axe)
     */
    @FXML
    private GridPane equippedItems;
    @FXML
    private GridPane unequippedInventory;
    @FXML
    private GridPane rareItemInventory;

    @FXML
    private Button mainMenuButton;

    // all image views including tiles, character, enemies, cards... even though
    // cards in separate gridpane...
    private List<ImageView> entityImages;

    /**
     * when we drag a card/item, the picture for whatever we're dragging is set here
     * and we actually drag this node
     */
    private DragIcon draggedEntity;

    private boolean isPaused;
    private LoopManiaWorld world;

    /**
     * runs the periodic game logic - second-by-second moving of character through
     * maze, as well as enemies, and running of battles
     */
    private Timeline timeline;

    private LoopManiaWorldController currentController = this;

    /**
     * the image currently being dragged, if there is one, otherwise null. Holding
     * the ImageView being dragged allows us to spawn it again in the drop location
     * if appropriate.
     */
    private ImageView currentlyDraggedImage;

    /**
     * null if nothing being dragged, or the type of item being dragged
     */
    private DRAGGABLE_TYPE currentlyDraggedType;

    /**
     * mapping from draggable type enum CARD/TYPE to the event handler triggered
     * when the draggable type is dropped over its appropriate gridpane
     */
    private EnumMap<DRAGGABLE_TYPE, EventHandler<DragEvent>> gridPaneSetOnDragDropped;
    /**
     * mapping from draggable type enum CARD/TYPE to the event handler triggered
     * when the draggable type is dragged over the background
     */
    private EnumMap<DRAGGABLE_TYPE, EventHandler<DragEvent>> anchorPaneRootSetOnDragOver;
    /**
     * mapping from draggable type enum CARD/TYPE to the event handler triggered
     * when the draggable type is dropped in the background
     */
    private EnumMap<DRAGGABLE_TYPE, EventHandler<DragEvent>> anchorPaneRootSetOnDragDropped;
    /**
     * mapping from draggable type enum CARD/TYPE to the event handler triggered
     * when the draggable type is dragged into the boundaries of its appropriate
     * gridpane
     */
    private EnumMap<DRAGGABLE_TYPE, EventHandler<DragEvent>> gridPaneNodeSetOnDragEntered;
    /**
     * mapping from draggable type enum CARD/TYPE to the event handler triggered
     * when the draggable type is dragged outside of the boundaries of its
     * appropriate gridpane
     */
    private EnumMap<DRAGGABLE_TYPE, EventHandler<DragEvent>> gridPaneNodeSetOnDragExited;

    /**
     * object handling switching to the main menu
     */
    private MenuSwitcher mainMenuSwitcher;
    private MenuSwitcher gameOverSwitcher;
    private MenuSwitcher gameWinSwitcher;

    /**
     * Grey out the screen when paused
     */
    @FXML
    private Rectangle greyScreen;
    @FXML
    private Text greyScreenTextBox;
    @FXML
    private Text attackText;

    /**
     * Used for displaying heros castle on the right cycle
     */
    private Parent herosCastleRoot;
    MediaPlayer mediaPlayer;
    MediaPlayer battleSoundPlayer;
    MediaPlayer gameMusicPlayer;
    MediaPlayer heroCastlePlayer;
    private Duration gameMusicLength = new Duration(0);
    private Duration battleMusicLength = new Duration(0);
    
    private Parent widgetRoot;
    private Boolean atHerosCastle = false;
    private int nextHerosCastleCycle = 1;
    private int herosCastleCycleStep = 2;

    private Map<Item, ImageView> equippedItemImages = new HashMap<>();
    /**
     * Side Bar Co-ords
     */
    static final private int SIDE_BAR_X = 257;
    static final private int SIDE_BAR_Y = 230;

    /**
     * @param world           world object loaded from file
     * @param initialEntities the initial JavaFX nodes (ImageViews) which should be
     *                        loaded into the GUI
     */
    public LoopManiaWorldController(LoopManiaWorld world, List<ImageView> initialEntities) {
        this.world = world;
        entityImages = new ArrayList<>(initialEntities);
        currentlyDraggedImage = null;
        currentlyDraggedType = null;

        // initialize them all...
        gridPaneSetOnDragDropped = new EnumMap<DRAGGABLE_TYPE, EventHandler<DragEvent>>(DRAGGABLE_TYPE.class);
        anchorPaneRootSetOnDragOver = new EnumMap<DRAGGABLE_TYPE, EventHandler<DragEvent>>(DRAGGABLE_TYPE.class);
        anchorPaneRootSetOnDragDropped = new EnumMap<DRAGGABLE_TYPE, EventHandler<DragEvent>>(DRAGGABLE_TYPE.class);
        gridPaneNodeSetOnDragEntered = new EnumMap<DRAGGABLE_TYPE, EventHandler<DragEvent>>(DRAGGABLE_TYPE.class);
        gridPaneNodeSetOnDragExited = new EnumMap<DRAGGABLE_TYPE, EventHandler<DragEvent>>(DRAGGABLE_TYPE.class);
    }

    @FXML
    public void initialize() {
        Image pathTilesImage = new Image((new File("src/images/32x32GrassAndDirtPath.png")).toURI().toString());
        Image inventorySlotImage = new Image((new File("src/images/empty_slot.png")).toURI().toString());
        Image rareInventorySlotImage = new Image((new File("src/images/rare_item_slot.png")).toURI().toString());

        Rectangle2D imagePart = new Rectangle2D(0, 0, 32, 32);
        int[] possibleRotations = new int[] {0,90,180,270};
        
        // Add the ground first so it is below all other entities (inculding all the
        // twists and turns)
        for (int x = 0; x < world.getWidth(); x++) {
            for (int y = 0; y < world.getHeight(); y++) {
                ImageView groundView = new ImageView(pathTilesImage);
                int randomRotation = possibleRotations[(new Random()).nextInt(possibleRotations.length)];
                groundView.setRotate(groundView.getRotate() + randomRotation); 
                groundView.setViewport(imagePart);
                squares.add(groundView, x, y);
            }
        }

        // load entities loaded from the file in the loader into the squares gridpane
        for (ImageView entity : entityImages) {
            squares.getChildren().add(entity);
        }

        // add the ground underneath the cards
        for (int x = 0; x < world.getWidth(); x++) {
            ImageView groundView = new ImageView(pathTilesImage);
            groundView.setViewport(imagePart);
            cards.add(groundView, x, 0);
        }

        // add the empty slot images for the unequipped inventory
        for (int x = 0; x < LoopManiaWorld.unequippedInventoryWidth; x++) {
            for (int y = 0; y < LoopManiaWorld.unequippedInventoryHeight; y++) {
                ImageView emptySlotView = new ImageView(inventorySlotImage);
                unequippedInventory.add(emptySlotView, x, y);
            }
        }

        // add the empty slot images for the rare items
        for (int x = 0; x < 4; x++) {
            for (int y = 0; y < 1; y++) {
                ImageView emptySlotView = new ImageView(rareInventorySlotImage);
                rareItemInventory.add(emptySlotView, x, y);
            }
        }

        gameMusicPlayer = createAudioPlayer("sound/game_music.mp3");
        battleSoundPlayer = createAudioPlayer("sound/battle.mp3");

        // create the draggable icon
        draggedEntity = new DragIcon();
        draggedEntity.setVisible(false);
        draggedEntity.setOpacity(0.7);
        anchorPaneRoot.getChildren().add(draggedEntity);

        // create the side bar
        SideBarController sideBarController = new SideBarController();
        FXMLLoader sideBarLoader = new FXMLLoader(getClass().getResource("SideBarView.fxml"));
        sideBarLoader.setController(sideBarController);
        sideBarController.setWorld(world);
        sideBarController.setAnchorPaneRoot(anchorPaneRoot);
        try {
            Parent sideBarRoot = sideBarLoader.load();
            sideBarRoot.setLayoutX(SIDE_BAR_X);
            sideBarRoot.setLayoutY(SIDE_BAR_Y);
            anchorPaneRoot.getChildren().add(sideBarRoot);
        } catch (IOException e1) {
            e1.printStackTrace();
        }

        anchorPaneRoot.requestFocus();

        /**
         * When the number of cycles changes, create the hero's castle :)
         */
        world.getCharacter().getCyclesCompleted().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                if (newValue.intValue() == nextHerosCastleCycle) {
                    // calucalte the next cycle information
                    nextHerosCastleCycle += herosCastleCycleStep;
                    herosCastleCycleStep += 1;
                    // load the heros castle menu
                    pause();
                    // start song
                    mediaPlayer = createAudioPlayer("sound/door_open.mp3");
                    mediaPlayer.play();
                    heroCastlePlayer = createAudioPlayer("sound/shop_sound.mp3");
                    heroCastlePlayer.setVolume(0.3);
                    heroCastlePlayer.play();
                    HerosCastleController herosCastleController = new HerosCastleController();
                    FXMLLoader herosCastleLoader = new FXMLLoader(getClass().getResource("HerosCastleView.fxml"));
                    herosCastleLoader.setController(herosCastleController);
                    herosCastleController.setLMWController(currentController);
                    herosCastleController.setWorld(world);
                    herosCastleController.setState(world.getState());
                    herosCastleController.setCharacter(world.getCharacter());
                    herosCastleController.setUnequippedInventory(world.getUnEquippedItems());

                    try {
                        herosCastleRoot = herosCastleLoader.load();
                        anchorPaneRoot.getChildren().add(herosCastleRoot);
                        atHerosCastle = true;
                    } catch (IOException e) {
                        e.printStackTrace();
                    }    
                }
            }
        });

        /**
         * Track a game ending
         */
        world.gameLost.addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
                // if won
                if (newValue.booleanValue()) {
                    // turn off any sounds
                    gameMusicPlayer.stop();
                    if (battleSoundPlayer != null) battleSoundPlayer.stop();
                    playAudio("sound/game_over.mp3");
                    timeline.stop();
                    gameOverSwitcher.switchMenu();
                }
            }
        });

        /**
         * Track game winning
         */
        world.gameWon.addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
                // if won
                if (newValue.booleanValue()) {
                    gameMusicPlayer.stop();
                    timeline.stop();
                    gameWinSwitcher.switchMenu();
                    playAudio("sound/game_won.mp3");
                }
            }
        });
        
    }

    private void playAudio(String musicFile) {
        mediaPlayer = createAudioPlayer(musicFile);
        mediaPlayer.setVolume(0.2);
        mediaPlayer.play();
    }

    private void playBattleSound() {
        battleSoundPlayer.setCycleCount(MediaPlayer.INDEFINITE);
        battleSoundPlayer.seek(battleMusicLength.compareTo(battleSoundPlayer.getTotalDuration()) >= 0 ? Duration.seconds(0) : battleMusicLength);
        battleSoundPlayer.play();
    }

    // set music
    private MediaPlayer createAudioPlayer(String audioFile) {
        Media sound = new Media(new File(audioFile).toURI().toString());
        return new MediaPlayer(sound);
    }
    
    // set music
    private void playGameMusic() {
        battleSoundPlayer.setCycleCount(MediaPlayer.INDEFINITE);
        gameMusicPlayer.setVolume(0.2);
        gameMusicPlayer.seek(gameMusicLength.compareTo(gameMusicPlayer.getTotalDuration()) >= 0 ? Duration.seconds(0) : gameMusicLength);
        gameMusicPlayer.play();
    }



    /**
     * create and run the timer
     */
    public void startTimer() {
        isPaused = false;
        playGameMusic();
        
        // trigger adding code to process main game logic to queue. JavaFX will target framerate of 0.3 seconds
        timeline = new Timeline(new KeyFrame(Duration.seconds(0.3), event -> {
            Boolean inBattle = false;
            ArrayList<BasicEnemy> enemiesInBattle = world.getEnemiesInBattle();
            ArrayList<Item> newGroundItems = world.possibleSpawnItem();
            // pick up any potential item on character path
            for (Item item: newGroundItems) {
                onLoadToGround(item);
            }
            ArrayList<Item> pickedUpItems = world.pickUpItemOnPath();
            for (Item item: pickedUpItems) {
                onLoad(item);
            }
            // check for any new buildings/tax office
            TaxOffice taxOffice = world.createTaxOffice();
            if (taxOffice != null) {
                onLoad(taxOffice);
                createInformationWidget("A new building has been added. \nYou must pay 10% tax everytime you go past here", "Tax Office", "src/images/tax_office.png");
            }

            if (enemiesInBattle.size() > 0) {
                inBattle = true;
                world.runTickMoves(inBattle);
                if (battleSoundPlayer == null || !(battleSoundPlayer.getStatus().equals(Status.PLAYING))) {
                    playBattleSound();
                }
                
                for (BasicEnemy e: enemiesInBattle) {
                    if (e instanceof Zombie) {
                        playAudio("sound/zombie_groan.mp3");
                        break;
                    }
                }
                ArrayList<BasicEnemy> defeatedEnemies = world.runBattles(enemiesInBattle);
                ArrayList<Item> rewards = world.reactToEnemyDefeat(defeatedEnemies);
                for (Item reward: rewards) {
                    if (reward != null) {
                        if (reward instanceof Card) {
                            onLoad((Card) reward);
                        } else {
                            onLoad(reward);
                        }
                    }
                }
                // check if battle won but first update enemiesInBattle
                if (enemiesInBattle.size() == 0) {
                    RareItem rareItem = world.reactToWinBattle(new Random().nextFloat());
                    
                    if (rareItem != null) {
                        // add it to world items
                        onLoad(rareItem);
                    }
                }
            } else {
                if (battleSoundPlayer != null) {
                    battleSoundPlayer.pause();
                    battleMusicLength = battleSoundPlayer.getCurrentTime();
                }
                inBattle = false;
                world.runTickMoves(inBattle);
            }
            List<BasicEnemy> spawnedEnemies = world.spawnEnemies(inBattle);
            for (BasicEnemy newEnemy: spawnedEnemies) {
                onLoad(newEnemy);
            }

            List<AlliedSoldier> newSoldiers = world.possiblyCreateAlliedSoldier();
            for (AlliedSoldier soldier: newSoldiers) {
                onLoad(soldier);
            }
        }));
        timeline.setCycleCount(Animation.INDEFINITE);
        timeline.play();
    }

    public void stopAllMusic() {
        if (battleSoundPlayer != null) {
            battleSoundPlayer.stop();
        }
        if (gameMusicPlayer != null) {
            gameMusicPlayer.stop();
        }
        if (mediaPlayer != null) {
            mediaPlayer.stop();
        }
        if (heroCastlePlayer != null) {
            heroCastlePlayer.stop();
        }    
    }

    /**
     * pause the execution of the game loop
     * the human player can still drag and drop items during the game pause
     */
    public void pause() {
        if (!isPaused) {
            isPaused = true;
            if (timeline != null) {
                timeline.pause();
            }
            if (battleSoundPlayer != null) {
                battleSoundPlayer.pause();
            }
            if (gameMusicPlayer != null) {
                gameMusicPlayer.pause();
                gameMusicLength = gameMusicPlayer.getCurrentTime();
            }
        }
        
    }

    public void terminate() {
        pause();
    }

    private void createInformationWidget(String info, String title, String imagePath) {
        pause();
        if (battleSoundPlayer != null) battleSoundPlayer.pause();
        InformationWidget widget = new InformationWidget(info, title, imagePath);
        FXMLLoader widgetLoader = new FXMLLoader(getClass().getResource("InformationWidget.fxml"));
        widgetLoader.setController(widget);
        widget.setLMWController(currentController);

        try {
            widgetRoot = widgetLoader.load();
            anchorPaneRoot.getChildren().add(widgetRoot);
        } catch (IOException e) {
            e.printStackTrace();
        }    
    }

    /**
     * pair the entity an view so that the view copies the movements of the entity.
     * add view to list of entity images
     * @param entity backend entity to be paired with view
     * @param view frontend imageview to be paired with backend entity
     */
    private void addEntity(Entity entity, ImageView view) {
        trackPosition(entity, view);
        entityImages.add(view);
    }


    /**
     * Changes an image path string to an Image
     * @param stringToConvert
     * @return Image corresponding to the file at the path of stringToConvert
     */
    private Image turnStringToImage(String stringToConvert) {
        return new Image((new File(stringToConvert)).toURI().toString());
    }

    /**
     * load a card into the GUI.
     * Particularly, we must connect to the drag detection event handler,
     * and load the image into the cards GridPane.
     * @param card
     */
    private void onLoad(Card card) {
        Image cardImage = turnStringToImage(card.getEntityImageString());
        ImageView view = new ImageView(cardImage);

        // FROM https://stackoverflow.com/questions/41088095/javafx-drag-and-drop-to-gridpane
        // note target setOnDragOver and setOnDragEntered defined in initialize method
        addDragEventHandlers(view, cardImage, DRAGGABLE_TYPE.CARD, cards, squares);

        addEntity(card, view);
        cards.getChildren().add(view);
    }

    /**
     * load an item into the GUI.
     * Particularly, we must connect to the drag detection event handler,
     * and load the image into the unequippedInventory GridPane.
     * @param item
     */
    public void onLoad(Item item) {
        Image itemImage = turnStringToImage(item.getEntityImageString());
        ImageView view = new ImageView(itemImage);
        addDragEventHandlers(view, itemImage, DRAGGABLE_TYPE.ITEM, unequippedInventory, equippedItems);
        addEntity(item, view);
        item.addToGrid();
        unequippedInventory.getChildren().add(view);
    }

    public void onLoad(RareItem item) {
        Image itemImage = turnStringToImage(((StaticEntity) item).getEntityImageString());
        ImageView view = new ImageView(itemImage);
        addDragEventHandlers(view, itemImage, DRAGGABLE_TYPE.RARE_ITEM, unequippedInventory, equippedItems);
        addEntity((Item) item, view);
        rareItemInventory.getChildren().add(view);
    }

    /**
     * load an item into the GUI.
     * Particularly, we must connect to the drag detection event handler,
     * and load the image into the unequippedInventory GridPane.
     * @param item
     */
    public void onLoadToGround(Item item) {
        Image itemImage = turnStringToImage(item.getEntityImageString());
        ImageView view = new ImageView(itemImage);
        addEntity(item, view);
        squares.getChildren().add(view);
    }

    /**
     * load an enemy into the GUI
     * @param enemy
     */
    private void onLoad(BasicEnemy enemy) {
        addEntity(enemy, (enemy.getCurrentImageView()));
        squares.getChildren().add(enemy.getCurrentImageView());
    }

    /**
     * load an enemy into the GUI
     * @param soldier
     */
    private void onLoad(AlliedSoldier soldier) {
        addEntity(soldier, soldier.getCurrentImageView());
        squares.getChildren().add(soldier.getCurrentImageView());
    }

    /**
     * load a building into the GUI
     * @param building
     */
    private void onLoad(Building building){
        Image buildingImage = turnStringToImage(building.getEntityImageString());
        ImageView view = new ImageView(buildingImage);
        addEntity(building, view);
        squares.getChildren().add(view);
    }

    /**
     * Move an item from unequipped to equipped
     * @param acceptedX coordinate x item can be dropped
     * @param acceptedY coordinate y item can be dropped
     * @param itemMoved the itembeing moved
     * @param targetGridPane grid item is moved to
     * @return false if unableto move item true otherwise
     */
    private Boolean moveItemFromUnEquipped(int acceptedX, int acceptedY, Item itemMoved, GridPane targetGridPane, int destX, int destY, ImageView image, String type) {
        Boolean entityDropped = false;
        int nodeX = GridPane.getColumnIndex(currentlyDraggedImage);
        int nodeY = GridPane.getRowIndex(currentlyDraggedImage);

        if (destX != acceptedX || destY != acceptedY) {
            draggedEntity.setVisible(false);
            onLoad(itemMoved);
        } else {
            Item ejected = world.moveItemToEquippedFromUnequipped(nodeX, nodeY, type);
            if (ejected != null) {
                // remove it from screen
                if (ejected instanceof RareItem) {
                    onLoad((RareItem) ejected);
                } else {
                    onLoad(ejected);
                }
                ImageView imageRemoved = equippedItemImages.get(ejected);
                imageRemoved.setVisible(false);
            }
            equippedItemImages.put(itemMoved, image);
            targetGridPane.add(image, destX, destY, 1, 1);
            entityDropped = true;
        }
        return entityDropped;
    }

    private void moveItemFromRare(Item itemMoved, GridPane targetGridPane, int destX, int destY, ImageView image, String type) {
        int nodeX = GridPane.getColumnIndex(currentlyDraggedImage);
        int nodeY = GridPane.getRowIndex(currentlyDraggedImage);

        Item ejected = world.moveItemToEquippedFromRare(nodeX, nodeY, type);
        if (ejected != null) {
            // remove it from screen
            if (ejected instanceof RareItem) {
                onLoad((RareItem) ejected);
            } else {
                onLoad(ejected);
            }
            ImageView imageRemoved = equippedItemImages.get(ejected);
            imageRemoved.setVisible(false);
        }
        equippedItemImages.put(itemMoved, image);
        targetGridPane.add(image, destX, destY, 1, 1);        
    }

    /**
     * add drag event handlers for dropping into gridpanes, dragging over the background, dropping over the background.
     * These are not attached to invidual items such as swords/cards.
     * @param draggableType the type being dragged - card or item
     * @param sourceGridPane the gridpane being dragged from
     * @param targetGridPane the gridpane the human player should be dragging to (but we of course cannot guarantee they will do so)
     */
    private void buildNonEntityDragHandlers(DRAGGABLE_TYPE draggableType, GridPane sourceGridPane, GridPane targetGridPane){
        // for example, in the specification, villages can only be dropped on path, whilst vampire castles cannot go on the path

        
        gridPaneSetOnDragDropped.put(draggableType, new EventHandler<DragEvent>() {
            public void handle(DragEvent event) {
                /*
                 *you might want to design the application so dropping at an invalid location drops at the most recent valid location hovered over,
                 * or simply allow the card/item to return to its slot (the latter is easier, as you won't have to store the last valid drop location!)
                 */
                Boolean entityDropped = true;

                if (currentlyDraggedType == draggableType) {
                    
                    //Data dropped
                    //If there is an image on the dragboard, read it and use it
                    Dragboard db = event.getDragboard();
                    Node node = event.getPickResult().getIntersectedNode();
                    if(node != targetGridPane && db.hasImage()){
                        Integer cIndex = GridPane.getColumnIndex(node);
                        Integer rIndex = GridPane.getRowIndex(node);
                        int x = cIndex == null ? 0 : cIndex;
                        int y = rIndex == null ? 0 : rIndex;
                        //Places at 0,0 - will need to take coordinates once that is implemented
                        ImageView image = new ImageView(db.getImage());

                        int nodeX = GridPane.getColumnIndex(currentlyDraggedImage);
                        int nodeY = GridPane.getRowIndex(currentlyDraggedImage);
                        switch (draggableType){
                            case CARD:
                                removeDraggableDragEventHandlers(draggableType, targetGridPane);
                                Card card = world.getCardFromPosition(nodeX, nodeY);
                                // check no building already on that spot
                                Building buildingOnSpot = world.getBuildingOnPath(x, y);
                                // check card cna be dropped there
                                if (((card.canBeDroppedOnPath() && (world.isOnPath(x, y)))
                                    || (!card.canBeDroppedOnPath() && !(world.isOnPath(x, y))))
                                    && buildingOnSpot == null) 
                                {
                                    Building newBuilding = convertCardToBuilding(card, nodeX, x, y);
                                    if (newBuilding != null) {
                                        onLoad(newBuilding);
                                    }
                                } else {
                                    // return card to its position
                                    entityDropped = false;
                                    draggedEntity.setVisible(false);
                                    // add card back to position
                                    onLoad(card);
                                }
                                break;
                            case ITEM:
                                removeDraggableDragEventHandlers(draggableType, targetGridPane);
                                Item itemMoved = world.getUnequippedInventoryItemEntityByCoordinates(nodeX, nodeY);
                                // if item moved is a weapon
                                if (itemMoved instanceof Weapon) {
                                    // can only be moved to position 0, 0
                                    entityDropped = moveItemFromUnEquipped(0, 0, itemMoved, targetGridPane, x, y, image, "weapon");
                                } else if (itemMoved instanceof Helmet) {
                                    entityDropped = moveItemFromUnEquipped(1, 0, itemMoved, targetGridPane, x, y, image, "helmet");
                                } else if (itemMoved instanceof Shield) {
                                    entityDropped = moveItemFromUnEquipped(2, 0, itemMoved, targetGridPane, x, y, image, "shield");
                                } else if (itemMoved instanceof Armour) {
                                    entityDropped = moveItemFromUnEquipped(3, 0, itemMoved, targetGridPane, x, y, image, "armour");
                                } else {
                                    entityDropped = false;
                                    draggedEntity.setVisible(false);
                                    onLoad(itemMoved);
                                }     
                                
                                break;
                            case RARE_ITEM:
                                removeDraggableDragEventHandlers(draggableType, targetGridPane);
                                Item item = world.getRareInventoryItemEntityByCoordinates(nodeX, nodeY);
                                RareItem rareItem = (RareItem) item;
                                if (x == 0) {
                                    // rareItem must include anduril
                                    if (rareItem.isSword()) {
                                        // move item otherwise
                                        moveItemFromRare(item, targetGridPane, x, y, image, "weapon");
                                        entityDropped = true;
                                    } else {
                                        entityDropped = false;
                                        draggedEntity.setVisible(false);
                                        onLoad((RareItem) rareItem);
                                    }
                                } else if (x == 2) {
                                    // rareItem must include tree stump
                                    if (rareItem.isShield()) {
                                        // move item otherwise
                                        moveItemFromRare(item, targetGridPane, x, y, image, "shield");
                                        entityDropped = true;
                                    } else {
                                        entityDropped = false;
                                        draggedEntity.setVisible(false);
                                        onLoad((RareItem) rareItem);
                                    }
                                } else {
                                    entityDropped = false;
                                    draggedEntity.setVisible(false);
                                    onLoad((RareItem) rareItem);
                                }
                                
                            default:
                                break;
                        }
                        if (entityDropped) {
                            draggedEntity.setVisible(false);
                            draggedEntity.setMouseTransparent(false);
                            // remove drag event handlers before setting currently dragged image to null
                            currentlyDraggedImage = null;
                            currentlyDraggedType = null;
                        }
                    }
                }
                event.setDropCompleted(true);
                // consuming prevents the propagation of the event to the anchorPaneRoot (as a sub-node of anchorPaneRoot, GridPane is prioritized)
                // https://openjfx.io/javadoc/11/javafx.base/javafx/event/Event.html#consume()
                // to understand this in full detail, ask your tutor or read https://docs.oracle.com/javase/8/javafx/events-tutorial/processing.htm
                event.consume();
            }
        });


        // this doesn't fire when we drag over GridPane because in the event handler for dragging over GridPanes, we consume the event
        anchorPaneRootSetOnDragOver.put(draggableType, new EventHandler<DragEvent>(){
            // https://github.com/joelgraff/java_fx_node_link_demo/blob/master/Draggable_Node/DraggableNodeDemo/src/application/RootLayout.java#L110
            @Override
            public void handle(DragEvent event) {
                if (currentlyDraggedType == draggableType){
                    if(event.getGestureSource() != anchorPaneRoot && event.getDragboard().hasImage()){
                        event.acceptTransferModes(TransferMode.MOVE);
                    }
                }
                if (currentlyDraggedType != null){
                    draggedEntity.relocateToPoint(new Point2D(event.getSceneX(), event.getSceneY()));
                }
                event.consume();
            }
        });

        // this doesn't fire when we drop over GridPane because in the event handler for dropping over GridPanes, we consume the event
        anchorPaneRootSetOnDragDropped.put(draggableType, new EventHandler<DragEvent>() {
            public void handle(DragEvent event) {
                if (currentlyDraggedType == draggableType){
                    //Data dropped
                    //If there is an image on the dragboard, read it and use it
                    Dragboard db = event.getDragboard();
                    Node node = event.getPickResult().getIntersectedNode();
                    if(node != anchorPaneRoot && db.hasImage()){
                        //Places at 0,0 - will need to take coordinates once that is implemented
                        currentlyDraggedImage.setVisible(true);
                        draggedEntity.setVisible(false);
                        draggedEntity.setMouseTransparent(false);
                        // remove drag event handlers before setting currently dragged image to null
                        removeDraggableDragEventHandlers(draggableType, targetGridPane);
                        
                        currentlyDraggedImage = null;
                        currentlyDraggedType = null;
                    }
                }
                //let the source know whether the image was successfully transferred and used
                event.setDropCompleted(true);
                event.consume();
            }
        });
    }

    /**
     * remove the card from the world, and spawn and return a building instead where the card was dropped
     * @param cardNodeX the x coordinate of the card which was dragged, from 0 to width-1
     * @param cardNodeY the y coordinate of the card which was dragged (in starter code this is 0 as only 1 row of cards)
     * @param buildingNodeX the x coordinate of the drop location for the card, where the building will spawn, from 0 to width-1
     * @param buildingNodeY the y coordinate of the drop location for the card, where the building will spawn, from 0 to height-1
     * @return building entity returned from the world
     */
    private Building convertCardToBuilding(Card card, int cardNodeX, int buildingNodeX, int buildingNodeY) {
        return world.convertCardToBuilding(card, cardNodeX, buildingNodeX, buildingNodeY);
    }


    /**
     * add drag event handlers to an ImageView
     * @param view the view to attach drag event handlers to
     * @param draggableType the type of item being dragged - card or item
     * @param sourceGridPane the relevant gridpane from which the entity would be dragged
     * @param targetGridPane the relevant gridpane to which the entity would be dragged to
     */
    private void addDragEventHandlers(ImageView view, Image image, DRAGGABLE_TYPE draggableType, GridPane sourceGridPane, GridPane targetGridPane){
        view.setOnDragDetected(new EventHandler<MouseEvent>() {
            public void handle(MouseEvent event) {
                currentlyDraggedImage = view; // set image currently being dragged, so squares setOnDragEntered can detect it...
                currentlyDraggedType = draggableType;
                //Drag was detected, start drap-and-drop gesture
                //Allow any transfer node
                Dragboard db = view.startDragAndDrop(TransferMode.MOVE);
    
                //Put ImageView on dragboard
                ClipboardContent cbContent = new ClipboardContent();
                cbContent.putImage(view.getImage());
                db.setContent(cbContent);
                view.setVisible(false);

                buildNonEntityDragHandlers(draggableType, sourceGridPane, targetGridPane);

                draggedEntity.relocateToPoint(new Point2D(event.getSceneX(), event.getSceneY()));
                switch (draggableType){
                    case CARD:
                        draggedEntity.setImage(image);
                        break;
                    case ITEM:
                        draggedEntity.setImage(image);
                        break;
                    default:
                        break;
                }
                
                draggedEntity.setVisible(true);
                draggedEntity.setMouseTransparent(true);
                draggedEntity.toFront();

                // IMPORTANT!!!
                // to be able to remove event handlers, need to use addEventHandler
                // https://stackoverflow.com/a/67283792
                targetGridPane.addEventHandler(DragEvent.DRAG_DROPPED, gridPaneSetOnDragDropped.get(draggableType));
                anchorPaneRoot.addEventHandler(DragEvent.DRAG_OVER, anchorPaneRootSetOnDragOver.get(draggableType));
                anchorPaneRoot.addEventHandler(DragEvent.DRAG_DROPPED, anchorPaneRootSetOnDragDropped.get(draggableType));

                for (Node n: targetGridPane.getChildren()){
                    // events for entering and exiting are attached to squares children because that impacts opacity change
                    // these do not affect visibility of original image...
                    // https://stackoverflow.com/questions/41088095/javafx-drag-and-drop-to-gridpane
                    gridPaneNodeSetOnDragEntered.put(draggableType, new EventHandler<DragEvent>() {
                        public void handle(DragEvent event) {
                            if (currentlyDraggedType == draggableType) {
                                //The drag-and-drop gesture entered the target
                                //show the user that it is an actual gesture target

                                // locate which card it is, and where it wants to be dropped
                                int nodeX = GridPane.getColumnIndex(currentlyDraggedImage);
                                int nodeY = GridPane.getRowIndex(currentlyDraggedImage);
                                Card card = world.getCardFromPosition(nodeX, nodeY);
                                Integer cIndex = GridPane.getColumnIndex(n);
                                Integer rIndex = GridPane.getRowIndex(n);
                                int x = cIndex == null ? 0 : cIndex;
                                int y = rIndex == null ? 0 : rIndex;

                                // decide whether or not to put an opaque background
                                if (event.getGestureSource() != n && event.getDragboard().hasImage()){
                                    // get the valid position
                                    if (currentlyDraggedType == DRAGGABLE_TYPE.CARD) {
                                        if ((card.canBeDroppedOnPath() && (world.isOnPath(x, y)))
                                            || (!card.canBeDroppedOnPath() && !(world.isOnPath(x, y)))) {
                                                n.setOpacity(0.7);
                                        }
                                    }
                                    
                                }  
                            }
                            event.consume();
                        }
                    });
                    gridPaneNodeSetOnDragExited.put(draggableType, new EventHandler<DragEvent>() {
                        public void handle(DragEvent event) {
                            // gets rid of the highlight square after being placed
                            n.setOpacity(1);                
                            event.consume();
                        }
                    });
                    n.addEventHandler(DragEvent.DRAG_ENTERED, gridPaneNodeSetOnDragEntered.get(draggableType));
                    n.addEventHandler(DragEvent.DRAG_EXITED, gridPaneNodeSetOnDragExited.get(draggableType));
                }
                event.consume();
            }
            
        });
    }

    /**
     * remove drag event handlers so that we don't process redundant events
     * this is particularly important for slower machines such as over VLAB.
     * @param draggableType either cards, or items in unequipped inventory
     * @param targetGridPane the gridpane to remove the drag event handlers from
     */
    private void removeDraggableDragEventHandlers(DRAGGABLE_TYPE draggableType, GridPane targetGridPane){
        // remove event handlers from nodes in children squares, from anchorPaneRoot, and squares
        targetGridPane.removeEventHandler(DragEvent.DRAG_DROPPED, gridPaneSetOnDragDropped.get(draggableType));

        anchorPaneRoot.removeEventHandler(DragEvent.DRAG_OVER, anchorPaneRootSetOnDragOver.get(draggableType));
        anchorPaneRoot.removeEventHandler(DragEvent.DRAG_DROPPED, anchorPaneRootSetOnDragDropped.get(draggableType));

        for (Node n: targetGridPane.getChildren()){
            n.removeEventHandler(DragEvent.DRAG_ENTERED, gridPaneNodeSetOnDragEntered.get(draggableType));
            n.removeEventHandler(DragEvent.DRAG_EXITED, gridPaneNodeSetOnDragExited.get(draggableType));
        }
    }

    /**
     * handle the pressing of keyboard keys.
     * Specifically, we should pause when pressing SPACE
     * @param event some keyboard key press
     */
    @FXML
    public void handleKeyPress(KeyEvent event) {
        
        switch (event.getCode()) {
        case SPACE:
            if (atHerosCastle) {
                // dont want it to care about space bar :)
                return;
            }
            if (isPaused) {                
                // get rid of the box and text
                anchorPaneRoot.getChildren().remove(greyScreen);
                anchorPaneRoot.getChildren().remove(greyScreenTextBox);
                startTimer();
            }
            else {
                pause();
                // grey out the screen
                this.greyScreen = new Rectangle(0, 0, 255, 550);
                greyScreen.setFill(Color.GREY);
                greyScreen.setOpacity(0.7);
                // put paused so player knows what happened
                this.greyScreenTextBox = new Text("PAUSED");
                greyScreenTextBox.setX(75);
                greyScreenTextBox.setY(240);
                greyScreenTextBox.setFill(Color.WHITE);
                greyScreenTextBox.setFont(Font.font("Segoe UI", FontWeight.BOLD, 30));
                anchorPaneRoot.getChildren().add(greyScreen);
                anchorPaneRoot.getChildren().add(greyScreenTextBox);

            }
            break;
        case H:
            // consume first health potion
            world.drinkHealthPotion();
        default:
            break;
        }
    }

    public void setMainMenuSwitcher(MenuSwitcher mainMenuSwitcher) {
        this.mainMenuSwitcher = mainMenuSwitcher;
    }

    public void setGameOverSwitcher(MenuSwitcher gameOverSwitcher) {
        this.gameOverSwitcher = gameOverSwitcher;
    }

    public void setGameWinSwitcher(MenuSwitcher gameWinSwitcher) {
        this.gameWinSwitcher = gameWinSwitcher;
    }

    /**
     * this method is triggered when click button to go to main menu in FXML
     * @throws IOException
     */
    @FXML
    private void switchToMainMenu() {
        stopAllMusic();
        timeline.stop();
        mainMenuSwitcher.switchMenu();
    }


    /**
     * Set a node in a GridPane to have its position track the position of an
     * entity in the world.
     *
     * By connecting the model with the view in this way, the model requires no
     * knowledge of the view and changes to the position of entities in the
     * model will automatically be reflected in the view.
     * 
     * note that this is put in the controller rather than the loader because we need to track positions of spawned entities such as enemy
     * or items which might need to be removed should be tracked here
     * 
     * NOTE teardown functions setup here also remove nodes from their GridPane. So it is vital this is handled in this Controller class
     * @param entity
     * @param node
     */
    private void trackPosition(Entity entity, Node node) {
        GridPane.setColumnIndex(node, entity.getX());
        GridPane.setRowIndex(node, entity.getY());

        ChangeListener<Number> xListener = new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observable,
                    Number oldValue, Number newValue) {
                GridPane.setColumnIndex(node, newValue.intValue());
            }
        };
        ChangeListener<Number> yListener = new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observable,
                    Number oldValue, Number newValue) {
                GridPane.setRowIndex(node, newValue.intValue());
            }
        };

        // if need to remove items from the equipped inventory, add code to remove from equipped inventory gridpane in the .onDetach part
        ListenerHandle handleX = ListenerHandles.createFor(entity.x(), node)
                                               .onAttach((o, l) -> o.addListener(xListener))
                                               .onDetach((o, l) -> {
                                                    o.removeListener(xListener);
                                                    entityImages.remove(node);
                                                    squares.getChildren().remove(node);
                                                    cards.getChildren().remove(node);
                                                    equippedItems.getChildren().remove(node);
                                                    unequippedInventory.getChildren().remove(node);
                                                    rareItemInventory.getChildren().remove(node);
                                                    if (equippedItemImages.containsKey(entity)) equippedItemImages.get(entity).setVisible(false);
                                                    // if was in equipped and now isn't
                                                })
                                               .buildAttached();
        ListenerHandle handleY = ListenerHandles.createFor(entity.y(), node)
                                               .onAttach((o, l) -> o.addListener(yListener))
                                               .onDetach((o, l) -> {
                                                   o.removeListener(yListener);
                                                   entityImages.remove(node);
                                                   squares.getChildren().remove(node);
                                                   cards.getChildren().remove(node);
                                                   equippedItems.getChildren().remove(node);
                                                   unequippedInventory.getChildren().remove(node);
                                                   rareItemInventory.getChildren().remove(node);
                                                   if (equippedItemImages.containsKey(entity)) equippedItemImages.get(entity).setVisible(false);
                                                })
                                               .buildAttached();
        handleX.attach();
        handleY.attach();

        // this means that if we change boolean property in an entity tracked from here, position will stop being tracked
        // this wont work on character/path entities loaded from loader classes
        entity.shouldExist().addListener(new ChangeListener<Boolean>(){
            @Override
            public void changed(ObservableValue<? extends Boolean> obervable, Boolean oldValue, Boolean newValue) {
                if (newValue) {
                    // if new value is set to true then attach
                    handleX.attach();
                    handleY.attach();
                } else {
                    handleX.detach();
                    handleY.detach();
                }
            }
        });
    }

    public void closeShop() {
        // take away the menu and restart game
        this.anchorPaneRoot.getChildren().remove(herosCastleRoot);
        startTimer();
        atHerosCastle = false;
        mediaPlayer = createAudioPlayer("sound/door_shut.mp3");
        mediaPlayer.play();
        heroCastlePlayer.stop();
        // put focus back to main screen
        anchorPaneRoot.requestFocus();
    }

    public void closeWidget() {
        // take away the menu and restart game
        this.anchorPaneRoot.getChildren().remove(widgetRoot);
        startTimer();
        anchorPaneRoot.requestFocus();
    }
    
}
