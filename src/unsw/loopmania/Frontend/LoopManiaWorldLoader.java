package unsw.loopmania.Frontend;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Arrays;

import org.javatuples.Pair;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;

import javafx.beans.property.SimpleIntegerProperty;
import unsw.loopmania.Building;
import unsw.loopmania.Character;
import unsw.loopmania.GoalComponent;
import unsw.loopmania.LoopManiaWorld;
import unsw.loopmania.PathPosition;
import unsw.loopmania.PathTile;
import unsw.loopmania.Buildings.HeroCastle;
import unsw.loopmania.Buildings.*;
import unsw.loopmania.GameModes.BerserkerMode;
import unsw.loopmania.GameModes.ConfusionMode;
import unsw.loopmania.GameModes.GameMode;
import unsw.loopmania.GameModes.State;
import unsw.loopmania.GameModes.SurvivalMode;
import unsw.loopmania.Goals.AndCompositeGoal;
import unsw.loopmania.Goals.CompositeGoal;
import unsw.loopmania.Goals.CycleGoal;
import unsw.loopmania.Goals.ExperienceGoal;
import unsw.loopmania.Goals.KillBosses;
import unsw.loopmania.Goals.MoneyGoal;
import unsw.loopmania.Goals.OrCompositeGoal;

import java.util.List;

/**
 * Loads a world from a .json file.
 * 
 * By extending this class, a subclass can hook into entity creation.
 * This is useful for creating UI elements with corresponding entities.
 * 
 * this class is used to load the world.
 * it loads non-spawning entities from the configuration files.
 * spawning of enemies/cards must be handled by the controller.
 */
public abstract class LoopManiaWorldLoader {
    private JSONObject json;

    /**
     * states for the game modes
     */
    State berserker;
    State survival;
    State normal;
    State confusion;
    State state;


    public LoopManiaWorldLoader(String filename, String gameMode) throws FileNotFoundException {
        json = new JSONObject(new JSONTokener(new FileReader("worlds/" + filename)));
        berserker = new BerserkerMode();
        survival = new SurvivalMode();
        confusion = new ConfusionMode();
        normal = new GameMode();

        if (gameMode.equals("Berserker Mode")) {
            state = berserker;
        } else if (gameMode.equals("Survival Mode")) {
            state = survival;
        } else if (gameMode.equals("Confusion Mode")) {
            state = confusion;
        } else {
            state = normal;
        }
    }

    private GoalComponent getSimpleGoal(JSONObject goal) {
        String goalDescOne = goal.getString("goal");
        int goalValue = 0;
        if (goal.has("quantity")) goalValue = goal.getInt("quantity");
        if (goalDescOne.equals("experience")) {
            return new ExperienceGoal(goalValue);
        } else if (goalDescOne.equals("gold")) {
            return new MoneyGoal(goalValue);
        } else if (goalDescOne.equals("cycles")) {
            return new CycleGoal(goalValue);
        } else if (goalDescOne.equals("bosses")) {
            ArrayList<String> allBosses = new ArrayList<>(Arrays.asList("elan", "doggie"));
            return new KillBosses(allBosses);
        }
        return null;
    }


    private ArrayList<String> getRareItems(JSONArray items) {
        ArrayList<String> allItems = new ArrayList<>();
        for (int i = 0; i < items.length(); i++) {
            String item = items.getString(i);
            allItems.add(item);
        }
        return allItems;
    }


    private CompositeGoal parseGoals(JSONObject goal) {
        String goalDesc = goal.getString("goal");
        CompositeGoal result;
        if (goalDesc.equals("AND")) {
            result = new AndCompositeGoal();
        } else {
            result = new OrCompositeGoal();
        }
        if (goal.has("subgoals")) {
            // for each item in list, parseGoals
            JSONArray subgoals = goal.getJSONArray("subgoals");
            for (int i = 0; i <subgoals.length(); i++) {
                JSONObject newGoal = subgoals.getJSONObject(i);
                result.addGoal(parseGoals(newGoal));
            }
        } else {
            result.addGoal(getSimpleGoal(goal));
        }
        return result;
    }

    /**
     * Parses the JSON to create a world.
     */
    public LoopManiaWorld load() {
        int width = json.getInt("width");
        int height = json.getInt("height");
        // get goals
        JSONObject goals = json.getJSONObject("goal-condition");
        JSONArray items = json.getJSONArray("rare_items");

        CompositeGoal result = parseGoals(goals);
        ArrayList<String> allRareItems = getRareItems(items);
        // path variable is collection of coordinates with directions of path taken...
        List<Pair<Integer, Integer>> orderedPath = loadPathTiles(json.getJSONObject("path"), width, height);

        LoopManiaWorld world = new LoopManiaWorld(width, height, orderedPath, result, allRareItems, state);

        JSONArray jsonEntities = json.getJSONArray("entities");
        // load non-path entities later so that they're shown on-top
        for (int i = 0; i < jsonEntities.length(); i++) {
            loadEntity(world, jsonEntities.getJSONObject(i), orderedPath);
        }
        return world;
    }

