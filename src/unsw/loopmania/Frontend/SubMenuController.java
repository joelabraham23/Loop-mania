package unsw.loopmania.Frontend;

import java.io.IOException;

import javafx.fxml.FXML;
import javafx.scene.text.Text;

/**
 * controller for the main menu.
 */
public class SubMenuController {
    /**
     * facilitates switching to main game
     */
    private MenuSwitcher gameSwitcher;
    private MenuSwitcher mainMenuSwitcher;
    @FXML private Text subMenuHeading;
    @FXML private Text subMenuSubTitle;

    public void setGameSwitcher(MenuSwitcher gameSwitcher){
        this.gameSwitcher = gameSwitcher;
    }

    public void setMainMenuSwitcher(MenuSwitcher mainMenuSwitcher){
        this.mainMenuSwitcher = mainMenuSwitcher;
    }

    public void setSubMenuHeading(String text) {
        this.subMenuHeading.setText(text);
    }
    
    public void setSubMenuSubTitle(String text) {
        this.subMenuSubTitle.setText(text);
    }

    /**
     * facilitates switching to main game upon button click
     * @throws IOException
     */
    @FXML
    private void switchToGame() throws IOException {
        gameSwitcher.switchMenu();
    }

    /**
     * Quits the game and the program
     * @throws IOException
     */
    @FXML
    private void backToMainMenu() throws IOException {
        mainMenuSwitcher.switchMenu();
    }

}
