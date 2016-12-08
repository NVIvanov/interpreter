package utils

/**
 * @author nivanov
 * on 06.12.16.
 */
class Stack {
    private Stack instance
    private final Map<LanguageFunction, Object> functionDefinitions = new HashMap<>()
    private final Deque<LanguageFunction> current = new ArrayDeque<>()

    boolean functionWithSuchNameExists(name){
        functionDefinitions.entrySet().stream().filter{ entry ->
            (entry.getKey().name == name)
        }.findAny().isPresent()
    }

    boolean functionWithSuchArgumentsExists(name, List<Class> types){
        functionDefinitions.entrySet().stream().filter{ entry ->
            (entry.getKey().name == name)
        }.filter{ entry ->
            def function = entry.getKey()
            if (function.arguments.size() != types.size())
                return false
            for (int i = 0; i <= function.arguments.size(); i++)
                if (function.arguments.get(i).getClass() != types.get(i))
                    return false
        }.findAny().isPresent()
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

    Stack getInstance(){
        if (instance == null)
            instance = new Stack()
        return instance
    }

    void createDefinition(function, node){
        functionDefinitions.put(function as LanguageFunction, node)
    }
}
