package unsw.loopmania.Frontend;


import java.util.ArrayList;
import java.util.Collections;

import javafx.fxml.FXML;
import javafx.event.EventHandler;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import unsw.loopmania.LoopManiaWorld;

public class SideBarController {
    private LoopManiaWorld world;

    public void setWorld(LoopManiaWorld world) {
        this.world = world;
    }

    @FXML
    private Rectangle characterHealthBox;

    @FXML
    private Rectangle defaultHealthBarSize;

    @FXML
    private Text goldText;

    @FXML
    private Text expText;

    @FXML
    private ScrollPane goalScroll;

    @FXML
    private Text goalTextBox;

    @FXML
    private Text cyclesText;

    @FXML
    private Text healthText;

    @FXML
    private ImageView ringImage;

    @FXML
    private Text doggeCoinText;

    @FXML
    private Text exchangeRate;

    @FXML
    private ImageView ally1;

    @FXML
    private ImageView ally2;

    @FXML
    private ImageView ally4;

    @FXML
    private ImageView ally3;

    // this is used to prevent clicking on the scroll bar
    private AnchorPane LMWanchorPaneRoot;

    // this is used to track the alliedSoldier image views
    private ArrayList<ImageView> allAlliedSoldiers = new ArrayList<ImageView>();

    public void setAnchorPaneRoot(AnchorPane anchorPaneRoot) {
        this.LMWanchorPaneRoot = anchorPaneRoot;
    }

    @FXML
    public void initialize() {
        // add all allied soldiers to array
        Collections.addAll(allAlliedSoldiers, ally1, ally2, ally3, ally4);


        // set listener on cycles
        world.getCharacter().getCyclesCompleted().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                cyclesText.setText(String.valueOf(newValue));
            }
        });

        // set listener on dogge coin
        world.getCharacter().getDoggieCoins().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                doggeCoinText.setText(String.valueOf(newValue));
            }
        });

        // set listener on dogge coin
        world.doggieValue.addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                exchangeRate.setText("1 Doggie = " + String.valueOf(newValue) + " Gold");
            }
        });

        /// set listener on health
        world.getCharacter().getHealth().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                // once health changes, update the health box width
                double healthPercentage = newValue.doubleValue() / 100;
                characterHealthBox.setWidth(defaultHealthBarSize.getWidth() * healthPercentage);
                // update the health string as well
                healthText.setText(String.valueOf(newValue.intValue()) + " / 100");
            }
        });

        // set listener on gold
        world.getCharacter().getMoney().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                // update the gold string
                if (newValue.intValue() < oldValue.intValue()) {
                    // set colour to red
                    goldText.setStyle("-fx-fill: red;");        
                } else {
                    goldText.setStyle("-fx-fill: #bf9939;");        
                }

                goldText.setText(String.valueOf(newValue.intValue()));
            }
        });

        // set listener on experience
        world.getCharacter().getExperience().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                // update the gold string as well
                expText.setText(String.valueOf(newValue.intValue()));
            }
        });

        // Goals
        // goalScroll.setFocusTraversable(false);
        // Set the string for frontend
        String goalString = world.getGoalString();
        goalString = goalString.replace("AND", "AND\n");
        goalString = goalString.replace("OR", "OR\n");
        goalTextBox.setText(goalString);

        // consume all clicks and put focus back on anchor
        goalScroll.addEventHandler(MouseEvent.ANY, new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                event.consume();
                LMWanchorPaneRoot.requestFocus();
            }
        });
        

        // allied soliders

        world.getNumberOfAlliedSoldiers().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                // new value is current amount of soldiers
                for (int i = 0; i < 4; i++) {
                    if (i < newValue.intValue()) {
                        allAlliedSoldiers.get(i).setVisible(true);
                    } else {
                        allAlliedSoldiers.get(i).setVisible(false);
                    }
                }  
            }
        });
    }
}
