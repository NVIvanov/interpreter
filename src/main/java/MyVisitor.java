import generated.RobotBaseVisitor;
import generated.RobotParser;
import org.antlr.v4.runtime.ParserRuleContext;
import utils.InterpreterException;
import utils.Scope;
import utils.TypeResolver;
import utils.Variable;

import java.util.Optional;

/**
 * @author nivanov
 * on 12.12.16.
 */
class MyVisitor extends RobotBaseVisitor<String> {
    private Scope scope = Scope.global;

    @Override
    public String visitVariableDeclaration(RobotParser.VariableDeclarationContext ctx) {
        visitDeclaration(ctx);
        return super.visitVariableDeclaration(ctx);
    }

    @Override
    public String visitConstVariableDeclaration(RobotParser.ConstVariableDeclarationContext ctx) {
        visitDeclaration(ctx);
        return super.visitConstVariableDeclaration(ctx);
    }

    private void visitDeclaration(ParserRuleContext ctx){
        String variableName = ctx.getChild(1).getText();
        Optional<Variable> variableOptional = scope.getVariable(variableName);
        if (variableOptional.isPresent())
            throw new InterpreterException(InterpreterException.Type.IDENTIFIER_ALREADY_EXISTS, "ID EXISTS");
        String variableType = ctx.getChild(0).getText();
        String value = visit(ctx.getChild(3));
        Object valueObj = TypeResolver.getValue(variableType, value);
        scope.addVariables(new Variable(variableName, valueObj != null ? valueObj.getClass() : null,
                valueObj, TypeResolver.isImmutable(variableType)));
    }

    @Override
    public String visitEqual(RobotParser.EqualContext ctx) {
        String arg1 = visit(ctx.getChild(0)), arg2 = visit(ctx.getChild(2));
        return arg1.equals(arg2) ? "true" : "false";
    }

    @Override
    public String visitIdentifier(RobotParser.IdentifierContext ctx) {
        Optional<Variable> variable = scope.getVariable(ctx.getText());
        if (variable.isPresent()){
            if (variable.get().getType().equals(Integer.class) || variable.get().getType().equals(Boolean.class))
                return variable.get().getValue().toString();
            throw new InterpreterException(InterpreterException.Type.ILLEGAL_OPERAND_TYPE, "operand must be int or bool");
        }
        throw new InterpreterException(InterpreterException.Type.UNEXPECTED_TOKEN, "unknown identifier");
    }

    @Override
    public String visitAssignment(RobotParser.AssignmentContext ctx) {
        String variableName = ctx.getChild(0).getText();
        Optional<Variable> variable = scope.getVariable(variableName);
        if (variable.isPresent()){
            Variable var = variable.get();
            if (!var.getImmutable() && (var.getType().equals(Integer.class) || var.getType().equals(Boolean.class))){
                String valueToAssign = visit(ctx.getChild(2));
                Object value = TypeResolver.getValue(valueToAssign);
                if (!value.getClass().equals(var.getType()))
                    throw new InterpreterException(InterpreterException.Type.ILLEGAL_OPERAND_TYPE, "TYPES MUST EQUAL");
                var.setValue(value);
                return null;
            }
            throw new InterpreterException(InterpreterException.Type.ILLEGAL_OPERAND_TYPE, "you can't assign value to this variable");
        }
        throw new InterpreterException(InterpreterException.Type.UNEXPECTED_TOKEN, "unknown identifier");
    }

    @Override
    public String visitAddition(RobotParser.AdditionContext ctx) {
        String arg1 = visit(ctx.getChild(0)), arg2 = visit(ctx.getChild(2));
        Class argType1 = TypeResolver.resolveType(arg1), argType2 = TypeResolver.resolveType(arg2);
        if (argType1.equals(Integer.class) && argType1.equals(argType2)){
            if (ctx.getChild(1).getText().equals("-")){
                Integer result = Integer.valueOf(arg1) - Integer.valueOf(arg2);
                if (result < 0)
                    throw new InterpreterException(InterpreterException.Type.ILLEGAL_OPERAND_TYPE, "VALUE MUST BE POSITIVE");
                return String.valueOf(result);
            }
            else return String.valueOf(Integer.valueOf(arg1) + Integer.valueOf(arg2));
        }
        throw new InterpreterException(InterpreterException.Type.ILLEGAL_OPERAND_TYPE, "TYPES MUST EQUAL AND BE INTEGER");
    }

