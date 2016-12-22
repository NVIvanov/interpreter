package robot.commands.impl;

import robot.Robot;
import robot.commands.RobotCommand;

/**
 * @author nivanov
 *         on 22.12.16.
 */
public abstract class AbstractPushCommand implements RobotCommand {
    private Robot.Direction direction;

    @Override
    public final boolean perform(Robot robot) {
        direction = robot.getDirection();
        return performPush(robot);
    }

    public abstract boolean performPush(Robot robot);

    @Override
    public final boolean undo(Robot robot) {
        boolean result = undoPush(robot);
        robot.setDirection(direction);
        return result;
    }

    public abstract boolean undoPush(Robot robot);
}
