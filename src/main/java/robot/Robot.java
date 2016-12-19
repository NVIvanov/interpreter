package robot;

import robot.commands.RobotCommand;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Map;
import java.util.OptionalInt;
import java.util.logging.Logger;

import static robot.Maze.ExitDirection.*;

/**
 * @author nivanov
 *         on 17.12.16.
 */
public class Robot {
    private final static Integer DEFAULT_MAX_LENGTH = Integer.MAX_VALUE;
    private final static Logger LOG = Logger.getLogger(Robot.class.getName());
    private static Robot instance;
    private int x, y;
    private final Maze maze;
    private Deque<RobotCommand> commandStack = new ArrayDeque<>();

    private Robot(int x, int y, Maze maze) {
        this.x = x;
        this.y = y;
        this.maze = maze;
    }

    public boolean forward(){
        if (maze.canMove(x, y, x, y + 1)){
            setCoordinates(x, ++y);
            return true;
        }
        return false;
    }

    public boolean back(){
        if (maze.canMove(x, y, x, y - 1)){
            setCoordinates(x, --y);
            return true;
        }
        return false;
    }

    public boolean right(){
        if (maze.canMove(x, y, x + 1, y)){
            setCoordinates(++x, y);
            return true;
        }
        return false;
    }

    public boolean left(){
        if (maze.canMove(x, y, x - 1, y)){
            setCoordinates(--x, y);
            return true;
        }
        return false;
    }

    public boolean pushF(){
        maze.moveBlockUp(x, y, x, y+1);
        return forward();
    }

    public boolean pushL(){
        maze.moveBlockLeft(x, y, x-1, y);
        return left();
    }

    public boolean pushB(){
        maze.moveBlockDown(x, y, x, y - 1);
        return back();
    }

    public boolean pushR(){
        maze.moveBlockRight(x, y, x + 1, y);
        return right();
    }

    public Integer getF(){
        return getNearestExitWithDirection(UP);
    }

    public Integer getB(){
        return getNearestExitWithDirection(DOWN);
    }

    public Integer getR(){
        return getNearestExitWithDirection(RIGHT);
    }

    public Integer getL(){
        return getNearestExitWithDirection(LEFT);
    }

    public boolean performCommand(RobotCommand command){
        boolean result = command.perform(this);
        if (result)
            commandStack.addLast(command);
        return result;
    }

    public boolean undoLastCommand() {
        return !commandStack.isEmpty() && commandStack.removeLast().undo(this);
    }

    private Integer getNearestExitWithDirection(Maze.ExitDirection direction){
        Map<Maze.ExitDirection, Integer> exits = maze.getExits(x, y);
        OptionalInt minLength = exits.entrySet().stream()
                .filter(entry -> entry.getKey() == direction)
                .mapToInt(Map.Entry::getValue).min();
        return minLength.orElse(DEFAULT_MAX_LENGTH);
    }

    static void createRobot(int x, int y, Maze maze){
        instance = new Robot(x, y, maze);
    }

    public static Robot getInstance() {
        return instance;
    }

    private void setCoordinates(int x, int y){
        this.x = x;
        this.y = y;
        LOG.info(String.format("robot was moved to (%d, %d)", x, y));
    }
}
