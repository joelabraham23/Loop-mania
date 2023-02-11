package unsw.loopmania.Frontend;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;

public class GameWinController {

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
        mainMenuSwitcher.switchMenu();
        loopManiaWorldController.stopAllMusic();
    }

    @FXML
    void quitGame(ActionEvent event) {
        Platform.exit();
    }

}

