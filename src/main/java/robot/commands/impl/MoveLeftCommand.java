package robot.commands.impl;

import robot.Robot;
import robot.commands.RobotCommand;

/**
 * @author nivanov
 *         on 19.12.16.
 */
public class MoveLeftCommand implements RobotCommand {
    @Override
    public boolean perform(Robot robot) {
        return false;
    }

    @Override
    public boolean undo(Robot robot) {
        return false;
    }
}