    @Override
    public String visitMultiplying(RobotParser.MultiplyingContext ctx) {
        String arg1 = visit(ctx.getChild(0)), arg2 = visit(ctx.getChild(2));
        Class argType1 = TypeResolver.resolveType(arg1), argType2 = TypeResolver.resolveType(arg2);
        if (argType1.equals(Integer.class) && argType1.equals(argType2)){
            if (ctx.getChild(1).getText().equals("/")){
                Integer arg2Int = Integer.valueOf(arg2);
                if (arg2Int.equals(0))
                    throw new InterpreterException(InterpreterException.Type.ILLEGAL_OPERAND_TYPE, "NULL DIVISION");
                return String.valueOf(Integer.valueOf(arg1) / arg2Int);
            }
            else return String.valueOf(Integer.valueOf(arg1) * Integer.valueOf(arg2));
        }
        throw new InterpreterException(InterpreterException.Type.ILLEGAL_OPERAND_TYPE, "TYPES MUST EQUAL AND BE INTEGER");
    }

    @Override
    public String visitIncrement(RobotParser.IncrementContext ctx) {
        String varName = ctx.getChild(1).getText();
        Optional<Variable> variable = scope.getVariable(varName);
        if (variable.isPresent()){
            if (variable.get().getType().equals(Integer.class)){
                Variable var = variable.get();
                var.setValue(Integer.valueOf(var.getValue().toString()) + 1);
                return var.getValue().toString();
            }
            throw new InterpreterException(InterpreterException.Type.ILLEGAL_OPERAND_TYPE, "operand must be int or bool");
        }
        throw new InterpreterException(InterpreterException.Type.UNEXPECTED_TOKEN, "unknown identifier");
    }

    @Override
    public String visitDecrement(RobotParser.DecrementContext ctx) {
        String varName = ctx.getChild(1).getText();
        Optional<Variable> variable = scope.getVariable(varName);
        if (variable.isPresent()){
            if (variable.get().getType().equals(Integer.class)){
                Variable var = variable.get();
                var.setValue(Integer.valueOf(var.getValue().toString()) - 1);
                return var.getValue().toString();
            }
            throw new InterpreterException(InterpreterException.Type.ILLEGAL_OPERAND_TYPE, "operand must be int or bool");
        }
        throw new InterpreterException(InterpreterException.Type.UNEXPECTED_TOKEN, "unknown identifier");
    }

    @Override
    public String visitNegation(RobotParser.NegationContext ctx) {
        String value = visit(ctx.getChild(1));
        if (TypeResolver.resolveType(value).equals(Boolean.class))
            return String.valueOf(!Boolean.valueOf(value));
        throw new InterpreterException(InterpreterException.Type.ILLEGAL_OPERAND_TYPE, "operand must be bool");
    }

    @Override
    public String visitPriotiry(RobotParser.PriotiryContext ctx) {
        return visit(ctx.getChild(1));
    }

    @Override
    public String visitComparation(RobotParser.ComparationContext ctx) {
        String arg1 = visit(ctx.getChild(0)), arg2 = visit(ctx.getChild(2));
        Class argType1 = TypeResolver.resolveType(arg1), argType2 = TypeResolver.resolveType(arg2);
        if (argType1.equals(Integer.class) && argType1.equals(argType2)){
            if (ctx.getChild(1).getText().equals("GT")){
                Boolean result = Integer.valueOf(arg1) > Integer.valueOf(arg2);
                return String.valueOf(result);
            }
            else return String.valueOf(Integer.valueOf(arg1) < Integer.valueOf(arg2));
        }
        throw new InterpreterException(InterpreterException.Type.ILLEGAL_OPERAND_TYPE, "TYPES MUST EQUAL AND BE INTEGER");
    }

    @Override
    public String visitAND(RobotParser.ANDContext ctx) {
        String arg1 = visit(ctx.getChild(0)), arg2 = visit(ctx.getChild(2));
        Class argType1 = TypeResolver.resolveType(arg1), argType2 = TypeResolver.resolveType(arg2);
        if (argType1.equals(Boolean.class) && argType1.equals(argType2))
            return String.valueOf(Boolean.valueOf(arg1) && Boolean.valueOf(arg2));
        throw new InterpreterException(InterpreterException.Type.ILLEGAL_OPERAND_TYPE, "TYPES MUST EQUAL AND BE BOOLEAN");
    }

    @Override
    public String visitOR(RobotParser.ORContext ctx) {
        String arg1 = visit(ctx.getChild(0)), arg2 = visit(ctx.getChild(2));
        Class argType1 = TypeResolver.resolveType(arg1), argType2 = TypeResolver.resolveType(arg2);
        if (argType1.equals(Boolean.class) && argType1.equals(argType2))
            return String.valueOf(Boolean.valueOf(arg1) || Boolean.valueOf(arg2));
        throw new InterpreterException(InterpreterException.Type.ILLEGAL_OPERAND_TYPE, "TYPES MUST EQUAL AND BE BOOLEAN");
    }

    @Override
    public String visitMain(RobotParser.MainContext ctx) {
        String s =  super.visitMain(ctx);
        System.out.println(scope);
        return s;
    }

    @Override
    public String visitLiteral(RobotParser.LiteralContext ctx) {
        return ctx.getText();
    }
}
