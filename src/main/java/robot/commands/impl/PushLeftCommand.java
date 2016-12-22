package robot.commands.impl;

import robot.Robot;
import robot.commands.RobotCommand;

/**
 * @author nivanov
 *         on 19.12.16.
 */
public class PushLeftCommand extends AbstractPushCommand {

    @Override
    public boolean performPush(Robot robot) {
        return robot.pushL();
    }

    @Override
    public boolean undoPush(Robot robot) {
        return robot.grabR();
    }
}
