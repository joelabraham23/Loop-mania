package unsw.loopmania.Frontend;

import java.io.IOException;

import javafx.application.Platform;
import javafx.fxml.FXML;

/**
 * controller for the main menu.
 */
public class MainMenuController {
    /**
     * facilitates switching to main game
     */
    private MenuSwitcher gameSwitcher;
    private MenuSwitcher subMenuSwitcher;
    private String gameModeTitle = "";
    private String gameModeDescription = "";

    public void setGameSwitcher(MenuSwitcher gameSwitcher) {
        this.gameSwitcher = gameSwitcher;
    }

    public void setSubMenuSwitcher(MenuSwitcher subMenuSwitcher){
        this.subMenuSwitcher = subMenuSwitcher;
    }

    public String getGameModeTitle() {
        return this.gameModeTitle;
    }

    public String getGameModeDescription() {
        return this.gameModeDescription;
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
     * facilitates switching to beserker game menu upon button click
     * @throws IOException
     */
    @FXML
    private void switchToBeserkerMenu() throws IOException {
        this.gameModeTitle = "Berserker Mode";
        this.gameModeDescription = "Unleash your savage instincts in Berserker Mode. You cannot purchase more than 1 piece of protective gear (protective gear includes armour, helmets, and shields) each time your Champion shops at the Hero's Castle.\nYou have a slightly lower chance of getting rare item which will help you in your battles";
        subMenuSwitcher.switchMenu();
    }

    /**
     * facilitates switching to standard game menu upon button click
     * @throws IOException
     */
    @FXML
    private void switchToStandardMenu() throws IOException {
        this.gameModeTitle = "Standard Mode";
        this.gameModeDescription = "For the casual gamer, Standard mode is straightforward action, with no distinguishing effects";
        subMenuSwitcher.switchMenu();

        /**
         * in submenuController
         */
    }

    /**
     * facilitates switching to survival game menu upon button click
     * @throws IOException
     */
    @FXML
    private void switchToSurvivalMenu() throws IOException {
        this.gameModeTitle = "Survival Mode";
        this.gameModeDescription = "Not for the fainthearted, in Survival mode you can only purchase 1 health potion each time your Champion shops at the Hero's Castle.\nYou have very VERY lower chance of getting helpful rare items.";
        subMenuSwitcher.switchMenu();
    }

    /**
     * facilitates switching to survival game menu upon button click
     * @throws IOException
     */
    @FXML
    private void switchToConfusionMenu() throws IOException {
        this.gameModeTitle = "Confusion Mode";
        this.gameModeDescription = "You have no idea what you're getting yourself into.";
        subMenuSwitcher.switchMenu();
    }

    /**
     * Quits the game and the program
     * @throws IOException
     */
    @FXML
    private void exitGame() throws IOException {
        Platform.exit();
    }

}
