package robot;

import java.util.*;

/**
 * @author nivanov
 *         on 17.12.16.
 */
class Maze {
    private final int width, height;
    private Map<Point, Point> obstacles = new HashMap<>();
    private Map<Point, ExitDirection> exits = new HashMap<>();

    static Maze createMaze(int width, int height){
        return new Maze(width, height);
    }

    private Maze(int width, int height) {
        this.width = width;
        this.height = height;
    }

    void addObstacle(Point point1, Point point2){
        obstacles.put(point1, point2);
    }

    boolean canMove(int x1, int y1, int x2, int y2){
        boolean hasObstacle = obstacles.entrySet().stream().anyMatch(e ->
                e.getKey().getX() == x1 && e.getKey().getY() == y1 &&
                        e.getValue().getX() == x2 && e.getValue().getY() == y2 ||
                        e.getValue().getX() == x1 && e.getValue().getY() == y1 &&
                                e.getKey().getX() == x2 && e.getKey().getY() == y2);
        boolean noBoards = noBoards(x2, y2);
        return noBoards && !hasObstacle;
    }

    private boolean canMove(Point p1, Point p2){
        return canMove(p1.getX(), p1.getY(), p2.getX(), p2.getY());
    }

    private boolean noBoards(int x2, int y2){
        return x2 >= 0 && x2 < width && y2 >= 0 && y2 < height;
    }

    private boolean noWallsBetween(Point p1, Point p2){
        return obstacles.entrySet().stream().noneMatch(pointPointEntry ->
            pointPointEntry.getKey().equals(p1) && pointPointEntry.getValue().equals(p2)
            || pointPointEntry.getKey().equals(p2) && pointPointEntry.getValue().equals(p1));
    }

    boolean moveBlockRight(int x1, int y1, int x2, int y2){
        if (noWallsBetween(new Point(x1 + 1, y1), new Point(x2 + 1, y2))){
            obstacles.entrySet().stream().filter(e ->
                    hasWall(x1, y1, x2, y2, e))
                    .forEach(e -> {
                        e.getKey().setX(e.getKey().getX() + 1);
                        e.getValue().setX(e.getValue().getX() + 1);
                    });
            return true;
        }else return false;
    }

    boolean moveBlockLeft(int x1, int y1, int x2, int y2){
        if (noWallsBetween(new Point(x1 - 1, y1), new Point(x2 - 1, y2))){
            obstacles.entrySet().stream().filter(e ->
                    hasWall(x1, y1, x2, y2, e))
                    .forEach(e -> {
                        e.getKey().setX(e.getKey().getX() - 1);
                        e.getValue().setX(e.getValue().getX() - 1);
                    });
            return true;
        } else return false;
    }

    boolean moveBlockUp(int x1, int y1, int x2, int y2){
        if (noWallsBetween(new Point(x1, y1 + 1), new Point(x2, y2 + 1))){
            obstacles.entrySet().stream().filter(e ->
                    hasWall(x1, y1, x2, y2, e))
                    .forEach(e -> {
                        e.getKey().setY(e.getKey().getY() + 1);
                        e.getValue().setY(e.getValue().getY() + 1);
                    });
            return true;
        }else return false;
    }

    boolean moveBlockDown(int x1, int y1, int x2, int y2){
        if (noWallsBetween(new Point(x1, y1 - 1), new Point(x2, y2 - 1))){
            obstacles.entrySet().stream().filter(e ->
                    hasWall(x1, y1, x2, y2, e))
                    .forEach(e -> {
                        e.getKey().setY(e.getKey().getY() - 1);
                        e.getValue().setY(e.getValue().getY() - 1);
                    });
            return true;
        }else return false;
    }

    private boolean hasWall(int x1, int y1, int x2, int y2, Map.Entry<Point, Point> e) {
        return e.getKey().getX() == x1 && e.getKey().getY() == y1 &&
                e.getValue().getX() == x2 && e.getValue().getY() == y2 ||
                e.getValue().getX() == x1 && e.getValue().getY() == y1 &&
                        e.getKey().getX() == x2 && e.getValue().getY() == y2;
    }

    void addExit(ExitDirection direction, Point exit){
        exits.put(exit, direction);
    }

    Map<ExitDirection, Integer> getExits(int x, int y){
        Point start = new Point(x, y);
        Map<Point, Integer> markedPoints = new HashMap<>();
        markedPoints.put(start, 0);
        markNearest(start, 0, markedPoints);
        Map<ExitDirection, Integer> availableExits = new HashMap<>();
        markedPoints.forEach((point, integer) -> {
            if (exits.containsKey(point))
                availableExits.put(exits.get(point), integer);
        });
        return availableExits;
    }

    private void markNearest(Point start, Integer currentLength, Map<Point, Integer> markedPoints){
        Integer newLength = currentLength + 1;
        Point[] pointsAround = {
                new Point(start.getX(), start.getY() + 1),
                new Point(start.getX() + 1, start.getY()),
                new Point(start.getX() - 1, start.getY()),
                new Point(start.getX(), start.getY() - 1)
        };
        Map<Point, Integer> pointsToAdd = new HashMap<>();
        Arrays.stream(pointsAround).forEach(point -> {
            if (canMove(start, point) && !markedPoints.containsKey(point))
                pointsToAdd.put(point, newLength);
        });
        markedPoints.putAll(pointsToAdd);
        pointsToAdd.forEach((point, integer) -> markNearest(point, integer, markedPoints));
    }

    enum ExitDirection{
        UP, DOWN, LEFT, RIGHT
    }
}
