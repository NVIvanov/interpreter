package robot.commands.impl;

import robot.Robot;
import robot.commands.RobotCommand;

/**
 * @author nivanov
 *         on 19.12.16.
 */
public class PushBackCommand implements RobotCommand {
    @Override
    public boolean perform(Robot robot) {
        return robot.pushB();
    }

    @Override
    public boolean undo(Robot robot) {
        return robot.grabF();
    }
}
