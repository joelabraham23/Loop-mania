package unsw.loopmania;

import java.io.IOException;

import javafx.application.Platform;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;

import javafx.stage.Stage;
import unsw.loopmania.Frontend.GameOverController;
import unsw.loopmania.Frontend.GameWinController;
import unsw.loopmania.Frontend.LoopManiaWorldController;
import unsw.loopmania.Frontend.LoopManiaWorldControllerLoader;
import unsw.loopmania.Frontend.MainMenuController;
import unsw.loopmania.Frontend.SubMenuController;

/**
 * the main application
 * run main method from this class
 */
public class LoopManiaApplication extends Application {

    /**
     * the controller for the game. Stored as a field so can terminate it when click exit button
     */
    private LoopManiaWorldController mainController;

    @Override
    public void start(Stage primaryStage) throws IOException {
        // set title on top of window bar
        primaryStage.setTitle("Loop Mania");

        // prevent human player resizing game window (since otherwise would see white space)
        // alternatively, you could allow rescaling of the game (you'd have to program resizing of the JavaFX nodes)
        primaryStage.setResizable(false);

        // load the main menu
        MainMenuController mainMenuController = new MainMenuController();
        FXMLLoader menuLoader = new FXMLLoader(getClass().getResource("Frontend/MainMenuView.fxml"));
        menuLoader.setController(mainMenuController);
        Parent mainMenuRoot = menuLoader.load();

        // load game over menu
        GameOverController gameOverController  = new GameOverController();
        FXMLLoader gameOverLoader = new FXMLLoader(getClass().getResource("Frontend/GameOverView.fxml"));
        gameOverLoader.setController(gameOverController);
        Parent gameOverRoot = gameOverLoader.load();

        // load game win menu
        GameWinController gameWinController  = new GameWinController();
        FXMLLoader gameWinLoader = new FXMLLoader(getClass().getResource("Frontend/GameWinView.fxml"));
        gameWinLoader.setController(gameWinController);
        Parent gameWinRoot = gameWinLoader.load();

        // create new scene with the main menu (so we start with the main menu)
        Scene scene = new Scene(mainMenuRoot);
        
        mainMenuController.setSubMenuSwitcher(() -> {
            try {
                // Create the Sub Menu controller
                SubMenuController subMenuController = new SubMenuController();
                FXMLLoader subMenuLoader = new FXMLLoader(getClass().getResource("Frontend/SubMenuView.fxml"));
                subMenuLoader.setController(subMenuController);
                Parent subMenuRoot = subMenuLoader.load();

                // set the headings and sub titles of the Sub Menu
                // this is based on which main menu button was clicked
                String gameMode = mainMenuController.getGameModeTitle();
                subMenuController.setSubMenuHeading(gameMode);
                subMenuController.setSubMenuSubTitle(mainMenuController.getGameModeDescription());

                // load the main game
                LoopManiaWorldControllerLoader loopManiaLoader = new LoopManiaWorldControllerLoader("world_with_twists_and_turns.json", gameMode);
                mainController = loopManiaLoader.loadController();
                FXMLLoader gameLoader = new FXMLLoader(getClass().getResource("Frontend/LoopManiaView.fxml"));
                gameLoader.setController(mainController);
                Parent gameRoot = gameLoader.load();

                // set functions which are activated when button click to switch menu is pressed
                // e.g. from main menu to start the game, or from the game to return to main menu
                mainController.setMainMenuSwitcher(() -> {
                    restartGame(primaryStage);
                    switchToRoot(scene, mainMenuRoot, primaryStage);
                });
                mainController.setGameOverSwitcher(() -> {switchToRoot(scene, gameOverRoot, primaryStage);});
                mainController.setGameWinSwitcher(() -> {switchToRoot(scene, gameWinRoot, primaryStage);});

                // setup the menu switching for the buttons
                subMenuController.setMainMenuSwitcher(() -> {
                    switchToRoot(scene, mainMenuRoot, primaryStage);
                });

                subMenuController.setGameSwitcher(() -> {
                    switchToRoot(scene, gameRoot, primaryStage);
                    mainController.startTimer();
                });
                gameWinController.setWorldController(mainController);
                gameOverController.setWorldController(mainController);
                switchToRoot(scene, subMenuRoot, primaryStage);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        
        gameOverController.setMainMenuSwitcher(() -> {
            restartGame(primaryStage);
            switchToRoot(scene, mainMenuRoot, primaryStage);
        });
        gameWinController.setMainMenuSwitcher(() -> {
            restartGame(primaryStage);
            switchToRoot(scene, mainMenuRoot, primaryStage);
        });
        
        
        // deploy the main onto the stage
        primaryStage.setScene(scene);
        primaryStage.show();
    }
    @Override
    public void stop(){
        // wrap up activities when exit program
        mainController.terminate();
    }

    /**
     * switch to a different Root
     */
    private void switchToRoot(Scene scene, Parent root, Stage stage){
        scene.setRoot(root);
        root.requestFocus();
        stage.setScene(scene);
        stage.sizeToScene();
        stage.show();
    }

    private void restartGame(Stage primaryStage) {
        primaryStage.close();
        Platform.runLater(() -> {
            try {
                new LoopManiaApplication().start(primaryStage);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } );
    }

    public static void main(String[] args) {
        launch(args);
    }
}
