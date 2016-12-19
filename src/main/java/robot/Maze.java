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
                        e.getValue().getX() == x2 && e.getValue().getY() == y2);
        boolean noBoards = noBoards(x2, y2);
        return noBoards && !hasObstacle;
    }

    private boolean canMove(Point p1, Point p2){
        return canMove(p1.getX(), p1.getY(), p2.getX(), p2.getY());
    }

    private boolean noBoards(int x2, int y2){
        return x2 >= 0 && x2 < width && y2 >= 0 && y2 < height;
    }

    boolean moveBlockRight(int x1, int y1, int x2, int y2){
        if (noBoards(x2, y2)){
            obstacles.entrySet().stream().filter(e ->
                    e.getKey().getX() == x1 && e.getKey().getY() == y1 &&
                            e.getValue().getX() == x2 && e.getValue().getY() == y2)
                    .forEach(e -> {
                        e.getKey().setX(e.getKey().getX() + 1);
                        e.getValue().setX(e.getValue().getX() + 1);
                    });
            return true;
        }else return false;
    }

    boolean moveBlockLeft(int x1, int y1, int x2, int y2){
        if (noBoards(x2, y2)){
            obstacles.entrySet().stream().filter(e ->
                    e.getKey().getX() == x1 && e.getKey().getY() == y1 &&
                            e.getValue().getX() == x2 && e.getValue().getY() == y2)
                    .forEach(e -> {
                        e.getKey().setX(e.getKey().getX() - 1);
                        e.getValue().setX(e.getValue().getX() - 1);
                    });
            return true;
        } else return false;
    }

    boolean moveBlockUp(int x1, int y1, int x2, int y2){
        if (noBoards(x2, y2)){
            obstacles.entrySet().stream().filter(e ->
                    e.getKey().getX() == x1 && e.getKey().getY() == y1 &&
                            e.getValue().getX() == x2 && e.getValue().getY() == y2)
                    .forEach(e -> {
                        e.getKey().setY(e.getKey().getY() + 1);
                        e.getValue().setY(e.getValue().getY() + 1);
                    });
            return true;
        }else return false;
    }

    boolean moveBlockDown(int x1, int y1, int x2, int y2){
        if (noBoards(x2, y2)){
            obstacles.entrySet().stream().filter(e ->
                    e.getKey().getX() == x1 && e.getKey().getY() == y1 &&
                            e.getValue().getX() == x2 && e.getValue().getY() == y2)
                    .forEach(e -> {
                        e.getKey().setY(e.getKey().getY() - 1);
                        e.getValue().setY(e.getValue().getY() - 1);
                    });
            return true;
        }else return false;
    }

    void addExit(ExitDirection direction, Point exit){
        exits.put(exit, direction);
    }

    Map<ExitDirection, Integer> getExits(int x, int y){
        Point start = new Point(x, y);
        Map<Point, Integer> markedPoints = new HashMap<>();
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
