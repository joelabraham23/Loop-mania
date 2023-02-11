package unsw.loopmania.Frontend;
import java.util.List;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import unsw.loopmania.Character;
import unsw.loopmania.Item;
import unsw.loopmania.LoopManiaWorld;
import unsw.loopmania.GameModes.BerserkerMode;
import unsw.loopmania.GameModes.State;
import unsw.loopmania.GameModes.SurvivalMode;
import unsw.loopmania.Items.*;
public class HerosCastleController {

    @FXML
    private Button buyHealthPotionButton;

    @FXML
    private Button buySwordButton;

    @FXML
    private Button buyStakeButton;

    @FXML
    private Button buyStaffButton;

    @FXML
    private Button buyHelmetButton;

    @FXML
    private Button buyShieldButton;

    @FXML
    private Button buyArmourButton;

    @FXML
    private Button sellHealthPotionButton;

    @FXML
    private Button sellDoggieCoin;

    @FXML
    private Button sellSwordButton;

    @FXML
    private Button sellStakeButton;

    @FXML
    private Button sellStaffButton;

    @FXML
    private Button sellHelmetButton;

    @FXML
    private Button sellShieldButton;

    @FXML
    private Button sellArmourButton;

    @FXML
    private Button resumeButton;

    @FXML
    private TextField doggeAmount;

    private LoopManiaWorld world;
    private LoopManiaWorldController loopManiaWorldController;

    private Character character;
    private List<Item> unequippedInventoryItems;
    private State state;

    /**
     * keep trakc of wether user has already bought a protective gear
     */
    private Boolean protectiveGearBought = false;

    /**
     * keep trakc of wether user has already bought a health potion
     */
    private Boolean healthPotionBough = false;

    @FXML
    public void initialize() {
        // for the first tick before anything changes
        // update all changes
        Boolean restrctionReached = checkRestrictionReached();
        calculateAbleToBuy(restrctionReached);
        calculateAbleToSell();
        
        world.getCharacter().getMoney().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                // update the buy buttons
                Boolean restrctionReached = checkRestrictionReached();
                calculateAbleToBuy(restrctionReached);
                
            }
        });
        
        world.getTimesInventoryChanged().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                // update the sell buttons
                calculateAbleToSell();
            }
        });

        doggeAmount.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, 
                String newValue) {
                if (!newValue.matches("\\d*")) {
                    doggeAmount.setText(newValue.replaceAll("[^\\d]", ""));
                }
                String amount = doggeAmount.getText();
                try {
                    calculateCanSellDoggie(Integer.parseInt(amount));
                } catch (NumberFormatException e) {
                    sellDoggieCoin.setDisable(true);
                }

            }
        });

        sellDoggieCoin.setDisable(true);

    }

    public Boolean checkRestrictionReached() {
        Boolean restrctionReached = false;
        if (state instanceof BerserkerMode) {
            restrctionReached = protectiveGearBought;
        } else if (state instanceof SurvivalMode) {
            restrctionReached = healthPotionBough;
        }
        return restrctionReached;
    }

    public void setWorld(LoopManiaWorld world) {
        this.world = world;
    }

    public void setState(State s) {
        this.state = s;
    }

    public void setCharacter(Character c) {
        this.character = c;
    }

    public void setUnequippedInventory(List<Item> items) {
        this.unequippedInventoryItems = items;
    }

    public void setLMWController(LoopManiaWorldController LMWC) {
        this.loopManiaWorldController = LMWC;
    }

    @FXML
    void buyArmour(ActionEvent event) {
        protectiveGearBought = true;
        Armour newArmour = world.buyArmour();
        loopManiaWorldController.onLoad(newArmour);
    }

    @FXML
    void buyHealthPotion(ActionEvent event) {
        healthPotionBough = true;
        HealthPotion newHealthPotion = world.buyHealthPotion();
        loopManiaWorldController.onLoad(newHealthPotion);
    }

    @FXML
    void buyHelmet(ActionEvent event) {
        protectiveGearBought = true;
        Helmet newHelmet = world.buyHelmet();
        loopManiaWorldController.onLoad(newHelmet);
    }

    @FXML
    void sellDoggie(ActionEvent event) {
        int amount = Integer.parseInt(doggeAmount.getText());
        world.sellDoggie(amount);
        sellDoggieCoin.setText("");
    }

    @FXML
    void buyShield(ActionEvent event) {
        protectiveGearBought = true;
        Shield newShield = world.buyShield();
        loopManiaWorldController.onLoad(newShield);
    }


    @FXML
    void buyStaff(ActionEvent event) {
        Staff newStaff = world.buyStaff();
        loopManiaWorldController.onLoad(newStaff);
    }

    @FXML
    void buyStake(ActionEvent event) {
        Stake newStake = world.buyStake();
        loopManiaWorldController.onLoad(newStake);
    }

    @FXML
    void buySword(ActionEvent event) {
        Sword newSword = world.buySword();
        loopManiaWorldController.onLoad(newSword);
    }

    @FXML
    void resumeFromShop(ActionEvent event) {
        loopManiaWorldController.closeShop();
    }

    @FXML
    void sellArmour(ActionEvent event) {
        world.sellArmour();
    }

    @FXML
    void sellHealthPotion(ActionEvent event) {
        world.sellHealthPotion();
    }

    @FXML
    void sellHelmet(ActionEvent event) {
        world.sellHelmet();
    }

    @FXML
    void sellShield(ActionEvent event) {
        world.sellShield();
    }

    @FXML
    void sellStaff(ActionEvent event) {
        world.sellStaff();
    }

    @FXML
    void sellStake(ActionEvent event) {
        world.sellStake();
    }

    @FXML
    void sellSword(ActionEvent event) {
        world.sellSword();
    }

    private void calculateAbleToBuy(Boolean restrctionReached) {
        

        buyArmourButton.setDisable(state.checkCantBuy(new Armour(null, null), character, restrctionReached));
        buyStaffButton.setDisable(state.checkCantBuy(new Staff(null, null), character, restrctionReached));
        buyShieldButton.setDisable(state.checkCantBuy(new Shield(null, null), character, restrctionReached));
        buySwordButton.setDisable(state.checkCantBuy(new Sword(null, null), character, restrctionReached));
        buyStakeButton.setDisable(state.checkCantBuy(new Stake(null, null), character, restrctionReached));
        buyHelmetButton.setDisable(state.checkCantBuy(new Helmet(null, null), character, restrctionReached));
        buyHealthPotionButton.setDisable(state.checkCantBuy(new HealthPotion(null, null), character, restrctionReached));
    }

    private void calculateAbleToSell() {
        sellArmourButton.setDisable(state.checkCantSell(new Armour(null, null), unequippedInventoryItems));
        sellStaffButton.setDisable(state.checkCantSell(new Staff(null, null), unequippedInventoryItems));
        sellShieldButton.setDisable(state.checkCantSell(new Shield(null, null), unequippedInventoryItems));
        sellSwordButton.setDisable(state.checkCantSell(new Sword(null, null), unequippedInventoryItems));
        sellStakeButton.setDisable(state.checkCantSell(new Stake(null, null), unequippedInventoryItems));
        sellHelmetButton.setDisable(state.checkCantSell(new Helmet(null, null), unequippedInventoryItems));
        sellHealthPotionButton.setDisable(state.checkCantSell(new HealthPotion(null, null), unequippedInventoryItems));
    }

    private void calculateCanSellDoggie(int amount) {
        if (character.getDoggieCoins().getValue() >= amount) {
            sellDoggieCoin.setDisable(false);
        } else {
            sellDoggieCoin.setDisable(true);
        }
    }
}
