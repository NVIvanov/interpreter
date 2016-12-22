package robot;

import robot.commands.RobotCommand;

import java.util.*;
import java.util.logging.Logger;

import static robot.Maze.ExitDirection.*;

/**
 * @author nivanov
 *         on 17.12.16.
 */
public class Robot {
    private final static Integer DEFAULT_MAX_LENGTH = 100000;
    private final static Logger LOG = Logger.getLogger(Robot.class.getName());
    private static Robot instance;
    private int x, y;
    private final Maze maze;
    private Deque<RobotCommand> commandStack = new ArrayDeque<>();
    private Direction direction = Direction.UP;

    private Robot(int x, int y, Maze maze) {
        this.x = x;
        this.y = y;
        this.maze = maze;
    }

    public boolean forward(){
        int nextX = computeNextX(direction), nextY = computeNextY(direction);
        if (maze.canMove(x, y, nextX, nextY)){
            setCoordinates(nextX, nextY);
            return true;
        }
        return false;
    }

    private int computeNextX(Direction direction){
        switch (direction){
            case RIGHT:
                return x + 1;
            case LEFT:
                return x - 1;
            default:
                return x;
        }
    }

    private int computeNextY(Direction direction){
        switch (direction){
            case DOWN:
                return y - 1;
            case UP:
                return y + 1;
            default:
                return y;
        }
    }

    public boolean back(){
        rotateLeft();
        rotateLeft();
        boolean result = forward();
        if (!result){
            rotateLeft();
            rotateLeft();
        }
        return result;
    }

    public boolean right(){
        rotateRight();
        boolean result = forward();
        if (!result)
            rotateLeft();
        return result;
    }

    public boolean left(){
        rotateLeft();
        boolean result = forward();
        if (!result)
            rotateRight();
        return result;
    }

    public void rotateLeft(){
        direction = direction.rotateLeft();
        //LOG.info(String.format("rotating. direction %s", direction));
    }

    public void rotateRight(){
        direction = direction.rotateRight();
        //LOG.info(String.format("rotating. direction %s", direction));
    }

    public boolean pushF(){
        Direction tmp = direction;
        direction = Direction.UP;
        boolean result = maze.moveBlockUp(x, y, computeNextX(direction), computeNextY(direction));
        if (result)
            return forward();
        else
            direction = tmp;
        return false;
    }

    public boolean pushL(){
        Direction tmp = direction;
        direction = Direction.LEFT;
        boolean result = maze.moveBlockLeft(x, y, computeNextX(direction), computeNextY(direction));
        if (result)
            return forward();
        else
            direction = tmp;
        return false;
    }

    public boolean pushB(){
        Direction tmp = direction;
        direction = Direction.DOWN;
        boolean result = maze.moveBlockDown(x, y, computeNextX(direction), computeNextY(direction));
        if (result)
            return forward();
        else
            direction = tmp;
        return false;
    }

    public boolean pushR(){
        Direction tmp = direction;
        direction = Direction.RIGHT;
        boolean result = maze.moveBlockRight(x, y, computeNextX(direction), computeNextY(direction));
        if (result)
            return forward();
        else
            direction = tmp;
        return false;
    }

    public boolean grabF(){
        int x = this.x, y = this.y;
        Direction tmp = direction;
        direction = Direction.UP;
        boolean result = forward();
        if (result)
            maze.moveBlockUp(x, y, x, y - 1);
        else
            direction = tmp;
        return result;
    }

    public boolean grabL(){
        int x = this.x, y = this.y;
        Direction tmp = direction;
        direction = Direction.LEFT;
        boolean result = forward();
        if (result)
            maze.moveBlockLeft(x, y, x + 1, y);
        else
            direction = tmp;
        return result;
    }

    public boolean grabB(){
        int x = this.x, y = this.y;
        Direction tmp = direction;
        direction = Direction.DOWN;
        boolean result = forward();
        if (result)
            maze.moveBlockDown(x, y, x, y + 1);
        else
            direction = tmp;
        return result;
    }

    public boolean grabR(){
        int x = this.x, y = this.y;
        Direction tmp = direction;
        direction = Direction.RIGHT;
        boolean result = forward();
        if (result)
            maze.moveBlockRight(x, y, x - 1, y);
        else
            direction = tmp;
        return result;
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
        LOG.info(String.format("robot was moved to (%d, %d). direction is %s", x, y, direction));
    }

    public enum Direction{
        UP, DOWN, LEFT, RIGHT;
        private static Map<Direction, Direction> rightRotates = new HashMap<>();
        private static Map<Direction, Direction> leftRotates = new HashMap<>();

        static {
            rightRotates.put(UP, RIGHT);
            rightRotates.put(RIGHT, DOWN);
            rightRotates.put(DOWN, LEFT);
            rightRotates.put(LEFT, UP);
            leftRotates.put(UP, LEFT);
            leftRotates.put(LEFT, DOWN);
            leftRotates.put(DOWN, RIGHT);
            leftRotates.put(RIGHT, UP);
        }

        Direction rotateLeft(){
            return leftRotates.get(this);
        }

        Direction rotateRight(){
            return rightRotates.get(this);
        }
    }

    public Direction getDirection() {
        return direction;
    }

    public void setDirection(Direction direction) {
        this.direction = direction;
    }
}
