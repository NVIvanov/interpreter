package robot;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author nivanov
 *         on 17.12.16.
 */
public class RobotFileImporter {
    private static final String mazeInitRegex = "maze (\\d) (\\d)";
    private static final String robotInitRegex = "robot (\\d) (\\d)";
    private static final String mazeWallRegex = "(\\d) (\\d) (\\d) (\\d)";
    private static final String mazeExitRegex = "exit ([A-Z]+) (\\d) (\\d)";
    private static final Logger logger = Logger.getLogger(RobotFileImporter.class.getName());
    private static Maze maze;

    public static void importFile(String filename) throws IOException {
        Files.lines(Paths.get(filename)).forEach(line -> {
            if (line.matches(mazeInitRegex)){
                Pattern pattern = Pattern.compile(mazeInitRegex);
                Matcher matcher = pattern.matcher(line);
                if (matcher.matches()){
                    Integer width = Integer.valueOf(matcher.group(1));
                    Integer height = Integer.valueOf(matcher.group(2));
                    maze = Maze.createMaze(width, height);
                    logger.info(String.format("maze with width = %d and height = %d was created", width, height));
                }
            }
            if (line.matches(robotInitRegex)){
                Pattern pattern = Pattern.compile(robotInitRegex);
                Matcher matcher = pattern.matcher(line);
                if (matcher.matches()){
                    Integer x = Integer.valueOf(matcher.group(1));
                    Integer y = Integer.valueOf(matcher.group(2));
                    Robot.createRobot(x, y, maze);
                    logger.info(String.format("robot was created, coordinates x = %d, y = %d", x, y));
                }
            }
            if (line.matches(mazeExitRegex)){
                Pattern pattern = Pattern.compile(mazeExitRegex);
                Matcher matcher = pattern.matcher(line);
                if (matcher.matches()){
                    Maze.ExitDirection direction = Maze.ExitDirection.valueOf(matcher.group(1));
                    Integer x = Integer.valueOf(matcher.group(2));
                    Integer y = Integer.valueOf(matcher.group(3));
                    maze.addExit(direction, new Point(x, y));
                    logger.info(String.format("exit was added, coordinates x = %d, y = %d, direction - %s", x, y, direction.toString()));
                }
            }
            if (line.matches(mazeWallRegex)){
                Pattern pattern = Pattern.compile(mazeWallRegex);
                Matcher matcher = pattern.matcher(line);
                if (matcher.matches()){
                    Integer x1 = Integer.valueOf(matcher.group(1));
                    Integer y1 = Integer.valueOf(matcher.group(2));
                    Integer x2 = Integer.valueOf(matcher.group(3));
                    Integer y2 = Integer.valueOf(matcher.group(4));
                    maze.addObstacle(new Point(x1, y1), new Point(x2, y2));
                    logger.info(String.format("obstacle between (%d, %d) and (%d, %d) was created", x1, y1, x2, y2));
                }
            }
        });
    }
}
