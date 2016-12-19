package utils;

import generated.RobotParser;
import intr.MyVisitor;
import robot.Robot;
import robot.commands.impl.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;

/**
 * @author nivanov
 * on 06.12.16.
 */
public class LanguageFunction {
    public static LanguageFunction main = new LanguageFunction("main", Collections.emptyList(), Collections.emptyList(), null);
    static LanguageFunction printInt = new LanguageFunction("printInt",
            Collections.singletonList(new FunctionArgument("value", Integer.class, 0)) , Collections.emptyList(),
            (function) -> System.out.println(function.getScope().getVariable("value").get()), null);
    static LanguageFunction square = new LanguageFunction("sqr",
            Collections.singletonList(new FunctionArgument("value", Integer.class, 0)),
            Collections.singletonList(new FunctionArgument("result", Integer.class, 0)),
            (function -> {
                Integer value = Integer.valueOf(function.scope.getVariable("value").get().getValue().toString());
                Variable result = function.scope.getVariable("result").get();
                result.setValue(value * value);
            }), null);

    static LanguageFunction forw = new LanguageFunction("FORW",
            Collections.emptyList(),
            Collections.singletonList(new FunctionArgument("result", Boolean.class, false)),
            (function -> function.scope.getVariable("result").get().setValue(Robot.getInstance().performCommand(new MoveUpCommand()))), null);

    static LanguageFunction back = new LanguageFunction("BACK",
            Collections.emptyList(),
            Collections.singletonList(new FunctionArgument("result", Boolean.class, false)),
            (function -> function.scope.getVariable("result").get().setValue(Robot.getInstance().performCommand(new MoveDownCommand()))), null);

    static LanguageFunction right = new LanguageFunction("RIGHT",
            Collections.emptyList(),
            Collections.singletonList(new FunctionArgument("result", Boolean.class, false)),
            (function -> function.scope.getVariable("result").get().setValue(Robot.getInstance().performCommand(new MoveRightCommand()))), null);

    static LanguageFunction left = new LanguageFunction("LEFT",
            Collections.emptyList(),
            Collections.singletonList(new FunctionArgument("result", Boolean.class, false)),
            (function -> function.scope.getVariable("result").get().setValue(Robot.getInstance().performCommand(new MoveLeftCommand()))), null);

    static LanguageFunction getf = new LanguageFunction("GETF",
            Collections.emptyList(),
            Collections.singletonList(new FunctionArgument("result", Integer.class, false)),
            (function -> function.scope.getVariable("result").get().setValue(Robot.getInstance().getF())), null);

    static LanguageFunction getb = new LanguageFunction("GETB",
            Collections.emptyList(),
            Collections.singletonList(new FunctionArgument("result", Integer.class, false)),
            (function -> function.scope.getVariable("result").get().setValue(Robot.getInstance().getB())), null);

    static LanguageFunction getr = new LanguageFunction("GETR",
            Collections.emptyList(),
            Collections.singletonList(new FunctionArgument("result", Integer.class, false)),
            (function -> function.scope.getVariable("result").get().setValue(Robot.getInstance().getR())), null);

    static LanguageFunction getl = new LanguageFunction("GETL",
            Collections.emptyList(),
            Collections.singletonList(new FunctionArgument("result", Integer.class, false)),
            (function -> function.scope.getVariable("result").get().setValue(Robot.getInstance().getL())), null);

    static LanguageFunction pushF = new LanguageFunction("PUSHF",
            Collections.emptyList(),
            Collections.singletonList(new FunctionArgument("result", Boolean.class, false)),
            (function -> function.scope.getVariable("result").get().setValue(Robot.getInstance().performCommand(new PushForwardCommand()))), null);

    static LanguageFunction pushB = new LanguageFunction("PUSHB",
            Collections.emptyList(),
            Collections.singletonList(new FunctionArgument("result", Boolean.class, false)),
            (function -> function.scope.getVariable("result").get().setValue(Robot.getInstance().performCommand(new PushBackCommand()))), null);

    static LanguageFunction pushR = new LanguageFunction("PUSHR",
            Collections.emptyList(),
            Collections.singletonList(new FunctionArgument("result", Boolean.class, false)),
            (function -> function.scope.getVariable("result").get().setValue(Robot.getInstance().performCommand(new PushRightCommand()))), null);

    static LanguageFunction pushL = new LanguageFunction("PUSHL",
            Collections.emptyList(),
            Collections.singletonList(new FunctionArgument("result", Boolean.class, false)),
            (function -> function.scope.getVariable("result").get().setValue(Robot.getInstance().performCommand(new PushLeftCommand()))), null);

    static LanguageFunction undo = new LanguageFunction("UNDO",
            Collections.emptyList(),
            Collections.singletonList(new FunctionArgument("result", Boolean.class, false)),
            (function -> function.scope.getVariable("result").get().setValue(Robot.getInstance().undoLastCommand())), null);


    private String name;
    private Integer currentLine = 0;
    private List<FunctionArgument> arguments = new ArrayList<>();
    private List<FunctionArgument> returnValues = new ArrayList<>();
    private Scope scope;
    private Consumer<LanguageFunction> doInCode;
    private List<RobotParser.SentenseContext> context;

