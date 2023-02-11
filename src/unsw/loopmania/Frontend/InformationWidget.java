package unsw.loopmania.Frontend;

import java.io.File;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.text.Text;



public class InformationWidget {

    @FXML
    private Text textInfo;

    @FXML
    private Text title;

    @FXML
    private ImageView infoImage;

    /**
     * information text to show on screen
     */
    private String information;

    /**
     * information title text to show on screen
     */
    private String titleText;

    /**
     * information image to show on screen
     */
    private String imageText;

    private LoopManiaWorldController loopManiaWorldController;

    public InformationWidget(String info, String title, String imagePath) {
        this.information = info;
        this.titleText = title;
        this.imageText = imagePath;
    }

    @FXML
    public void initialize() {
        textInfo.setText(information);
        title.setText(titleText);
        Image photo = new Image((new File(imageText)).toURI().toString());
        infoImage.setImage(photo);
        

    }

    public void setLMWController(LoopManiaWorldController LMWC) {
        this.loopManiaWorldController = LMWC;
    }

    @FXML
    void closeWidget(ActionEvent event) {
        loopManiaWorldController.closeWidget();
    }

    
}
