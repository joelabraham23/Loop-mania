package unsw.loopmania;

import javafx.beans.property.SimpleIntegerProperty;

public class Building extends StaticEntity {

    public Building(SimpleIntegerProperty x, SimpleIntegerProperty y, String entityImage) {
        super(x, y, entityImage);
    }
}