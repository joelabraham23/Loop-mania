package unsw.loopmania.RareItems;

import unsw.loopmania.BasicEnemy;
import unsw.loopmania.Character;

public interface RareItem {

    /**
     * @return the image of the 'outer' layer of the decorator
     */
    public String getEntityImageString();    

    /**
     * perform different actions based on the request
     * (defend, attack, spawncharacter)
     * @param originalValue origianvalue for attack/defend
     * @param character main character
     * @param e basic enemy that is being attacked/defending from
     * @param chance chance of rare occurance in the fight
     * @param action action that is happening (attack/defend/heal)
     * @return the new value from original value
     */
    double doMagic(double originalValue, Character character, BasicEnemy e, float chance, RareItemAction action);

    /**
     * Checks if item has duration left
     * @return true if either of items has roundLeft of 0
     */
    boolean endOfLife();

    /**
     * @return true if any of the items in decorator are shield
     */
    boolean isShield();

    /**
     * 
     * @return true if any of the items in decorator are one ring 
     */
    boolean canHeal();

    /**
     * @return true if any of the items in decorator are sword
     */
    boolean isSword();

    void setInnerItem(RareItem item);

}