package unsw.loopmania.Frontend;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;

public class GameOverController {

    @FXML
    private Button BackToMainMenuButton;

    @FXML
    private Button quitGameButton;

    private MenuSwitcher mainMenuSwitcher;

    private LoopManiaWorldController loopManiaWorldController;

    public void setMainMenuSwitcher(MenuSwitcher mainMenuSwitcher){
        this.mainMenuSwitcher = mainMenuSwitcher;
    }

    public void setWorldController(LoopManiaWorldController controller) {
        this.loopManiaWorldController = controller;
    }

    @FXML
    void backToMainMenu(ActionEvent event) {
        // stop all game music and restart game, essentially
        this.loopManiaWorldController.stopAllMusic();
        mainMenuSwitcher.switchMenu();
    }

    @FXML
    void quitGame(ActionEvent event) {
        Platform.exit();
    }

}
