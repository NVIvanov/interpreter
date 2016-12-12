package utils;

import java.util.ArrayList;
import java.util.List;

/**
 * @author nivanov
 * on 06.12.16.
 */
class LanguageFunction {
    private String name;
    private Integer currentLine = 0;
    private List<FunctionArgument> arguments = new ArrayList<>();
    private Scope scope;

    /**
     * Создает функцию с именем, указанием имени и набора аргументов. Аргумент с повторяющимся именем добавлен не будет.
     * @param name имя функции
     * @param args список аргументов
     */
    LanguageFunction(String name, List<FunctionArgument> args){
        this.name = name;
        args.forEach( argument -> {
            if (!arguments.contains(argument))
                arguments.add(new FunctionArgument(argument.getName(), argument.getType()));
        });
    }

    /**
     * Вызов функции с указанными параметрами. Переменные, значения которых используются в качестве аргументов,
     * будут скопированы. Модификатор константы не будет распространяться на новые локальные переменные
     * @param vars список параметров
     */
    void invoke(Variable... vars){
        if (vars.length != arguments.size())
            throw new IllegalArgumentException();
        scope = new Scope(Scope.global);
        Variable[] args = new Variable[vars.length];
        for (int i = 0; i < vars.length; i++)
            args[i] = new Variable(arguments.get(i).getName(), arguments.get(i).getType(), vars[i].value, false);
        scope.addVariables(args);
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

}
