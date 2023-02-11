package unsw.loopmania;

import java.util.ArrayList;

/**
 * as part of observer subject pattern,
 * this is the subject that will notify
 * observers they need to create allied soldiers
 */
public interface AlliedSoldierSubject {
    void attachSoldierCreator(AlliedSoldierCreator creator);
    void detachSoldierCreator(AlliedSoldierCreator creator);

    /**
     * notify the creators to make allied soldiers possibly
     */
    ArrayList<AlliedSoldier> possiblyCreateAlliedSoldier(); 

}