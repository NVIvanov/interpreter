package robot.commands;

import robot.Robot;

/**
 * @author nivanov
 *         on 19.12.16.
 */
public interface RobotCommand {
    boolean perform(Robot robot);
    boolean undo(Robot robot);
}
