package robot.commands.impl;

import robot.Robot;
import robot.commands.RobotCommand;

/**
 * @author nivanov
 *         on 19.12.16.
 */
public class MoveRightCommand implements RobotCommand {
    @Override
    public boolean perform(Robot robot) {
        return robot.right();
    }

    @Override
    public boolean undo(Robot robot) {
        boolean result = robot.back();
        if (result)
            robot.rotateRight();
        return result;
    }
}