    /**
     * load an entity into the world
     * @param world backend world object
     * @param json a JSON object to parse (different from the )
     * @param orderedPath list of pairs of x, y cell coordinates representing game path
     */
    private void loadEntity(LoopManiaWorld world, JSONObject currentJson, List<Pair<Integer, Integer>> orderedPath) {
        String type = currentJson.getString("type");
        int x = currentJson.getInt("x");
        int y = currentJson.getInt("y");
        int indexInPath = orderedPath.indexOf(new Pair<Integer, Integer>(x, y));
    
        switch (type) {
        case "hero_castle":
            // adding heros castle to map
            HeroCastle heros = new HeroCastle(new SimpleIntegerProperty(0), new SimpleIntegerProperty(0)); 
            onLoad(heros);
            world.addToBuildingEntities(heros);
            // then add character on top
            Character character = new Character(new PathPosition(indexInPath, orderedPath));
            world.setCharacter(character);
            onLoad(character);
            break;
        case "rocket":
            Rocket rocket = new Rocket(new SimpleIntegerProperty(x), new SimpleIntegerProperty(y));
            onLoad(rocket);
            world.addToBuildingEntities(rocket);
            break;
        case "crypto_mine":
            DoggieMine mine = new DoggieMine(new SimpleIntegerProperty(x), new SimpleIntegerProperty(y));
            onLoad(mine);
            world.addToBuildingEntities(mine);
            break;
        case "path_tile":
            throw new RuntimeException("path_tile's aren't valid entities, define the path externally.");
        }
    }

    /**
     * load path tiles
     * @param path json data loaded from file containing path information
     * @param width width in number of cells
     * @param height height in number of cells
     * @return list of x, y cell coordinate pairs representing game path
     */
    private List<Pair<Integer, Integer>> loadPathTiles(JSONObject path, int width, int height) {
        if (!path.getString("type").equals("path_tile")) {
            // ... possible extension
            throw new RuntimeException(
                    "Path object requires path_tile type.  No other path types supported at this moment.");
        }
        PathTile starting = new PathTile(new SimpleIntegerProperty(path.getInt("x")), new SimpleIntegerProperty(path.getInt("y")));
        if (starting.getY() >= height || starting.getY() < 0 || starting.getX() >= width || starting.getX() < 0) {
            throw new IllegalArgumentException("Starting point of path is out of bounds");
        }
        // load connected path tiles
        List<PathTile.Direction> connections = new ArrayList<>();
        for (Object dir: path.getJSONArray("path").toList()){
            connections.add(Enum.valueOf(PathTile.Direction.class, dir.toString()));
        }

        if (connections.size() == 0) {
            throw new IllegalArgumentException(
                "This path just consists of a single tile, it needs to consist of multiple to form a loop.");
        }

        // load the first position into the orderedPath
        PathTile.Direction first = connections.get(0);
        List<Pair<Integer, Integer>> orderedPath = new ArrayList<>();
        orderedPath.add(Pair.with(starting.getX(), starting.getY()));

        int x = starting.getX() + first.getXOffset();
        int y = starting.getY() + first.getYOffset();

        // add all coordinates of the path into the orderedPath
        for (int i = 1; i < connections.size(); i++) {
            orderedPath.add(Pair.with(x, y));
            
            if (y >= height || y < 0 || x >= width || x < 0) {
                throw new IllegalArgumentException("Path goes out of bounds at direction index " + (i - 1) + " (" + connections.get(i - 1) + ")");
            }
            
            PathTile.Direction dir = connections.get(i);
            PathTile tile = new PathTile(new SimpleIntegerProperty(x), new SimpleIntegerProperty(y));
            x += dir.getXOffset();
            y += dir.getYOffset();
            if (orderedPath.contains(Pair.with(x, y)) && !(x == starting.getX() && y == starting.getY())) {
                throw new IllegalArgumentException("Path crosses itself at direction index " + i + " (" + dir + ")");
            }
            onLoad(tile, connections.get(i - 1), dir);
        }
        // we should connect back to the starting point
        if (x != starting.getX() || y != starting.getY()) {
            throw new IllegalArgumentException(String.format(
                    "Path must loop back around on itself, this path doesn't finish where it began, it finishes at %d, %d.",
                    x, y));
        }
        onLoad(starting, connections.get(connections.size() - 1), connections.get(0));
        return orderedPath;
    }

    public abstract void onLoad(Character character);
    public abstract void onLoad(PathTile pathTile, PathTile.Direction into, PathTile.Direction out);
    public abstract void onLoad(Building building);

}
