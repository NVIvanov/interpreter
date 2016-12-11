package utils

/**
 * @author nivanov
 * on 06.12.16.
 */
class LanguageFunction {
    String name
    Integer currentLine = 0
    List<FunctionArgument> arguments = new ArrayList<>()
    Scope scope

    /**
     * Создает функцию с именем, указанием имени и набора аргументов. Аргумент с повторяющимся именем добавлен не будет.
     * @param name имя функции
     * @param args список аргументов
     */
    LanguageFunction(name, List<FunctionArgument> args){
        this.name = name
        args.forEach{ argument ->
            if (!arguments.contains(argument))
                arguments.add(new FunctionArgument(argument.getName(), argument.getType()))
        }
    }

    /**
     * Вызов функции с указанными параметрами. Переменные, значения которых используются в качестве аргументов,
     * будут скопированы. Модификатор константы не будет распространяться на новые локальные переменные
     * @param vars список параметров
     */
    void invoke(Variable... vars){
        if (vars.length != arguments.size())
            throw new IllegalArgumentException()
        scope = new Scope(Scope.global)
        Variable[] args = new Variable[vars.length]
        for (int i = 0; i < vars.size(); i++)
            args[i] = new Variable(arguments.get(i).name, arguments.get(i).type, vars[i].value, false)
        scope.addVariables(args)
    }
}
