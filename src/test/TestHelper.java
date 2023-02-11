package test;

import java.util.ArrayList;
import org.javatuples.Pair;

public class TestHelper {
    public ArrayList<Pair<Integer, Integer>> getOrderedPath() {
        ArrayList<Pair<Integer, Integer>> orderedPath = new ArrayList<>();
        orderedPath.add(new Pair<>(0, 0));
        orderedPath.add(new Pair<>(1, 1));
        orderedPath.add(new Pair<>(2, 2));
        orderedPath.add(new Pair<>(3, 3));
        orderedPath.add(new Pair<>(4, 4));
        orderedPath.add(new Pair<>(5, 5));
        orderedPath.add(new Pair<>(6, 6));
        orderedPath.add(new Pair<>(7, 7));
        orderedPath.add(new Pair<>(8, 8));
        orderedPath.add(new Pair<>(9, 9));
        orderedPath.add(new Pair<>(10, 10));
        return orderedPath;
    }
}