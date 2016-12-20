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
        return robot.left();
    }

    @Override
    public boolean undo(Robot robot) {
        boolean result = robot.back();
        if (result)
            robot.rotateLeft();
        return result;
    }
}
