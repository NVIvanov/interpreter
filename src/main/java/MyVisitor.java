import generated.RobotBaseVisitor;
import generated.RobotParser;
import org.antlr.v4.runtime.ParserRuleContext;
import utils.*;

import java.util.ArrayList;
import java.util.List;
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
        return null;
    }

    @Override
    public String visitConstVariableDeclaration(RobotParser.ConstVariableDeclarationContext ctx) {
        visitDeclaration(ctx);
        return null;
    }

    private void visitDeclaration(ParserRuleContext ctx){
        String variableName = ctx.getChild(1).getText();
        checkVariableExisting(variableName);
        String variableType = ctx.getChild(0).getText();
        String value = visit(ctx.getChild(3));
        Object valueObj = TypeResolver.getValue(variableType, value);
        scope.addVariables(new Variable(variableName, valueObj != null ? valueObj.getClass() : null,
                valueObj, TypeResolver.isImmutable(variableType)));
    }

    private void checkVariableExisting(String name) {
        Optional<Variable> variableOptional = scope.getVariable(name);
        if (variableOptional.isPresent())
            throw new InterpreterException(InterpreterException.Type.IDENTIFIER_ALREADY_EXISTS, "ID EXISTS");
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
            if (!(variable.get() instanceof ArrayVariable))
                return variable.get().getValue().toString();
            throw new InterpreterException(InterpreterException.Type.ILLEGAL_OPERAND_TYPE, "operand must be int or bool");
        }
        throw new InterpreterException(InterpreterException.Type.UNEXPECTED_TOKEN, "unknown identifier");
    }

    @Override
    public String visitAssignment(RobotParser.AssignmentContext ctx) {
        String variableName = ctx.getChild(0).getText();
        if (variableName.contains("["))
            variableName = variableName.split("\\[")[0];
        Optional<Variable> variable = scope.getVariable(variableName);
        if (variable.isPresent()){
            Variable var = variable.get();
            if (!var.getImmutable() && (var.getType().equals(Integer.class) || var.getType().equals(Boolean.class))){
                String valueToAssign = visit(ctx.getChild(2));
                Object value = TypeResolver.getValue(valueToAssign);
                if (!value.getClass().equals(var.getType()))
                    throw new InterpreterException(InterpreterException.Type.ILLEGAL_OPERAND_TYPE, "TYPES MUST EQUAL");
                if (var instanceof DoubleArrayVariable){
                    String indexString = visit(ctx.getChild(0).getChild(2));
                    if (!(TypeResolver.resolveType(indexString).equals(Integer.class)))
                        throw new InterpreterException(InterpreterException.Type.ILLEGAL_ARGUMENT_TYPE, "INDEX MUST BE INTEGER");
                    Integer index = Integer.valueOf(indexString);
                    List<List> list = (List<List>) var.getValue();
                    if (index < 0 || index >= list.size())
                        throw new InterpreterException(InterpreterException.Type.ILLEGAL_ARGUMENT_TYPE, "ARRAY OUT OF BOUND");
                    String secondIndexString = visit(ctx.getChild(0).getChild(4));
                    Integer secondIndex = Integer.valueOf(secondIndexString);
                    if (secondIndex < 0 || secondIndex >= list.get(index).size())
                        throw new InterpreterException(InterpreterException.Type.ILLEGAL_ARGUMENT_TYPE, "ARRAY OUT OF BOUND");
                    list.get(index).set(secondIndex, value);
                } else if (var instanceof ArrayVariable){
                    String indexString = visit(ctx.getChild(0).getChild(2));
                    if (!(TypeResolver.resolveType(indexString).equals(Integer.class)))
                        throw new InterpreterException(InterpreterException.Type.ILLEGAL_ARGUMENT_TYPE, "INDEX MUST BE INTEGER");
                    Integer index = Integer.valueOf(indexString);
                    List list = (List) var.getValue();
                    if (index < 0 || index >= list.size())
                        throw new InterpreterException(InterpreterException.Type.ILLEGAL_ARGUMENT_TYPE, "ARRAY OUT OF BOUND");
                    list.set(index, value);
                }else {
                    var.setValue(value);
                }
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
        String s = super.visitMain(ctx);
        System.out.println(scope);
        return s;
    }

    @Override
    public String visitWhileCycle(RobotParser.WhileCycleContext ctx) {
        String value = visit(ctx.getChild(2));
        if (TypeResolver.resolveType(value).equals(Boolean.class)){
            while (Boolean.valueOf(value)){
                this.scope = new Scope(this.scope);
                visit(ctx.getChild(4));
                this.scope = scope.getParent();
                value = visit(ctx.getChild(2));
            }
            return null;
        }
        throw new InterpreterException(InterpreterException.Type.ILLEGAL_ARGUMENT_TYPE, "IF EXPRESSION MUST BE BOOLEAN");
    }

    @Override
    public String visitArrayDeclaration(RobotParser.ArrayDeclarationContext ctx) {
        String varName = ctx.getChild(1).getText();
        checkVariableExisting(varName);
        String typeString = ctx.getChild(0).getText();
        Class type = TypeResolver.resolveArrayType(typeString);
        List list = new ArrayList();
        if (ctx.getChild(3).getChildCount() > 2){
            for (int i = 1; i < ctx.getChild(3).getChildCount(); i+=2){
                String value = visit(ctx.getChild(3).getChild(i));
                Class valueClass = TypeResolver.resolveType(value);
                if (!valueClass.equals(type))
                    throw new InterpreterException(InterpreterException.Type.ILLEGAL_ARGUMENT_TYPE, "INIT VALUES CLASSES MUST EQUAL");
                list.add(TypeResolver.getValue(value));
            }
        }
        Variable array = new ArrayVariable(varName, type, list, false);
        scope.addVariables(array);
        return null;
    }

    @Override
    public String visitArrayValue(RobotParser.ArrayValueContext ctx) {
        String variableName = ctx.getChild(0).getText();
        Optional<Variable> variableOptional = scope.getVariable(variableName);
        if (!variableOptional.isPresent())
            throw new InterpreterException(InterpreterException.Type.UNEXPECTED_TOKEN, "UNKNOWN IDENTIFIER");
        String indexString = visit(ctx.getChild(2));
        if (TypeResolver.resolveType(indexString).equals(Boolean.class))
            throw new InterpreterException(InterpreterException.Type.ILLEGAL_ARGUMENT_TYPE, "INDEX MUST BE INTEGER");
        if (!(variableOptional.get() instanceof ArrayVariable))
            throw new InterpreterException(InterpreterException.Type.UNEXPECTED_TOKEN, "VARIABLE MUST BE ARRAY");
        List list = (List)variableOptional.get().getValue();
        if (Integer.valueOf(indexString) >= list.size() || Integer.valueOf(indexString) < 0)
            throw new InterpreterException(InterpreterException.Type.ILLEGAL_ARGUMENT_TYPE, "ARRAY INDEX OUT OF BOUND");
        return list.get(Integer.valueOf(indexString)).toString();
    }

    @Override
    public String visitDoubleArrayDeclaration(RobotParser.DoubleArrayDeclarationContext ctx) {
        String varName = ctx.getChild(1).getText();
        checkVariableExisting(varName);
        String typeString = ctx.getChild(0).getText();
        Class type = TypeResolver.resolveArrayType(typeString);
        List<List> list = new ArrayList();
        if (ctx.getChild(3).getChildCount() > 2){
            for (int i = 1; i < ctx.getChild(3).getChildCount(); i+=2){
                List list1 = new ArrayList();
                if (ctx.getChild(3).getChild(i).getChildCount() > 2){
                    for (int j = 1; j < ctx.getChild(3).getChild(i).getChildCount(); j+=2){
                        String value = visit(ctx.getChild(3).getChild(i).getChild(j));
                        Class valueClass = TypeResolver.resolveType(value);
                        if (!valueClass.equals(type))
                            throw new InterpreterException(InterpreterException.Type.ILLEGAL_ARGUMENT_TYPE, "INIT VALUES CLASSES MUST EQUAL");
                        list1.add(TypeResolver.getValue(value));
                    }
                }
                list.add(list1);
            }
        }
        Variable array = new DoubleArrayVariable(varName, type, list, false);
        scope.addVariables(array);
        return null;
    }

    @Override
    public String visitDoubleArrayValue(RobotParser.DoubleArrayValueContext ctx) {
        String variableName = ctx.getChild(0).getText();
        Optional<Variable> variableOptional = scope.getVariable(variableName);
        if (!variableOptional.isPresent())
            throw new InterpreterException(InterpreterException.Type.UNEXPECTED_TOKEN, "UNKNOWN IDENTIFIER");
        String indexString = visit(ctx.getChild(2));
        if (TypeResolver.resolveType(indexString).equals(Boolean.class))
            throw new InterpreterException(InterpreterException.Type.ILLEGAL_ARGUMENT_TYPE, "INDEX MUST BE INTEGER");
        if (!(variableOptional.get() instanceof DoubleArrayVariable))
            throw new InterpreterException(InterpreterException.Type.UNEXPECTED_TOKEN, "VARIABLE MUST BE DOUBLE ARRAY");
        List<List> list = (List<List>)variableOptional.get().getValue();
        if (Integer.valueOf(indexString) >= list.size() || Integer.valueOf(indexString) < 0)
            throw new InterpreterException(InterpreterException.Type.ILLEGAL_ARGUMENT_TYPE, "ARRAY INDEX OUT OF BOUND");
        String secondIndexString = visit(ctx.getChild(4));
        if (TypeResolver.resolveType(secondIndexString).equals(Boolean.class))
            throw new InterpreterException(InterpreterException.Type.ILLEGAL_ARGUMENT_TYPE, "INDEX MUST BE INTEGER");
        if (Integer.valueOf(secondIndexString) >= list.get(Integer.valueOf(indexString)).size()
                || Integer.valueOf(secondIndexString) < 0)
            throw new InterpreterException(InterpreterException.Type.ILLEGAL_ARGUMENT_TYPE, "ARRAY INDEX OUT OF BOUND");
        return list.get(Integer.valueOf(indexString)).get(Integer.valueOf(secondIndexString)).toString();
    }

    @Override
    public String visitArraySize(RobotParser.ArraySizeContext ctx) {
        String variableName = ctx.getChild(1).getText();
        Optional<Variable> variableOptional = scope.getVariable(variableName);
        if (!variableOptional.isPresent())
            throw new InterpreterException(InterpreterException.Type.UNEXPECTED_TOKEN, "UNKNOWN IDENTIFIER");
        if (!(variableOptional.get() instanceof ArrayVariable))
            throw new InterpreterException(InterpreterException.Type.ILLEGAL_OPERAND_TYPE, "VARIABLE MUST BE ARRAYVARIABLE");
        return String.valueOf(((List)variableOptional.get().getValue()).size());
    }

    @Override
    public String visitIfExpr(RobotParser.IfExprContext ctx) {
        String value = visit(ctx.getChild(2));
        if (TypeResolver.resolveType(value).equals(Boolean.class)){
            if (Boolean.valueOf(value)){
                this.scope = new Scope(this.scope);
                String res = visit(ctx.getChild(4));
                this.scope = scope.getParent();
                return res;
            }
            return null;
        }
        throw new InterpreterException(InterpreterException.Type.ILLEGAL_ARGUMENT_TYPE, "IF EXPRESSION MUST BE BOOLEAN");
    }

    @Override
    public String visitLiteral(RobotParser.LiteralContext ctx) {
        return ctx.getText();
    }
}
