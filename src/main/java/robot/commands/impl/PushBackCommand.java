package robot.commands.impl;

import robot.Robot;

/**
 * @author nivanov
 *         on 19.12.16.
 */
public class PushBackCommand extends AbstractPushCommand {
    @Override
    public boolean performPush(Robot robot) {
        return robot.pushB();
    }

    @Override
    public boolean undoPush(Robot robot) {
        return robot.grabF();
    }
}
