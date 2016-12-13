package intr;

import generated.RobotBaseVisitor;
import generated.RobotParser;
import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.tree.ParseTree;
import utils.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

/**
 * @author nivanov
 * on 12.12.16.
 */
public class MyVisitor extends RobotBaseVisitor<String> {
    private Scope scope = Scope.global;
    private Stack stack = Stack.getInstance();

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
            if (!(variable.get() instanceof DoubleArrayVariable))
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
                    list.get(index).set(secondIndex, value.toString());
                } else if (var instanceof ArrayVariable){
                    String indexString = visit(ctx.getChild(0).getChild(2));
                    if (!(TypeResolver.resolveType(indexString).equals(Integer.class)))
                        throw new InterpreterException(InterpreterException.Type.ILLEGAL_ARGUMENT_TYPE, "INDEX MUST BE INTEGER");
                    Integer index = Integer.valueOf(indexString);
                    List list = (List) var.getValue();
                    if (index < 0 || index >= list.size())
                        throw new InterpreterException(InterpreterException.Type.ILLEGAL_ARGUMENT_TYPE, "ARRAY OUT OF BOUND");
                    list.set(index, value.toString());
                }else {
                    var.setValue(value.toString());
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
        if (varName.contains("["))
            varName = varName.split("\\[")[0];
        Optional<Variable> variable = scope.getVariable(varName);
        if (variable.isPresent()){
            if (variable.get().getType().equals(Integer.class)){
                Variable var = variable.get();
                try {
                    if (var instanceof DoubleArrayVariable){
                        int index1 = getIndexFrom(visit(ctx.getChild(1).getChild(2)));
                        int index2 = getIndexFrom(visit(ctx.getChild(1).getChild(4)));
                        ((DoubleArrayVariable) var).setValue(index1, index2,
                                String.valueOf(Integer.valueOf(((DoubleArrayVariable) var).getValue(index1, index2).toString()) + 1));
                    } else if (var instanceof ArrayVariable){
                        int index1 = getIndexFrom(visit(ctx.getChild(1).getChild(2)));
                        ((ArrayVariable) var).setValue(index1,
                                String.valueOf(Integer.valueOf(((ArrayVariable) var).getValue(index1).toString()) + 1));
                    }else
                        var.setValue(Integer.valueOf(var.getValue().toString()) + 1);
                }catch (Exception e){
                    if (e instanceof InterpreterException)
                        throw new InterpreterException(((InterpreterException) e).getType(),
                                ((InterpreterException) e).getErrorMessage());
                    else
                        throw new InterpreterException(InterpreterException.Type.ILLEGAL_OPERAND_TYPE, "WRONG VARIABLE TYPE");
                }
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
            }else if (ctx.getChild(1).getText().equals("LT")){
                Boolean result = Integer.valueOf(arg1) < Integer.valueOf(arg2);
                return String.valueOf(result);
            }
            return "false";
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
    public String visit(ParseTree tree) {
        return super.visit(tree);
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
        if (!(variableOptional.get() instanceof ArrayVariable))
            throw new InterpreterException(InterpreterException.Type.UNEXPECTED_TOKEN, "VARIABLE MUST BE ARRAY");
        int index = getIndexFrom(visit(ctx.getChild(2)));
        return ((ArrayVariable)variableOptional.get()).getValue(index).toString();
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
        if (!(variableOptional.get() instanceof DoubleArrayVariable))
            throw new InterpreterException(InterpreterException.Type.UNEXPECTED_TOKEN, "VARIABLE MUST BE DOUBLE ARRAY");
        int firstIndex = getIndexFrom(visit(ctx.getChild(2)));
        int secondIndex = getIndexFrom(visit(ctx.getChild(4)));
        return ((DoubleArrayVariable)variableOptional.get()).getValue(firstIndex, secondIndex).toString();
    }

    private Integer getIndexFrom(String stringValue){
        if (TypeResolver.resolveType(stringValue).equals(Boolean.class))
            throw new InterpreterException(InterpreterException.Type.ILLEGAL_ARGUMENT_TYPE, "INDEX MUST BE INTEGER");
        return Integer.valueOf(stringValue);
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
    public String visitDoubleArraySize(RobotParser.DoubleArraySizeContext ctx) {
        String variableName = ctx.getChild(1).getText();
        Optional<Variable> variableOptional = scope.getVariable(variableName);
        int index = getIndexFrom(visit(ctx.getChild(2)));
        if (!variableOptional.isPresent())
            throw new InterpreterException(InterpreterException.Type.UNEXPECTED_TOKEN, "UNKNOWN IDENTIFIER");
        if (!(variableOptional.get() instanceof DoubleArrayVariable))
            throw new InterpreterException(InterpreterException.Type.ILLEGAL_OPERAND_TYPE, "VARIABLE MUST BE DOUBLEARRAYVARIABLE");
        return String.valueOf(((List)((List)variableOptional.get().getValue()).get(index)).size());
    }

    @Override
    public String visitArrayExtend(RobotParser.ArrayExtendContext ctx) {
        String varName = ctx.getChild(1).getText();
        Optional<Variable> variableOptional = scope.getVariable(varName);
        if (!variableOptional.isPresent())
            throw new InterpreterException(InterpreterException.Type.UNEXPECTED_TOKEN, "UNKNOWN IDENTIFIER");
        if (!(variableOptional.get() instanceof ArrayVariable))
            throw new InterpreterException(InterpreterException.Type.ILLEGAL_OPERAND_TYPE, "VARIABLE MUST BE ARRAYVARIABLE");
        ArrayVariable arrayVariable = (ArrayVariable) variableOptional.get();
        int valueToExtend = getIndexFrom(visit(ctx.getChild(2)));
        List value = arrayVariable.getValue();
        List newValue = new ArrayList();
        value.forEach(newValue::add);
        for (int i = 0; i < valueToExtend; i++)
            newValue.add(arrayVariable.getType().equals(Integer.class) ? 0: false);
        arrayVariable.setValue(newValue);
        return null;
    }

    @Override
    public String visitDoubleArrayExtend(RobotParser.DoubleArrayExtendContext ctx) {
        String varName = ctx.getChild(1).getText();
        Optional<Variable> variableOptional = scope.getVariable(varName);
        if (!variableOptional.isPresent())
            throw new InterpreterException(InterpreterException.Type.UNEXPECTED_TOKEN, "UNKNOWN IDENTIFIER");
        if (!(variableOptional.get() instanceof ArrayVariable))
            throw new InterpreterException(InterpreterException.Type.ILLEGAL_OPERAND_TYPE, "VARIABLE MUST BE ARRAYVARIABLE");
        DoubleArrayVariable arrayVariable = (DoubleArrayVariable) variableOptional.get();
        int rowToExtend = getIndexFrom(visit(ctx.getChild(2)));
        int valueToExtend = getIndexFrom(visit(ctx.getChild(3)));
        List value = (List) arrayVariable.getValue(rowToExtend);
        List newValue = new ArrayList();
        value.forEach(newValue::add);
        for (int i = 0; i < valueToExtend; i++)
            newValue.add(arrayVariable.getType().equals(Integer.class) ? 0: false);
        arrayVariable.setValue(rowToExtend, newValue);
        return null;
    }

    @Override
    public String visitProcedure(RobotParser.ProcedureContext ctx) {
        invokeFunction(ctx, 0, Collections.emptyList(), Collections.emptyList());
        return null;
    }

    @Override
    public String visitOneReturnFunction(RobotParser.OneReturnFunctionContext ctx) {
        String varName = ctx.getChild(0).getText();
        Optional<Variable> variableOptional = scope.getVariable(varName);
        if (!variableOptional.isPresent())
            throw new InterpreterException(InterpreterException.Type.IDENTIFIER_NOT_FOUND, "undefined variable");
        invokeFunction(ctx, 2, Collections.singletonList(variableOptional.get()),
                Collections.singletonList(variableOptional.get().getType()));
        return null;
    }

    @Override
    public String visitSeveralReturnFunction(RobotParser.SeveralReturnFunctionContext ctx) {
        List<Variable> returns = new ArrayList<>();
        List<Class> returnClasses = new ArrayList<>();
        int i = 1;
        while (!(ctx.getChild(i).getText().equals("]") || ctx.getChild(i).getText().equals("="))){
            while (ctx.getChild(i).getText().equals(",")){
                i++;
                returns.add(null);
            }
            if (ctx.getChild(i).getText().equals("]"))
                break;
            String value = ctx.getChild(i).getText();
            Optional<Variable> variableOptional = scope.getVariable(value);
            if (!variableOptional.isPresent())
                throw new InterpreterException(InterpreterException.Type.IDENTIFIER_NOT_FOUND, "undefined variable");
            Class type = variableOptional.get().getType();
            returnClasses.add(type);
            returns.add(variableOptional.get());
            i+=2;
        }
        Integer funcNameIndex = ctx.getChild(i).getText().equals("=") ? i + 1 : i + 2;
        invokeFunction(ctx, funcNameIndex, returns, returnClasses);
        return null;
    }

    private void invokeFunction(ParseTree ctx, Integer funcNameIndex, List<Variable> returns, List<Class> returnClasses){
        List<Variable> args = new ArrayList<>();
        List<Class> classes = new ArrayList<>();
        String funcName = ctx.getChild(funcNameIndex).getText();
        if (!stack.functionWithSuchNameExists(funcName))
            throw new InterpreterException(InterpreterException.Type.IDENTIFIER_NOT_FOUND, "function not defined");
        for (int i = funcNameIndex + 2; i < ctx.getChildCount() - 1; i+=2){
            while (ctx.getChild(i).getText().equals(",")){
                args.add(null);
                classes.add(null);
                i++;
            }
            if (i >= ctx.getChildCount() - 1)
                break;
            String value = visit(ctx.getChild(i));
            Class type;
            Variable var;
            if (value.contains("[")){
                type = TypeResolver.resolveArrayTypeByValue(value);
                List list = TypeResolver.parseArrayInit(value);
                var = new ArrayVariable(String.valueOf(i), type, list, false);
            }else{
                type = TypeResolver.resolveType(value);
                var = new Variable(String.valueOf(i), type, value, false);
            }
            classes.add(type);
            args.add(var);
        }
        Optional<LanguageFunction> function = stack.functionWithSuchArgumentsExists(funcName, classes, returnClasses);
        if (function.isPresent()){
            stack.putFunction(function.get());
            Scope current = scope;
            scope = function.get().getScope();
            function.get().invoke(this, args, returns);
            stack.removeLast();
            scope = current;
        }
        else
            throw new InterpreterException(InterpreterException.Type.ILLEGAL_ARGUMENT_TYPE,
                    "no function with such arg types");
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
    public String visitFunctionDeclaration(RobotParser.FunctionDeclarationContext ctx) {
        List<FunctionArgument> returnArgList = createReturnArgs(ctx.getChild(0));
        String functionName = ctx.getChild(2).getText();
        List<FunctionArgument> argumentList = createArgs(ctx.getChild(4));
        RobotParser.SentenseContext sentenseContext = (RobotParser.SentenseContext) ctx.getChild(7);
        LanguageFunction function = new LanguageFunction(functionName, argumentList, returnArgList, sentenseContext);
        stack.createDefinition(function);
        return null;
    }

    private List<FunctionArgument> createReturnArgs(ParseTree ctx){
        List<FunctionArgument> argumentList = new ArrayList<>();
        for (int i = 0; i < ctx.getChildCount(); i++){
            if (ctx.getChild(i) instanceof RobotParser.ReturnValueContext)
                argumentList.add(createArgument(ctx.getChild(i)));
        }
        return argumentList;
    }

    private List<FunctionArgument> createArgs(ParseTree ctx){
        List<FunctionArgument> argumentList = new ArrayList<>();
        for (int i = 0; i < ctx.getChildCount(); i++){
            if (ctx.getChild(i) instanceof RobotParser.ReturnValueContext ||
                    ctx.getChild(i) instanceof RobotParser.ArgumentContext)
                argumentList.add(createArgument(ctx.getChild(i).getChild(0)));
        }
        return argumentList;
    }

    private FunctionArgument createArgument(ParseTree ctx){
        String name = ctx.getChild(0).getText();
        String value = ctx.getChild(2).getText();
        Class type;
        Object valueObj;
        if (value.contains("[")){
            type = TypeResolver.resolveArrayTypeByValue(value);
            List list = new ArrayList();
            if (ctx.getChild(2).getChildCount() > 2){
                for (int i = 1; i < ctx.getChild(2).getChildCount(); i+=2){
                    String arrayValue = visit(ctx.getChild(2).getChild(i));
                    Class valueClass = TypeResolver.resolveType(arrayValue);
                    if (!valueClass.equals(type))
                        throw new InterpreterException(InterpreterException.Type.ILLEGAL_ARGUMENT_TYPE, "INIT VALUES CLASSES MUST EQUAL");
                    list.add(TypeResolver.getValue(arrayValue));
                }
            }
            valueObj = list;
            return new ArrayFunctionArgument(name, type, valueObj);
        }else{
            type = TypeResolver.resolveType(value);
            valueObj = value;
            return new FunctionArgument(name, type, valueObj);
        }
    }

    @Override
    public String visitLiteral(RobotParser.LiteralContext ctx) {
        return ctx.getText();
    }

    @Override
    public String visitMain(RobotParser.MainContext ctx) {
        String ret = super.visitMain(ctx);
        System.out.println(scope);
        return ret;
    }
}
