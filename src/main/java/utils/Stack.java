package utils;

import generated.RobotParser;

import java.util.*;

/**
 * @author nivanov
 * on 06.12.16.
 */
public class Stack {
    static private Stack instance;
    private final Map<LanguageFunction, RobotParser.SentenseContext> functionDefinitions = new HashMap<>();
    private final Deque<LanguageFunction> current = new ArrayDeque<>();

    private Stack(){}

    /**
     * Проверяет существование функции с указанным именем
     * @param name имя функции
     * @return true, если функция существует.
     */
    boolean functionWithSuchNameExists(String name){
        return functionDefinitions.entrySet().stream().anyMatch(entry -> entry.getKey().getName().equals(name));
    }

    /**
     * Проверяет существование функции с указанными именем и набором аргументов
     * @param name имя функции
     * @param types список аргументов
     * @return найденную функцию
     */
    Optional<LanguageFunction> functionWithSuchArgumentsExists(String name, List<Class> types){
        return functionDefinitions.entrySet().stream().filter(entry -> entry.getKey().getName().equals(name))
                .filter(entry -> {
            LanguageFunction function = entry.getKey();
            if (function.getArguments().size() != types.size())
                return false;
            for (int i = 0; i <= function.getArguments().size(); i++)
                if (function.getArguments().get(i).getClass() != types.get(i))
                    return false;
            return true;
        }).map(Map.Entry::getKey).findAny();
    }

    LanguageFunction currentFunction(){
        return current.getLast();
    }

    void removeLast(){
        current.removeLast();
    }

    void putFunction(LanguageFunction function){
        current.addLast(function);
    }

    static Stack getInstance(){
        if (instance == null)
            instance = new Stack();
        return instance;
    }

    void createDefinition(LanguageFunction function, RobotParser.SentenseContext node){
        functionDefinitions.put(function, node);
    }
}
