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

    void invoke(Variable... vars){
        scope = new Scope(Scope.global)
        scope.addVariables(vars)
    }
}
