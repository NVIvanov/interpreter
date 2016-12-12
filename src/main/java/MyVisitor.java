import generated.RobotBaseVisitor;
import generated.RobotParser;

/**
 * @author nivanov
 * on 12.12.16.
 */
class MyVisitor extends RobotBaseVisitor {
    int sentenseCounter = 0;

    @Override
    public Object visitSentense(RobotParser.SentenseContext ctx) {
        System.out.println(++sentenseCounter);
        return super.visitSentense(ctx);
    }
}