    /**
     * Создает функцию с именем, указанием имени и набора аргументов. Аргумент с повторяющимся именем добавлен не будет.
     * @param name имя функции
     * @param args список аргументов
     */
    public LanguageFunction(String name, List<FunctionArgument> args, List<FunctionArgument> returnValues,
                            List<RobotParser.SentenseContext> context){
        this.name = name;
        this.context = context;
        args.forEach( argument -> {
            if (!arguments.contains(argument))
                arguments.add(argument);
            else
                throw new InterpreterException(InterpreterException.Type.IDENTIFIER_ALREADY_EXISTS);
        });
        returnValues.forEach( argument -> {
            if (!this.returnValues.contains(argument) && !arguments.contains(argument))
                this.returnValues.add(argument);
            else
                throw new InterpreterException(InterpreterException.Type.IDENTIFIER_ALREADY_EXISTS);
        });
    }

    private LanguageFunction(String name, List<FunctionArgument> arguments, List<FunctionArgument> returnValues,
                             Consumer<LanguageFunction> doInCode, List<RobotParser.SentenseContext> context){
        this(name, arguments, returnValues, context);
        this.doInCode = doInCode;
    }

    /**
     * Вызов функции с указанными параметрами. Переменные, значения которых используются в качестве аргументов,
     * будут скопированы. Модификатор константы не будет распространяться на новые локальные переменные
     * @param variables список параметров
     * @param outVariables список возвращаемых значений
     */
    public void invoke(MyVisitor visitor, List<Variable> variables, List<Variable> outVariables){
        if (variables.size() != arguments.size() && outVariables.size() != returnValues.size())
            throw new InterpreterException(InterpreterException.Type.ILLEGAL_ARGUMENT_TYPE);
        Variable[] args = new Variable[variables.size()];
        for (int i = 0; i < variables.size(); i++){
            FunctionArgument currArg = arguments.get(i);
            Variable currVar = variables.get(i);
            if (currVar != null) {
                if (currVar.getType() == currArg.getType())
                    createVariable(args, i, currArg, currVar);
                else
                    throw new InterpreterException(InterpreterException.Type.ILLEGAL_ARGUMENT_TYPE);
            }else
                args[i] = new Variable(currArg.getName(), currArg.getType(),
                        currArg.getDefaultValue(), false);
        }

        Variable[] returnVals = new Variable[outVariables.size()];
        for (int i = 0; i < outVariables.size(); i++){
            FunctionArgument currArg = returnValues.get(i);
            Variable currVar = outVariables.get(i);
            if (currVar != null){
                if (currVar.getType() == currArg.getType()){
                    createReturnValue(returnVals, i, currArg, currVar);
                }
                else
                    throw new InterpreterException(InterpreterException.Type.ILLEGAL_ARGUMENT_TYPE);
            }
            else{
                if (currArg instanceof ArrayFunctionArgument)
                    returnVals[i] = new ArrayVariable(currArg.getName(), currArg.getType(), (List) currArg.getDefaultValue(), false);
                else
                    returnVals[i] = new Variable(currArg.getName(), currArg.getType(), currArg.getDefaultValue(), false);
            }
        }
        scope.addVariables(args);
        scope.addVariables(returnVals);
        if (context != null){
            context.forEach(visitor::visit);
        }
        if (doInCode != null)
            doInCode.accept(this);
        for (int i = 0; i < returnVals.length; i++){
            if (outVariables.get(i) != null)
                outVariables.get(i).setValue(returnVals[i].getValue());
        }
    }

    private void createReturnValue(Variable[] returnVals, int i, FunctionArgument currArg, Variable currVar) {
        if (currVar instanceof ArrayVariable && currArg instanceof ArrayFunctionArgument){
            returnVals[i] = new ArrayVariable(currArg.getName(), currArg.getType(), (List) currArg.getDefaultValue(), false);

        }else{
            if (currVar instanceof ArrayVariable || currArg instanceof ArrayFunctionArgument)
                throw new InterpreterException(InterpreterException.Type.ILLEGAL_ARGUMENT_TYPE);

            else
                returnVals[i] = new Variable(currArg.getName(), currArg.getType(), currArg.getDefaultValue(), false);
        }
    }

    private void createVariable(Variable[] returnVals, int i, FunctionArgument currArg, Variable currVar) {
        if (currVar instanceof ArrayVariable && currArg instanceof ArrayFunctionArgument){
            returnVals[i] = new ArrayVariable(currArg.getName(), currArg.getType(), (List) currVar.getValue(), false);

        }else{
            if (currVar instanceof ArrayVariable || currArg instanceof ArrayFunctionArgument)
                throw new InterpreterException(InterpreterException.Type.ILLEGAL_ARGUMENT_TYPE);

            else
                returnVals[i] = new Variable(currArg.getName(), currArg.getType(), currVar.getValue(), false);
        }
    }

    void createScope(){
        this.scope = new Scope(null);
    }

    public void setCurrentLine(Integer currentLine) {
        this.currentLine = currentLine;
    }

    public String getName() {
        return name;
    }

    public Integer getCurrentLine() {
        return currentLine;
    }

    public Scope getScope() {
        return scope;
    }

    public List<FunctionArgument> getArguments() {
        return arguments;
    }

    public List<FunctionArgument> getReturnValues() {
        return returnValues;
    }
}
