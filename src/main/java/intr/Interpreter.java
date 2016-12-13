package intr;

import generated.RobotLexer;
import generated.RobotParser;
import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.tree.ParseTree;

import java.io.IOException;

/**
 * @author nivanov
 * on 12.12.16.
 */
class Interpreter {
    public static void main(String[] args) throws IOException {
         ANTLRInputStream stream = new ANTLRFileStream(args[0]);
         RobotLexer lexer = new RobotLexer(stream);
         CommonTokenStream tokens = new CommonTokenStream(lexer);
         RobotParser parser = new RobotParser(tokens);
         ParseTree tree = parser.main();

         new MyVisitor().visit(tree);
    }
}
