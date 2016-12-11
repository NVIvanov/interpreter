package utils

/**
 * @author nivanov
 * on 06.12.16.
 */
class Stack {
    static private Stack instance
    private final Map<LanguageFunction, Object> functionDefinitions = new HashMap<>()
    private final Deque<LanguageFunction> current = new ArrayDeque<>()

    private Stack(){}

    /**
     * Проверяет существование функции с указанным именем
     * @param name имя функции
     * @return true, если функция существует.
     */
    boolean functionWithSuchNameExists(name){
        functionDefinitions.entrySet().stream().filter{ entry ->
            (entry.getKey().name == name)
        }.findAny().isPresent()
    }

    /**
     * Проверяет существование функции с указанными именем и набором аргументов
     * @param name имя функции
     * @param types список аргументов
     * @return найденную функцию
     */
    Optional<LanguageFunction> functionWithSuchArgumentsExists(name, List<Class> types){
        functionDefinitions.entrySet().stream().filter{ entry ->
            (entry.getKey().name == name)
        }.filter{ entry ->
            def function = entry.getKey()
            if (function.arguments.size() != types.size())
                return false
            for (int i = 0; i <= function.arguments.size(); i++)
                if (function.arguments.get(i).getClass() != types.get(i))
                    return false
        }.map{entry -> entry.getKey()}.findAny()
    }

    LanguageFunction currentFunction(){
        current.getLast()
    }

    void removeLast(){
        current.removeLast()
    }

    void putFunction(LanguageFunction function){
        current.addLast(function)
    }

    static Stack getInstance(){
        if (instance == null)
            instance = new Stack()
        return instance
    }

    void createDefinition(function, node){
        functionDefinitions.put(function as LanguageFunction, node)
    }
}
